package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/*
Purpose of this class:
Spring Security calls this service automatically during login/authentication.

It loads the user from database using phone number,
checks whether user is active/enabled,
then returns a Spring Security UserDetails object containing:
- username (phone)
- password hash
- role authority (ROLE_ADMIN / ROLE_SUPERADMIN / ROLE_STAFF)

APIs can then be protected using:
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('SUPERADMIN')")
@PreAuthorize("hasRole('STAFF')")
*/

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

        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new DisabledException("User inactive: " + username);
        }

        if (user.getIsApproved() == null || !user.getIsApproved()) {
            throw new LockedException("User not approved: " + username);
        }

        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new UsernameNotFoundException("User role missing: " + username);
        }

        String roleName = user.getRole().getRoleName().trim().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPasswordHash(),
                user.getIsActive(),      // enabled
                true,
                true,
                user.getIsApproved(),    // accountNonLocked
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName))
        );
    }
}
