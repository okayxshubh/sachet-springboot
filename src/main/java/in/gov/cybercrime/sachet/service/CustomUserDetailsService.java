package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("User inactive: " + username);
        }

        String roleName = user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName))
        );
    }
}
