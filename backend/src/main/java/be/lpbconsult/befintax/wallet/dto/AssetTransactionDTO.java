package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetTransactionDTO(
        Long id,
        TransactionType type,
        LocalDate date,
        BigDecimal quantity,
        BigDecimal price,
        String currency
) {}

