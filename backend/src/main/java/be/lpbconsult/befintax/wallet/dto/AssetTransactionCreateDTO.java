package be.lpbconsult.befintax.wallet.dto;

import be.lpbconsult.befintax.wallet.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetTransactionCreateDTO(
        TransactionType type,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        BigDecimal quantity,
        BigDecimal price,
        String currency
) {}

