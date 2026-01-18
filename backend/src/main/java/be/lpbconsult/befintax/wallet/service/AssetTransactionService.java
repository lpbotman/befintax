package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.AssetTransactionCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetTransactionMapper;
import be.lpbconsult.befintax.wallet.repository.AssetTransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AssetTransactionService {

    private final AssetTransactionRepository repository;
    private final AssetTransactionMapper mapper;
    private final AssetService assetService;

    public AssetTransactionService(
            AssetTransactionRepository repository,
            AssetTransactionMapper mapper,
            AssetService assetService
    ) {
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
}

