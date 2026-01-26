package be.lpbconsult.befintax.account.repository;

import be.lpbconsult.befintax.account.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByKeycloakId(String keycloakId);
}
