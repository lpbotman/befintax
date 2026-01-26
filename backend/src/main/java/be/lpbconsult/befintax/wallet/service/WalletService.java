package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.account.entity.UserEntity;
import be.lpbconsult.befintax.service.SecurityService;
import be.lpbconsult.befintax.wallet.dto.WalletDTO;
import be.lpbconsult.befintax.wallet.entity.WalletEntity;
import be.lpbconsult.befintax.wallet.mapper.WalletMapper;
import be.lpbconsult.befintax.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final SecurityService securityService;
    private final WalletMapper walletMapper;


    public WalletService(WalletRepository walletRepository, SecurityService securityService, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.securityService = securityService;
        this.walletMapper = walletMapper;
    }

    public List<WalletDTO> findAll() {
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        List<WalletEntity> wallets = walletRepository.findByUser(currentUser);

        if (wallets.isEmpty()) {
            WalletEntity wallet = new WalletEntity();
            wallet.setName("wallet");
            wallet.setUser(currentUser);
            wallet = createWallet(wallet);
            wallets.add(wallet);
        }

        return wallets
                .stream()
                .map(walletMapper::toDto)
                .toList();
    }

    public WalletEntity createWallet(WalletEntity wallet) {
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        wallet.setUser(currentUser);
        return walletRepository.save(wallet);
    }

    public Optional<WalletEntity> findById(Long walletId) {
        UserEntity currentUser = securityService.getCurrentAuthenticatedUser();
        return walletRepository.findByIdAndUser(walletId, currentUser);
    }
}
