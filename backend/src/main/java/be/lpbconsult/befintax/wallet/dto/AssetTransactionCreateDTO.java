package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetTransactionCreateDTO(
        TransactionType type,
        LocalDate date,
        BigDecimal quantity,
        BigDecimal price,
        String currency
) {}

