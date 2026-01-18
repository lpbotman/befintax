package be.lpbconsult.befintax.wallet.controller;

import be.lpbconsult.befintax.wallet.dto.AssetCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetDTO;
import be.lpbconsult.befintax.wallet.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public AssetDTO create(@RequestBody AssetCreateDTO dto) {
        return assetService.createAsset(dto);
    }

    @GetMapping
    public List<AssetDTO> getAll() {
        return assetService.findAll();
    }
}

