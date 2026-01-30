package be.lpbconsult.befintax.market.service;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.market.provider.MarketDataProvider;
import be.lpbconsult.befintax.market.utils.YahooTickerHelper;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MarketDataService {

    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);

    private final List<MarketDataProvider> providers;

    public MarketDataService(List<MarketDataProvider> providers) {
        this.providers = providers;
    }

    @Cacheable(value = "asset-prices", key = "{#symbol, #type, #exchangeCode}", unless = "#result == null || #result.isEmpty()")
    public List<PricePoint> getHistoryWithFailover(String symbol, AssetType type, String exchangeCode) {

        for (MarketDataProvider provider : providers) {
            if (provider.supports(type)) {
                // On récupère la liste des tickers possibles (ex: [IWLE.F, IWLE.DE] pour Yahoo, ou juste [IWLE] pour les autres)
                List<String> candidateSymbols = getCandidateSymbols(symbol, exchangeCode, provider.getProviderName());

                for (String adjustedSymbol : candidateSymbols) {
                    try {
                        if ("YAHOO_FINANCE".equals(provider.getProviderName())) {
                            Thread.sleep(500);
                        }
                        log.info("Tentative récupération {} via {}", adjustedSymbol, provider.getProviderName());

                        List<PricePoint> history = provider.getHistory(adjustedSymbol);

                        if (history != null && !history.isEmpty()) {
                            log.info("Succès via {} avec le symbole {}", provider.getProviderName(), adjustedSymbol);
                            return history;
                        }

                        log.warn("Provider {} a renvoyé vide pour {}", provider.getProviderName(), adjustedSymbol);

                    } catch (Exception e) {
                        log.error("Échec du provider {} pour {} : {}. Tentative du candidat suivant...",
                                provider.getProviderName(), adjustedSymbol, e.getMessage());
                    }
                }
            }
        }

        log.error("Aucun provider n'a réussi à récupérer les données pour {}", symbol);
        return Collections.emptyList();
    }

    /**
     * Gère la liste des symboles à tester.
     * Pour Yahoo, renvoie [TickerSpécifique, TickerFallback]. Pour les autres, renvoie [TickerPur].
     */
    private List<String> getCandidateSymbols(String symbol, String exchange, String providerName) {
        if ("YAHOO_FINANCE".equals(providerName)) {
            // Utilise la méthode avec fallback que nous avons créée
            return YahooTickerHelper.getYahooTickersWithFallback(symbol, exchange);
        }

        // Par défaut (Twelve Data, etc.), on ne teste qu'une seule variante
        return Collections.singletonList(symbol);
    }

    // Méthode utilitaire pour gérer la différence "IWDA" vs "IWDA.AS"
    private String adjustSymbolForProvider(String symbol, String exchange, String providerName) {
        if ("YAHOO_FINANCE".equals(providerName)) {
            // Appel propre à la méthode utilitaire
            return YahooTickerHelper.toYahooTicker(symbol, exchange);
        }

        // Twelve Data ou autres
        return symbol;
    }
}