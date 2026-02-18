package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.StatusUpdateRequest;
import in.gov.cybercrime.sachet.dto.UserCreateRequest;
import in.gov.cybercrime.sachet.dto.UserResponse;
import in.gov.cybercrime.sachet.dto.UserUpdateRequest;
import in.gov.cybercrime.sachet.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public GenericResponse<List<UserResponse>> listUsers(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive) {
        return GenericResponse.ok(userService.getUsers(includeInactive));
    }

    @PostMapping
    public GenericResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        return GenericResponse.ok(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public GenericResponse<UserResponse> getUser(@PathVariable Long id) {
        return GenericResponse.ok(userService.getUser(id));
    }

    @PutMapping("/{id}")
    public GenericResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return GenericResponse.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    public GenericResponse<UserResponse> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        return GenericResponse.ok(userService.updateStatus(id, Boolean.TRUE.equals(request.getIsActive())));
    }
}
