package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.AssetType;

import java.math.BigDecimal;

public record AssetCreateDTO(
        String name,
        String symbol,
        AssetType type,
        Boolean taxCollectedByBroker,
        BigDecimal stockTaxRate
) {}
