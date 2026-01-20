package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.AssetTransactionCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionTaxDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetTransactionMapper;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import be.lpbconsult.befintax.wallet.repository.AssetTransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetTransactionService {

    private final AssetTransactionRepository repository;
    private final AssetTransactionMapper mapper;
    private final AssetService assetService;

    public AssetTransactionService(
            AssetTransactionRepository repository,
            AssetTransactionMapper mapper,
            AssetService assetService,
            AssetRepository assetRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.assetService = assetService;
    }

    public AssetTransactionDTO addTransaction(Long assetId, AssetTransactionCreateDTO dto) {
        AssetEntity asset = assetService.getAssetOrThrow(assetId);

        AssetTransactionEntity tx = mapper.toEntity(dto);
        tx.setAsset(asset);

        return mapper.toDto(repository.save(tx));
    }

    public List<AssetTransactionTaxDTO> findTransactionsWhereTaxNotCollectedByBroker(Optional<LocalDate> beginDate, Optional<LocalDate> endDate) {
        return repository
                .findTransactionsWhereTaxNotCollectedByBroker(beginDate.orElse(null), endDate.orElse(null))
                .stream()
                .map(transaction -> {

                    BigDecimal tax = transaction.getPrice()
                            .multiply(transaction.getAsset().getStockTaxRate());

                    return new AssetTransactionTaxDTO(
                            transaction.getId(),
                            transaction.getDate(),
                            transaction.getPrice(),
                            transaction.getAsset().getStockTaxRate(),
                            tax
                    );
                })
                .toList();
    }

}

