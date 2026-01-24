package be.lpbconsult.befintax.wallet.service;

import be.lpbconsult.befintax.wallet.dto.TaxGainCalculationDTO;
import be.lpbconsult.befintax.wallet.dto.TaxTransactionDetailDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;
import be.lpbconsult.befintax.wallet.enums.TransactionType;
import be.lpbconsult.befintax.wallet.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TaxGainService {
    private static final LocalDate FISCAL_CLIFF = LocalDate.of(2026, 1, 1);
    private static final BigDecimal BASE_EXEMPTION = new BigDecimal("10000");
    private static final BigDecimal MAX_CARRY_OVER = new BigDecimal("5000"); // Plafond max 15k (10k + 5k)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final AssetRepository assetRepository;


    TaxGainService(AssetRepository assetRepository){
        this.assetRepository = assetRepository;
    }

    public TaxGainCalculationDTO getYearlyReport(int year) {
        List<AssetEntity> assets = assetRepository.findAll();

        int yearsPassedSince2026 = Math.max(0, year - FISCAL_CLIFF.getYear());
        return calculateGlobalTax(assets, yearsPassedSince2026);
    }

    private record AssetCalculationResult(BigDecimal totalGrossGain, BigDecimal totalTaxableGain, List<TaxTransactionDetailDTO> details) {}

    /**
     * Calcule la taxe globale pour un utilisateur sur une liste d'actifs.
     * @param assets Liste des actifs de l'utilisateur
     * @param yearsPassedSince2026 Nombre d'années sans PV pour le calcul du report (ex: 1 pour 2027)
     */
    private TaxGainCalculationDTO calculateGlobalTax(List<AssetEntity> assets, int yearsPassedSince2026) {
        BigDecimal globalGrossGain = BigDecimal.ZERO;
        BigDecimal globalTaxableGain = BigDecimal.ZERO;
        List<TaxTransactionDetailDTO> allTransactions = new ArrayList<>(); // Liste globale

        for (AssetEntity asset : assets) {
            // On récupère un objet intermédiaire qui contient les totaux ET les détails pour cet asset
            AssetCalculationResult assetResult = calculateForAssetInternal(asset);

            globalGrossGain = globalGrossGain.add(assetResult.totalGrossGain);
            globalTaxableGain = globalTaxableGain.add(assetResult.totalTaxableGain);
            allTransactions.addAll(assetResult.details); // On fusionne les transactions
        }

        // 2. Calcul du plafond d'exonération personnalisé (Règle des 1 000€/an reportés)
        // L'exonération augmente de 1000€ par an si non utilisée, max +5000€.
        BigDecimal carryOver = new BigDecimal(yearsPassedSince2026).multiply(new BigDecimal("1000"));
        BigDecimal currentYearExemptionLimit = BASE_EXEMPTION.add(carryOver.min(MAX_CARRY_OVER));

        // 3. Application de l'exonération sur la PV nette globale
        BigDecimal exemptionApplied = BigDecimal.ZERO;
        BigDecimal finalTaxableBase = BigDecimal.ZERO;

        if (globalTaxableGain.compareTo(BigDecimal.ZERO) > 0) {
            exemptionApplied = globalTaxableGain.min(currentYearExemptionLimit);
            finalTaxableBase = globalTaxableGain.subtract(exemptionApplied);
        }

        BigDecimal estimatedTax = finalTaxableBase.multiply(TAX_RATE);

        allTransactions.sort(Comparator.comparing(TaxTransactionDetailDTO::date));

        return new TaxGainCalculationDTO(
                globalGrossGain,
                globalTaxableGain,
                exemptionApplied,
                finalTaxableBase,
                estimatedTax,
                allTransactions
        );
    }

    private AssetCalculationResult calculateForAssetInternal(AssetEntity asset) {
        List<AssetTransactionEntity> transactions = asset.getTransactions().stream()
                .sorted(Comparator.comparing(AssetTransactionEntity::getDate))
                .toList();

        List<PurchaseLot> purchasePool = new ArrayList<>();
        BigDecimal totalTaxableGain = BigDecimal.ZERO;
        BigDecimal totalGrossGain = BigDecimal.ZERO;
        List<TaxTransactionDetailDTO> transactionDetails = new ArrayList<>();

        for (AssetTransactionEntity tx : transactions) {
            if (tx.getType() == TransactionType.BUY) {
                purchasePool.add(new PurchaseLot(tx.getQuantity(), tx.getPrice(), tx.getDate()));
            } else if (tx.getType() == TransactionType.SELL) {
                BigDecimal remainingToSell = tx.getQuantity();

                // Variables pour accumuler le résultat de CETTE vente spécifique
                BigDecimal txTotalFiscalCost = BigDecimal.ZERO;
                BigDecimal txTotalTaxableGain = BigDecimal.ZERO;
                BigDecimal txOriginalQty = tx.getQuantity(); // Pour affichage

                while (remainingToSell.compareTo(BigDecimal.ZERO) > 0 && !purchasePool.isEmpty()) {
                    PurchaseLot currentLot = purchasePool.get(0);
                    BigDecimal qtyToTake = remainingToSell.min(currentLot.quantity);

                    // --- RÈGLE DU MOMENT PHOTO ---
                    BigDecimal fiscalBasePrice = currentLot.price;
                    if (currentLot.date.isBefore(FISCAL_CLIFF) && asset.getPriceEnd2025() != null) {
                        fiscalBasePrice = currentLot.price.max(asset.getPriceEnd2025());
                    }

                    // Calculs partiels
                    BigDecimal lotTaxableGain = tx.getPrice().subtract(fiscalBasePrice).multiply(qtyToTake);
                    BigDecimal lotFiscalCost = fiscalBasePrice.multiply(qtyToTake);

                    // Accumulation globale
                    totalTaxableGain = totalTaxableGain.add(lotTaxableGain);
                    totalGrossGain = totalGrossGain.add(tx.getPrice().subtract(currentLot.price).multiply(qtyToTake));

                    // Accumulation pour le détail de cette transaction
                    txTotalTaxableGain = txTotalTaxableGain.add(lotTaxableGain);
                    txTotalFiscalCost = txTotalFiscalCost.add(lotFiscalCost);

                    remainingToSell = remainingToSell.subtract(qtyToTake);
                    currentLot.quantity = currentLot.quantity.subtract(qtyToTake);
                    if (currentLot.quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        purchasePool.remove(0);
                    }
                }

                // On crée le DTO de détail pour cette vente
                BigDecimal totalSellPrice = tx.getPrice().multiply(txOriginalQty);

                transactionDetails.add(new TaxTransactionDetailDTO(
                        tx.getDate(),
                        asset.getName(), // Ou asset.getSymbol()
                        txOriginalQty,
                        totalSellPrice,
                        txTotalFiscalCost,
                        txTotalTaxableGain
                ));
            }
        }
        return new AssetCalculationResult(totalGrossGain, totalTaxableGain, transactionDetails);
    }

    private static class PurchaseLot {
        BigDecimal quantity;
        BigDecimal price;
        LocalDate date;

        PurchaseLot(BigDecimal quantity, BigDecimal price, LocalDate date) {
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }
    }
}