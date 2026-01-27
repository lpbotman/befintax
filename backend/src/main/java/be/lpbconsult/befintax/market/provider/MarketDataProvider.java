package be.lpbconsult.befintax.market.provider;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.wallet.enums.AssetType;

import java.util.List;

public interface MarketDataProvider {
    String getProviderName();

    boolean supports(AssetType type);

    List<PricePoint> getHistory(String symbol);
}