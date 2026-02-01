package be.lpbconsult.befintax.market.controller;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.market.service.MarketDataService;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;
    private static final LocalDate END_2025 = LocalDate.of(2025, 12, 31);

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}/history")
    public List<PricePoint> getAssetHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "STOCK") AssetType type, @RequestParam String exchange) {

        return marketDataService.getHistoryWithFailover(symbol, type, exchange);
    }

    @GetMapping("/{symbol}/price/live")
    public BigDecimal getPriceLive( @PathVariable String symbol, @RequestParam String exchange) {
        return marketDataService.getPrice(symbol, exchange, LocalDate.now());
    }

    @GetMapping("/{symbol}/price/end2025")
    public BigDecimal getPriceEnd2025( @PathVariable String symbol, @RequestParam String exchange) {
        return marketDataService.getPrice(symbol, exchange, END_2025);
    }
}
