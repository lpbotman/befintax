package be.lpbconsult.befintax.wallet.controller;

import be.lpbconsult.befintax.wallet.dto.AssetTransactionCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionDTO;
import be.lpbconsult.befintax.wallet.service.AssetTransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets/{assetId}/transactions")
@CrossOrigin
public class AssetTransactionController {

    private final AssetTransactionService service;

    public AssetTransactionController(AssetTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public AssetTransactionDTO add(
            @PathVariable Long assetId,
            @RequestBody AssetTransactionCreateDTO dto
    ) {
        return service.addTransaction(assetId, dto);
    }
}

