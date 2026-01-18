package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.AssetCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetMapper;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    public AssetDTO createAsset(AssetCreateDTO dto) {
        AssetEntity asset = assetMapper.toEntity(dto);
        return assetMapper.toDto(assetRepository.save(asset));
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

