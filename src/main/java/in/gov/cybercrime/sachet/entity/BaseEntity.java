package in.gov.cybercrime.sachet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, updatable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = true, updatable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = true)
    private String updatedBy;

    @Column(nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) this.isActive = true;
        String actor = getCurrentActor();
        if (this.createdBy == null) this.createdBy = actor;
        if (this.updatedBy == null) this.updatedBy = actor;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        if (this.updatedBy == null) this.updatedBy = getCurrentActor();
    }

    private String getCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }
}
