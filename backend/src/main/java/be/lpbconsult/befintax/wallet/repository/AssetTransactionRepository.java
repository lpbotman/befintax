package be.lpbconsult.befintax.wallet.repository;

import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetTransactionRepository extends JpaRepository<AssetTransactionEntity, Long> {
    List<AssetTransactionEntity> findByAssetId(Long assetId);
}
