package qnt.moviebooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.UserRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.service.UserService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<UserResourceDto>>> getAllUser() {
        try {
            List<UserResourceDto> users = userService.getAllUserNoDelete();

            return ResponseEntity.ok(new ApiResponse<>(true, "Danh sách tài khoản!", users));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi tạo lấy danh sách", null));
        }
    }

    @DeleteMapping("/admin/users")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestParam String email) {
        try {
            userService.deleteUser(email);

            return ResponseEntity.ok(new ApiResponse<>(true, "Xoá tài khoản thành công!", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi xoá tài khoản", null));
        }
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<ApiResponse<UserResourceDto>> getUserById(@PathVariable Long id) {
        try {
            UserResourceDto user = userService.getUserById(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Thông tin tài khoản!", user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi tìm tài khoản", null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResourceDto>> getPublicUser() {
        try {
            UserResourceDto user = userService.getPublicUser(null);

            return ResponseEntity.ok(new ApiResponse<>(true, "Thông tin tài khoản!", user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi tìm tài khoản", null));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResourceDto>> updateUser(@RequestParam String email,
            @RequestBody UserRequestDto request) {
        try {
            UserResourceDto user = userService.updateUser(email, request);

            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật tài khoản thành công!", user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi cập nhật tài khoản", null));
        }
    }

}
