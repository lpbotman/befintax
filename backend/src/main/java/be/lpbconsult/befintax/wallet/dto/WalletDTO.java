package be.lpbconsult.befintax.wallet.dto;

import java.util.List;

public record WalletDTO(
        Long id,
        String name,
        List<AssetDTO> assets
)
{}
