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
    public GenericResponse<String> getActiveUsersByRank(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        RankIdRequest request =
                objectMapper.readValue(json, RankIdRequest.class);

        List<UserResponse> users =
                userService.getActiveUsersByRank(request.getRankId());

        String jsonList = objectMapper.writeValueAsString(users);
        String encryptedData = SachetCrypto.encrypt(jsonList);

        return GenericResponse.<String>builder()
                .status("OK")
                .message("Users fetched successfully")
                .data(encryptedData)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    @PostMapping("/get")
    public GenericResponse<String> getUser(@RequestBody String encryptedBody) throws Exception {

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
    }

    @PostMapping("/create")
    public GenericResponse<String> createUser(@RequestBody String encryptedBody) throws Exception {

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
    }

    @PutMapping("/update")
    public GenericResponse<String> updateUser(@RequestBody String encryptedBody) throws Exception {

        if (encryptedBody == null || encryptedBody.isBlank()) {
            throw new IllegalArgumentException("Encrypted body is required");
        }

        String json = SachetCrypto.decrypt(encryptedBody.trim());

        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Decryption failed");
        }

        UserUpdateWrapper wrapper =
                objectMapper.readValue(json, UserUpdateWrapper.class);

        if (wrapper.getId() == null || wrapper.getRequest() == null) {
            throw new IllegalArgumentException("Invalid request structure");
        }

        UserResponse updated =
                userService.updateUser(wrapper.getId(), wrapper.getRequest());

        String jsonResponse = objectMapper.writeValueAsString(updated);
        String encryptedData = SachetCrypto.encrypt(jsonResponse);

        return GenericResponse.ok("User updated successfully", encryptedData);
    }

    @PostMapping("/delete")
    public GenericResponse<String> deleteUser(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        DeleteUserRequest request =
                objectMapper.readValue(json, DeleteUserRequest.class);

        userService.deleteUser(request.getId(), request.getUpdatedBy());

        String encryptedData =
                SachetCrypto.encrypt("User deactivated successfully");

        return GenericResponse.<String>builder()
                .status("OK")
                .message("Success")
                .data(encryptedData)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    public static class UserUpdateWrapper {
        private Long id;
        private UserUpdateRequest request;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public UserUpdateRequest getRequest() { return request; }
        public void setRequest(UserUpdateRequest request) { this.request = request; }
    }
}