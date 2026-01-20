package be.lpbconsult.befintax.wallet.repository;

import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetTransactionRepository extends JpaRepository<AssetTransactionEntity, Long> {
    List<AssetTransactionEntity> findByAssetId(Long assetId);

    @Query("SELECT t FROM AssetTransactionEntity t WHERE " +
            "(CAST(:beginDate AS date) IS NULL OR t.date >= :beginDate) AND " +
            "(CAST(:endDate AS date) IS NULL OR t.date <= :endDate) AND " +
            "t.price > 0 AND t.asset.stockTaxRate > 0 AND " +
            "t.asset.taxCollectedByBroker = false")
    List<AssetTransactionEntity> findTransactionsWhereTaxNotCollectedByBroker(
            @Param("beginDate") LocalDate beginDate,
            @Param("endDate") LocalDate endDate);
}
