package hr.algebra.hospital.repository;

import hr.algebra.hospital.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
