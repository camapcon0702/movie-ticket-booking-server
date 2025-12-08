package qnt.moviebooking.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                UserEntity existingUser = userRepository.findByEmailFetchRole(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Không tồn tại tài khoản với email: " + email));
                return User.builder()
                                .username(existingUser.getEmail())
                                .password(existingUser.getPassword())
                                .roles(existingUser.getRole().getRoleName())
                                .authorities(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole()
                                                .getRoleName()))
                                .build();
        }
}
