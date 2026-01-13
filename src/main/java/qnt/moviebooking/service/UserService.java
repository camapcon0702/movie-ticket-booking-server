package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.RegisterRequestDto;
import qnt.moviebooking.dto.request.UserRequestDto;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.entity.RoleEntity;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.exception.BadRequestException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity getCurrentUser() {
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

    public UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + id));
    }

    public UserResourceDto getUserById(Long id) {
        UserEntity user = getUserEntityById(id);
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

    // Kiểm tra có tồn cái email này chưa (chỉ lấy nhưng user chưa bị xoá)
    public boolean isExistedUserNoDelete(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    // Kiểm tra có tồn tại email này chưa (chỉ lấy user đã xoá)
    public boolean isExistedUserDeleted(String email) {
        return userRepository.existsByEmailAndDeletedAtNotNull(email);
    }

    @Transactional
    public void activateUser(String email, String code) {
        UserEntity user = getUserByEmailNoDelete(email);

        if (user.getIsActive()) {
            throw new NotFoundException("Tài khoản đã xác thực!");
        }

        if (!code.trim().equals(user.getCode())) {
            throw new NotFoundException("Mã xác thực không hợp lệ!");
        }

        if (user.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("Mã xác thực đã hết hạn (2 phút). Vui lòng yêu cầu mã mới.");
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

    @Transactional
    public UserResourceDto createUser(RegisterRequestDto request, RoleEntity role, String code,
            LocalDateTime expiresAt) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new NotFoundException("Mật khẩu không trùng nhau!");
        }

        if (isExistedUserNoDelete(request.getEmail())) {
            throw new NotFoundException("Email đã được đăng ký!");
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

    @Transactional
    public UserResourceDto updateUser(UserRequestDto request) {
        UserEntity user = getCurrentUser();

        user.setFullName(request.getFullName());

        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (password != null || confirmPassword != null) {

            if (password == null || confirmPassword == null) {
                throw new BadRequestException("Vui lòng nhập đầy đủ mật khẩu và xác nhận mật khẩu ");
            }

            if (password.isBlank()) {
                throw new BadRequestException("Mật khẩu không được để trống");
            }

            if (!password.equals(confirmPassword)) {
                throw new BadRequestException("Mật khẩu không trùng nhau");
            }

            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);

        return mapToDto(user);
    }

    @Transactional
    public void deleteUser(String email) {
        if (!isExistedUserNoDelete(email)) {
            throw new NotFoundException("Không tìm thấy người dùng với email: " + email);
        }

        UserEntity user = getUserByEmailNoDelete(email);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void rollbackDeletedUser() {
        List<UserEntity> users = userRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (users.isEmpty()) {
            throw new NotFoundException("Không có tài khoản nào để khôi phục!");
        }

        for (UserEntity user : users) {
            user.setDeletedAt(null);
            userRepository.save(user);
        }
    }

    @Transactional
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
