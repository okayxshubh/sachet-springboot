package in.gov.cybercrime.sachet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id", nullable = false)
    private RankMaster rank;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ps_id", nullable = false)
    private PoliceStationMaster ps;

    @Column(name = "phone", unique = true)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleMaster role;

    // BCrypt hashed password
    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = true;
}
