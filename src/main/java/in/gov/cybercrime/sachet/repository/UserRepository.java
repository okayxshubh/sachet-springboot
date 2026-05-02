package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByIsApprovedFalse();
    List<User> findByIsApprovedTrue();
    boolean existsByPhoneAndIsApprovedFalse(String phone);
    boolean existsByPhoneAndIsApprovedTrue(String phone);

    List<User> findByRankIdAndIsApprovedTrueAndIsActiveTrueAndPs_District_IdAndPs_Id(
            Long rankId,
            Long districtId,
            Long psId
    );

}
