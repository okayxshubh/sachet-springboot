package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.IdRequest;
import in.gov.cybercrime.sachet.dto.StatusUpdateRequest;
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

    @PostMapping("/get")
    public GenericResponse<UserResponse> getUser(@RequestBody IdRequest request) {
        return GenericResponse.ok(userService.getUser(request.getId()));
    }

    @PutMapping("/update")
    public GenericResponse<UserResponse> updateUser(@RequestBody UserUpdateRequest request) {
        return GenericResponse.ok(userService.updateUser(request.getId(), request));
    }

    @PatchMapping("/status")
    public GenericResponse<UserResponse> updateStatus(@RequestBody StatusUpdateRequest request) {
        return GenericResponse.ok(userService.updateStatus(
                request.getId(),
                Boolean.TRUE.equals(request.getIsActive()),
                request.getUpdatedBy()
        ));
    }
}

