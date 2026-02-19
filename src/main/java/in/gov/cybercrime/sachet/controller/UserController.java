package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.IdRequest;
import in.gov.cybercrime.sachet.dto.UserCreateRequest;
import in.gov.cybercrime.sachet.dto.UserResponse;
import in.gov.cybercrime.sachet.dto.UserUpdateRequest;
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

    // Purpose: List users (encrypted body)
    @PostMapping("/list")
    public GenericResponse<List<UserResponse>> listUsers(@RequestBody String encryptedBody) throws Exception {
        // Decrypt payload
        String json = decrypt(encryptedBody);

        // Optional includeInactive field
        boolean includeInactive = objectMapper.readTree(json)
                .path("includeInactive").asBoolean(false);

        return GenericResponse.ok(userService.getUsers(includeInactive));
    }

    // Purpose: Create user (encrypted body)
    @PostMapping("/create")
    public GenericResponse<UserResponse> createUser(@RequestBody String encryptedBody) throws Exception {
        String json = decrypt(encryptedBody);
        UserCreateRequest request = objectMapper.readValue(json, UserCreateRequest.class);
        return GenericResponse.ok(userService.createUser(request));
    }

    // Purpose: Get user by id (encrypted body)
    @PostMapping("/get")
    public GenericResponse<UserResponse> getUser(@RequestBody String encryptedBody) throws Exception {
        String json = decrypt(encryptedBody);
        IdRequest request = objectMapper.readValue(json, IdRequest.class);
        return GenericResponse.ok(userService.getUser(request.getId()));
    }

    // Purpose: Update user (encrypted body)
    @PostMapping("/update")
    public GenericResponse<UserResponse> updateUser(@RequestBody String encryptedBody) throws Exception {
        String json = decrypt(encryptedBody);
        // The incoming JSON must contain id + fields to update
        UserUpdateWrapper wrapper = objectMapper.readValue(json, UserUpdateWrapper.class);
        return GenericResponse.ok(userService.updateUser(wrapper.getId(), wrapper.getRequest()));
    }

    // Purpose: Soft delete user (encrypted body)
    @PostMapping("/delete")
    public GenericResponse<String> deleteUser(@RequestBody String encryptedBody) throws Exception {
        String json = decrypt(encryptedBody);
        IdRequest request = objectMapper.readValue(json, IdRequest.class);
        userService.deleteUser(request.getId());
        return GenericResponse.ok("User deactivated successfully");
    }

    // Purpose: Hard delete user (encrypted body)
    @PostMapping("/delete-hard")
    public GenericResponse<String> hardDeleteUser(@RequestBody String encryptedBody) throws Exception {
        String json = decrypt(encryptedBody);
        IdRequest request = objectMapper.readValue(json, IdRequest.class);
        userService.hardDeleteUser(request.getId());
        return GenericResponse.ok("User permanently deleted");
    }

    // Placeholder: actual decryption logic
    private String decrypt(String encrypted) {
        // Implement AES/RSA/etc decryption here
        // For now, assume input is plaintext JSON
        return encrypted;
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
