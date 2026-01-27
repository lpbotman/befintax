package be.lpbconsult.befintax.market.service;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.market.provider.MarketDataProvider;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketDataService {

    private final List<MarketDataProvider> providers;

    public MarketDataService(List<MarketDataProvider> providers) {
        this.providers = providers;
    }

    @Cacheable(value = "asset-prices", key = "{#symbol, #type}", unless = "#result == null || #result.isEmpty()")
    public List<PricePoint> getPriceHistory(String symbol, AssetType type) {

        // On cherche le premier provider qui supporte ce type d'actif
        MarketDataProvider provider = providers.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun fournisseur de données pour le type : " + type));

        return provider.getHistory(symbol);
    }
}