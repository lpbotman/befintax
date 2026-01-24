package be.lpbconsult.befintax.wallet.dto;

import java.math.BigDecimal;
import java.util.List;

public record TaxGainCalculationDTO(
        BigDecimal totalGrossGain,      // Gain réel économique
        BigDecimal totalTaxableGain,    // Gain après "Moment Photo"
        BigDecimal exemptionApplied,    // Montant de l'abattement utilisé
        BigDecimal finalTaxableBase,    // Montant réellement taxé (après abattement)
        BigDecimal estimatedTax,
        List<TaxTransactionDetailDTO> transactions
) {}