package qnt.moviebooking.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.UserRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1.0/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResourceDto>> getPublicUser() {
        UserResourceDto user = userService.getPublicUser(null);

        return ResponseEntity.ok(new ApiResponse<>(true, "Thông tin tài khoản!", user));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResourceDto>> updateUser(@RequestParam String email,
            @RequestBody UserRequestDto request) {

        UserResourceDto user = userService.updateUser(email, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật tài khoản thành công!", user));
    }
}
