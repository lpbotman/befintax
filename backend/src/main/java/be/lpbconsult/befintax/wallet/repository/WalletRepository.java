package be.lpbconsult.befintax.wallet.repository;

import be.lpbconsult.befintax.account.entity.UserEntity;
import be.lpbconsult.befintax.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    List<WalletEntity> findByUser(UserEntity currentUser);

    Optional<WalletEntity> findByIdAndUser(Long walletId, UserEntity currentUser);
}
