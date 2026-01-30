package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.AssetType;

import java.math.BigDecimal;
import java.util.List;

public record AssetCreateDTO(
        String name,
        String symbol,
        AssetType type,
        Boolean taxCollectedByBroker,
        BigDecimal stockTaxRate,
        BigDecimal priceEnd2025,
        String currency,
        String exchange,
        List<AssetTransactionCreateDTO> transactions,
        Long walletId
) {}
