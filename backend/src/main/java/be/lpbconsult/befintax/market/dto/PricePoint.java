package be.lpbconsult.befintax.market.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePoint(
        LocalDate date,
        BigDecimal price
) {}
