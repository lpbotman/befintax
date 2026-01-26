package be.lpbconsult.befintax.wallet.controller;

import be.lpbconsult.befintax.wallet.dto.WalletDTO;
import be.lpbconsult.befintax.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<WalletDTO> getAll() {
        return walletService.findAll();
    }

    @GetMapping("/main")
    public WalletDTO getMainWallet() {
        return walletService.findAll().getFirst();
    }
}
