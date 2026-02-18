package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.masters.RoleMaster;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    // BCrypt
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleMaster role;

}
