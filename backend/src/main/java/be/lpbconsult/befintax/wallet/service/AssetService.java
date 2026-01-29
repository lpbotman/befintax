package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.account.entity.UserEntity;
import be.lpbconsult.befintax.exception.ResourceNotFoundException;
import be.lpbconsult.befintax.service.SecurityService;
import be.lpbconsult.befintax.wallet.dto.AssetCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.entity.WalletEntity;
import be.lpbconsult.befintax.wallet.mapper.AssetMapper;
import be.lpbconsult.befintax.wallet.mapper.AssetTransactionMapper;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import be.lpbconsult.befintax.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final AssetTransactionMapper assetTransactionMapper;
    private final WalletService walletService;
    private final SecurityService securityService;


    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper,
                        AssetTransactionMapper assetTransactionMapper, WalletRepository walletRepository,
                        SecurityService securityService, WalletService walletService) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
        this.assetTransactionMapper = assetTransactionMapper;
        this.walletService = walletService;
        this.securityService = securityService;
    }

    public AssetDTO createAsset(AssetCreateDTO dto) {
        AssetEntity asset = assetMapper.toEntity(dto);

        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();

        WalletEntity wallet;

        if(dto.walletId() == null){
            wallet = new WalletEntity();
            wallet.setName("wallet");
            wallet.setUser(currentUser);
            walletService.createWallet(wallet);
        }
        else {
            wallet = walletService.findById(dto.walletId())
                    .orElseThrow(() -> new ResourceNotFoundException("invalid wallet"));
        }

        asset.setWallet(wallet);

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
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        return assetRepository.findByWalletUser(currentUser)
                .stream()
                .map(assetMapper::toDto)
                .toList();
    }

    public AssetEntity getAssetOrThrow(Long id) {
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        return assetRepository.findByIdAndWalletUser(id, currentUser)
                .orElseThrow(() -> new AccessDeniedException("Asset not found"));
    }

    public void deleteAsset(Long id) {
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        Optional<AssetEntity> asset = assetRepository.findByIdAndWalletUser(id, currentUser);
        if (asset.isPresent()) {
            assetRepository.delete(asset.get());
        } else {
            throw new AccessDeniedException("Asset not found");
        }
    }
}

