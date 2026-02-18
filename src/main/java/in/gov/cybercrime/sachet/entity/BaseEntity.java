package in.gov.cybercrime.sachet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) this.isActive = true;
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }
}
