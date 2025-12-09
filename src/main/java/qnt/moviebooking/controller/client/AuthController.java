package qnt.moviebooking.controller.client;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.LoginRequestDto;
import qnt.moviebooking.dto.request.RegisterRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.service.AuthService;
import qnt.moviebooking.service.UserService;

@RestController
@RequestMapping("/v1.0/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequestDto request) {
        try {
            if (!userService.isExistedUserNoDelete(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Email hoặc mật khẩu không đúng!", null));
            }

            if (!userService.isUserActive(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Tài khoản chưa được kích hoạt!", null));
            }

            Map<String, Object> response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đăng nhập thành công!",
                    response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Email hoặc mật khẩu không đúng!", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResourceDto>> register(@RequestBody RegisterRequestDto request) {
        UserResourceDto registeredUser = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Đăng ký thành công!", registeredUser));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@RequestParam String email, @RequestParam String code) {
        userService.activateUser(email, code);

        return ResponseEntity
                .ok(new ApiResponse<>(true, "Kích hoạt tài khoản thành công!", null));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Void>> resendVerificationCode(@RequestParam String email) {
        authService.resendVerificationCode(email);

        return ResponseEntity.ok(new ApiResponse<>(true, "Mã xác thực mới đã được gửi đến email của bạn.", null));
    }
}
