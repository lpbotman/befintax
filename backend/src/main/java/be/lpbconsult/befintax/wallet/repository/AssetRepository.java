package be.lpbconsult.befintax.wallet.repository;

import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
}

