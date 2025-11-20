package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.RegisterRequestDto;
import qnt.moviebooking.dto.request.UserRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.entity.RoleEntity;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("Bạn chưa đăng nhập!");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng!"));
    }

    public UserResourceDto getPublicUser(String email) {
        UserEntity currentUser = (email == null)
                ? getCurrentUser()
                : getUserByEmailNoDelete(email);

        return mapToDto(currentUser);
    }

    public UserResourceDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + id));
        return mapToDto(user);
    }

    // Tìm user chưa bị xoá hoặc bị xoá (Soft Delete)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    // Tìm user chưa bị xoá
    public UserEntity getUserByEmailNoDelete(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    public boolean isUserActive(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).map(UserEntity::getIsActive).orElse(false);
    }

    // Kiểm tra có tồn tại email này chưa kể cả bị xoá
    public boolean isExistedUser(String email) {
        return userRepository.existsByEmail(email);
    }

    // Kiểm tra có tồn cái email này chưa (chỉ lấy nhưng user chưa bị xoá)
    public boolean isExistedUserNoDelete(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    // Kiểm tra có tồn tại email này chưa (chỉ lấy user đã xoá)
    public boolean isExistedUserDeleted(String email) {
        return userRepository.existsByEmailAndDeletedAtNotNull(email);
    }

    public void activateUser(String email, String code) {
        UserEntity user = getUserByEmailNoDelete(email);

        if (user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã xác thực!");
        }

        if (!code.trim().equals(user.getCode())) {
            throw new RuntimeException("Mã xác thực không hợp lệ!");
        }

        if (user.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn (2 phút). Vui lòng yêu cầu mã mới.");
        }

        user.setIsActive(true);
        user.setCode(null);
        user.setCodeExpiresAt(null);
        userRepository.save(user);
    }

    public List<UserResourceDto> getAllUserNoDelete() {
        return userRepository.findAllUserAndDeleteAtIsNull()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserResourceDto createUser(RegisterRequestDto request, RoleEntity role, String code,
            LocalDateTime expiresAt) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không trùng nhau!");
        }

        if (isExistedUserNoDelete(request.getEmail())) {
            throw new RuntimeException("Email đã được đăng ký!");
        }

        if (isExistedUserDeleted(request.getEmail())) {
            UserEntity user = getUserByEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setCode(code);
            user.setRole(role);
            user.setCodeExpiresAt(expiresAt);
            user.setDeletedAt(null);
            user.setIsActive(false);
            userRepository.save(user);

            return mapToDto(user);
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .code(code)
                .codeExpiresAt(expiresAt)
                .build();

        userRepository.save(user);

        return mapToDto(user);
    }

    public UserResourceDto updateUser(String email, UserRequestDto request) {
        UserEntity user = getCurrentUser();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không trùng nhau!");
        }

        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return mapToDto(user);
    }

    public void deleteUser(String email) {
        if (!isExistedUserNoDelete(email)) {
            throw new RuntimeException("Không tìm thấy người dùng với email: " + email);
        }

        UserEntity user = getUserByEmailNoDelete(email);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserEntity updateUserForce(UserEntity user) {
        return userRepository.save(user);
    }

    public UserResourceDto mapToDto(UserEntity userEntity) {
        return UserResourceDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .roleName(userEntity.getRole().getRoleName())
                .createAt(userEntity.getCreatedAt())
                .build();
    }
}
