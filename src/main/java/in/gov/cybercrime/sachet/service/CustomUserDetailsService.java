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

    /*
    This method is used by Spring Security internally.
    "username" is the login input, in your case phone number.
    */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Fetch user using phone number
        User user = userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Block login if soft deleted / inactive
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new DisabledException("User inactive: " + username);
        }
        if (Boolean.FALSE.equals(user.getIsApproved())) {
            throw new LockedException("User not approved: " + username);
        }

        // If role is missing, authentication must fail (prevents NullPointerException)
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new UsernameNotFoundException("User role missing: " + username);
        }

        // Get role name from RoleMaster table
        String roleName = user.getRole().getRoleName();

        // Normalize role format for Spring Security
        // SuperAdmin -> SUPERADMIN
        // Admin      -> ADMIN
        // Staff      -> STAFF
        roleName = roleName.trim().toUpperCase();

        // Return Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),                         // username
                user.getPasswordHash(),                  // password
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName)) // authorities
        );
    }
}
