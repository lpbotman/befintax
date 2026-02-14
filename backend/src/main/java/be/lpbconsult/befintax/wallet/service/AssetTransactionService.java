package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.AssetTransactionCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionTaxDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetMapper;
import be.lpbconsult.befintax.wallet.mapper.AssetTransactionMapper;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import be.lpbconsult.befintax.wallet.repository.AssetTransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetTransactionService {

    private final AssetTransactionRepository repository;
    private final AssetTransactionMapper transactionMapper;
    private final AssetMapper assetMapper;
    private final AssetService assetService;

    public AssetTransactionService(
            AssetTransactionRepository repository,
            AssetTransactionMapper transactionMapper,
            AssetMapper assetMapper,
            AssetService assetService,
            AssetRepository assetRepository) {
        this.repository = repository;
        this.transactionMapper = transactionMapper;
        this.assetMapper = assetMapper;
        this.assetService = assetService;
    }

    public AssetTransactionDTO addTransaction(Long assetId, AssetTransactionCreateDTO dto) {
        AssetEntity asset = assetService.getAssetOrThrow(assetId);

        AssetTransactionEntity tx = transactionMapper.toEntity(dto);
        tx.setAsset(asset);

        return transactionMapper.toDto(repository.save(tx));
    }

    public List<AssetTransactionTaxDTO> findTransactionsWhereTaxNotCollectedByBroker(Optional<LocalDate> beginDate, Optional<LocalDate> endDate) {
        return repository
                .findTransactionsWhereTaxNotCollectedByBroker(beginDate.orElse(null), endDate.orElse(null))
                .stream()
                .map(transaction -> {

                    BigDecimal tax = transaction.getPrice()
                            .multiply(transaction.getAsset().getStockTaxRate())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    return new AssetTransactionTaxDTO(
                            transaction.getId(),
                            assetMapper.toDto(transaction.getAsset()),
                            transaction.getDate(),
                            transaction.getPrice(),
                            transaction.getAsset().getStockTaxRate(),
                            tax
                    );
                })
                .toList();
    }

    public void deleteTransaction(Long assetId, Long transactionId) {
        AssetEntity asset = assetService.getAssetOrThrow(assetId);
        repository.deleteByIdAndAsset(transactionId, asset);
    }
}

