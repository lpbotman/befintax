package be.lpbconsult.befintax.wallet.controller;


import be.lpbconsult.befintax.wallet.dto.AssetTransactionTaxDTO;
import be.lpbconsult.befintax.wallet.dto.TaxGainCalculationDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;
import be.lpbconsult.befintax.wallet.service.AssetTransactionService;
import be.lpbconsult.befintax.wallet.service.TaxGainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/taxes")
@CrossOrigin
public class TaxesController {

    private final AssetTransactionService assetTransactionService;
    private final TaxGainService taxGainService;

    public TaxesController(AssetTransactionService assetTransactionService, TaxGainService taxGainService) {
        this.assetTransactionService = assetTransactionService;
        this.taxGainService = taxGainService;
    }


    @GetMapping("/assets-transactions")
    public List<AssetTransactionTaxDTO> calculateAssetsTransactionTax(@RequestParam(required = false) LocalDate beginDate,
                                                                      @RequestParam(required = false) LocalDate endDate){
        if (beginDate != null && endDate != null && beginDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "beginDate must be before endDate"
            );
        }

        return assetTransactionService.findTransactionsWhereTaxNotCollectedByBroker(Optional.ofNullable(beginDate), Optional.ofNullable(endDate));
    }

    @GetMapping("/gain/{year}")
    public ResponseEntity<TaxGainCalculationDTO> getYearlyReport(@PathVariable int year) {
        TaxGainCalculationDTO result = taxGainService.getYearlyReport(year);
        return ResponseEntity.ok(result);
    }
}
