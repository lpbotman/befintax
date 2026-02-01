package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetTransactionCreateDTO(
        TransactionType type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDate date,
        BigDecimal quantity,
        BigDecimal price,
        String currency
) {}

