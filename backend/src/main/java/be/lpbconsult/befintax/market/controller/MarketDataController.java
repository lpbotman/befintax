package be.lpbconsult.befintax.market.controller;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.market.service.MarketDataService;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}")
    public List<PricePoint> getAssetHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "STOCK") AssetType type, @RequestParam String exchange) {

        return marketDataService.getHistoryWithFailover(symbol, type, exchange);
    }
}
