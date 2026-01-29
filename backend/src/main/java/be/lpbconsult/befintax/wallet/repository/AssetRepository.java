package be.lpbconsult.befintax.wallet.repository;

import be.lpbconsult.befintax.account.entity.UserEntity;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
    List<AssetEntity> findByWalletUser(UserEntity user);

    Optional<AssetEntity> findByIdAndWalletUser(Long id, UserEntity user);

    void delete(AssetEntity asset);
}

