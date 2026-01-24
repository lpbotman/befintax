package be.lpbconsult.befintax.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaxTransactionDetailDTO(
        LocalDate date,
        String assetName,       // ex: "Apple" ou "AAPL"
        BigDecimal quantity,
        BigDecimal sellPrice,   // Prix de vente total pour cette ligne
        BigDecimal fiscalCost,  // Coût fiscal total (Prix achat ou Photo 2025)
        BigDecimal taxableGain  // La PV/MV calculée (Sell - Cost)
) {}
