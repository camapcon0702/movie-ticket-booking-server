package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.LoginRequestDto;
import qnt.moviebooking.dto.request.RegisterRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.entity.RoleEntity;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.util.JwtUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final EmailService emailService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final int EXPIRATION_MINUTES = 2;

    public Map<String, Object> login(LoginRequestDto request) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserResourceDto user = userService.getPublicUser(request.getEmail());
            String token = jwtUtil.generateToken(request.getEmail(), user.getRoleName());

            return Map.of(
                    "token", token,
                    "user", user);
        } catch (Exception e) {
            throw new NotFoundException("Email hoặc mật khẩu không đúng!");
        }
    }

    private String generateVerificationCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void sendVerificationEmail(String email, String code) {
        String subject = "Mã xác thực tài khoản:";
        String body = "Xin chào, \n\nMã kích hoạt tài khoản của bạn là: **" + code + "**\n\n"
                + "Vui lòng nhập mã này vào trang xác thực trong ứng dụng.";

        emailService.sendEmail(email, subject, body);
    }

    @Transactional
    public UserResourceDto register(RegisterRequestDto request) {

        if (userService.isExistedUserNoDelete(request.getEmail())) {
            throw new ExistException("Email đã được đăng ký!");
        }

        RoleEntity defaultRole = roleService.getRoleEntityByName("USER");

        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        UserResourceDto savedUser = userService.createUser(request, defaultRole, code, expiresAt);

        sendVerificationEmail(request.getEmail(), code);

        return savedUser;
    }

    @Transactional
    public void resendVerificationCode(String email) {
        UserEntity user = userService.getUserByEmail(email);

        if (user.getIsActive()) {
            throw new NotFoundException("Tài khoản đã được kích hoạt.");
        }

        String newCode = generateVerificationCode();
        LocalDateTime newExpiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        user.setCode(newCode);
        user.setCodeExpiresAt(newExpiryDate);
        userService.updateUserForce(user);

        sendVerificationEmail(user.getEmail(), newCode);
    }
}