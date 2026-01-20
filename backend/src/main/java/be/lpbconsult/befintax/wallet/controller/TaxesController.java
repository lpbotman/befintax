package be.lpbconsult.befintax.wallet.controller;


import be.lpbconsult.befintax.wallet.dto.AssetTransactionTaxDTO;
import be.lpbconsult.befintax.wallet.service.AssetTransactionService;
import org.springframework.http.HttpStatus;
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

    public TaxesController(AssetTransactionService assetTransactionService) {
        this.assetTransactionService = assetTransactionService;
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
}
