package be.lpbconsult.befintax.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetTransactionTaxDTO (
    Long id,
    AssetDTO asset,
    LocalDate date,
    BigDecimal price,
    BigDecimal taxRate,
    BigDecimal taxAmount
){}
