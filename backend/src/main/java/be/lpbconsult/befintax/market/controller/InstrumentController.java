package be.lpbconsult.befintax.market.controller;

import be.lpbconsult.befintax.market.model.InstrumentEntity;
import be.lpbconsult.befintax.market.model.InstrumentType;
import be.lpbconsult.befintax.market.service.InstrumentService;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/public/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @GetMapping("/search")
    public List<InstrumentEntity> search(
            @RequestParam String query,
            @RequestParam InstrumentType type,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        // Spring injecte directement le pageable ici
        return instrumentService.searchInstruments(query, type, pageable);
    }

    @PostMapping("/sync/stocks")
    public ResponseEntity<String> syncStocks() {
        String url = "https://api.twelvedata.com/stocks";
        instrumentService.syncInstruments(InstrumentType.STOCK, url);
        return ResponseEntity.ok("Synchronisation des actions terminée");
    }

    @PostMapping("/sync/cryptos")
    public ResponseEntity<String> syncCryptos() {
        String url = "https://api.twelvedata.com/cryptocurrencies";
        instrumentService.syncInstruments(InstrumentType.CRYPTO, url);
        return ResponseEntity.ok("Synchronisation des cryptos terminée");
    }

    @PostMapping("/sync/etfs")
    public ResponseEntity<String> syncEtfs() {
        String url = "https://api.twelvedata.com/etfs";
        instrumentService.syncInstruments(InstrumentType.ETF, url);
        return ResponseEntity.ok("Synchronisation des etfs terminée");
    }
}