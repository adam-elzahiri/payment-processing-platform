package io.github.adam_elzahiri.payment_processing_platform.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
               SET u.passwordHash = :newPasswordHash
             WHERE u.id = :userId
               AND u.passwordHash = :expectedPasswordHash
            """)
    void updatePasswordHashIfUnchanged(
            @Param("userId") UUID userId,
            @Param("expectedPasswordHash") String expectedPasswordHash,
            @Param("newPasswordHash") String newPasswordHash
    );

}
