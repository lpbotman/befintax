package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.AssetCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetMapper;
import be.lpbconsult.befintax.wallet.mapper.AssetTransactionMapper;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final AssetTransactionMapper assetTransactionMapper;


    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper, AssetTransactionMapper assetTransactionMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
        this.assetTransactionMapper = assetTransactionMapper;
    }

    public AssetDTO createAsset(AssetCreateDTO dto) {
        AssetEntity asset = assetMapper.toEntity(dto);
        asset.setTransactions(new ArrayList<>());
        assetRepository.save(asset);

        if (dto.transactions() != null) {
            List<AssetTransactionEntity> transactions = dto.transactions().stream()
                    .map(txDto -> {
                        AssetTransactionEntity tx = assetTransactionMapper.toEntity(txDto);
                        tx.setAsset(asset);
                        return tx;
                    })
                    .toList();

            asset.getTransactions().addAll(transactions);
            assetRepository.save(asset);
        }

        return assetMapper.toDto(asset);
    }

    public List<AssetDTO> findAll() {
        return assetRepository.findAll()
                .stream()
                .map(assetMapper::toDto)
                .toList();
    }

    public AssetEntity getAssetOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }
}

