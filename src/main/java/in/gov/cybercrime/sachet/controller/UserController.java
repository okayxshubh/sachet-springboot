package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/by-rank-active")
    public GenericResponse<String> getActiveUsersByRank(
            @RequestBody String encryptedBody) {

        try {
            // 1. Decrypt incoming body
            String json = SachetCrypto.decrypt(encryptedBody);

            // 2. Convert to DTO
            RankIdRequest request =
                    objectMapper.readValue(json, RankIdRequest.class);

            // 3. Fetch data
            List<UserResponse> users =
                    userService.getActiveUsersByRank(request.getRankId());

            // 4. Serialize only the list
            String jsonList = objectMapper.writeValueAsString(users);

            // 5. Encrypt serialized list
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // 6. Return encrypted list in data key
            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("Users fetched successfully")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }


    // Purpose: Get user by id (encrypted body)
    @PostMapping("/get")
    public GenericResponse<String> getUser(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            IdRequest request = objectMapper.readValue(json, IdRequest.class);

            UserResponse user = userService.getUser(request.getId());

            String jsonResponse = objectMapper.writeValueAsString(user);

            String encryptedData = SachetCrypto.encrypt(jsonResponse);

            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("User fetched successfully")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            return GenericResponse.fail("Server error");
        }
    }


    // Purpose: Create user (encrypted body)
    @PostMapping("/create")
    public GenericResponse<String> createUser(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            UserCreateRequest request =
                    objectMapper.readValue(json, UserCreateRequest.class);

            UserResponse user = userService.createUser(request);

            String jsonResponse = objectMapper.writeValueAsString(user);

            String encryptedData = SachetCrypto.encrypt(jsonResponse);

            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("User created successfully")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            return GenericResponse.fail("Server error");
        }
    }

    // Purpose: Update user (encrypted body)
    @PostMapping("/update")
    public GenericResponse<String> updateUser(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            UserUpdateWrapper wrapper =
                    objectMapper.readValue(json, UserUpdateWrapper.class);

            UserResponse updated =
                    userService.updateUser(wrapper.getId(), wrapper.getRequest());

            String jsonResponse = objectMapper.writeValueAsString(updated);

            String encryptedData = SachetCrypto.encrypt(jsonResponse);

            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("User updated successfully")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            return GenericResponse.fail("Server error");
        }
    }

    // Purpose: Soft delete user (encrypted body)
    @PostMapping("/delete")
    public GenericResponse<String> deleteUser(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            IdRequest request = objectMapper.readValue(json, IdRequest.class);

            userService.deleteUser(request.getId());

            String encryptedData =
                    SachetCrypto.encrypt("User deactivated successfully");

            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("Success")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            return GenericResponse.fail("Server error");
        }
    }


    // Wrapper class for update request with id
    public static class UserUpdateWrapper {
        private Long id;
        private UserUpdateRequest request;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public UserUpdateRequest getRequest() { return request; }
        public void setRequest(UserUpdateRequest request) { this.request = request; }
    }
}
