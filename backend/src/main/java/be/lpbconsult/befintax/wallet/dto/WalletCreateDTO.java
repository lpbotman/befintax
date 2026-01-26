package be.lpbconsult.befintax.wallet.dto;

import java.util.List;

public record WalletCreateDTO(
        String name,
        List<AssetDTO> assets
)
{}
