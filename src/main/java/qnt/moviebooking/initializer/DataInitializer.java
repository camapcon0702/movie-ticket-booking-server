package qnt.moviebooking.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.entity.RoleEntity;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.repository.RoleRepository;
import qnt.moviebooking.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles("ADMIN", "Quản trị hệ thống");
        initializeRoles("USER", "Người dùng hệ thống");
        initializeAdminUser();
    }

    private void initializeRoles(String roleName, String description) {
        roleRepository.findByRoleName(roleName).orElseGet(() -> {
            var role = new RoleEntity();
            role.setRoleName(roleName);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            UserEntity admin = UserEntity.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(roleRepository.findByRoleName("ADMIN").get())
                    .fullName("Administrator")
                    .isActive(true)
                    .build();

            userRepository.save(admin);
        }
    }
}
