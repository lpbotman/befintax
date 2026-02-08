package be.lpbconsult.befintax.market.service;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.market.model.InstrumentEntity;
import be.lpbconsult.befintax.market.provider.MarketDataProvider;
import be.lpbconsult.befintax.market.utils.YahooTickerHelper;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MarketDataService {

    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);
    private final List<MarketDataProvider> providers;
    private final InstrumentService instrumentService;
    private static final LocalDate END_2025 = LocalDate.of(2025, 12, 31);

    public MarketDataService(List<MarketDataProvider> providers, InstrumentService instrumentService) {
        this.providers = providers;
        this.instrumentService = instrumentService;
    }

    @Cacheable(value = "asset-prices-history", key = "{#symbol, #type, #exchangeCode}", unless = "#result == null || #result.isEmpty()")
    public List<PricePoint> getHistoryWithFailover(String symbol, AssetType type, String exchangeCode) {

        for (MarketDataProvider provider : providers) {
            if (provider.supports(type)) {
                List<String> candidateSymbols = getCandidateSymbols(symbol, exchangeCode, provider.getProviderName());

                for (String adjustedSymbol : candidateSymbols) {
                    try {

                        LocalDate endDate = LocalDate.now();
                        LocalDate startdate = endDate.minusDays(90);
                        List<PricePoint> history = provider.getHistory(adjustedSymbol, exchangeCode, startdate, endDate);

                        if (history != null && !history.isEmpty()) {
                            return history;
                        }
                        if ("YAHOO_FINANCE".equals(provider.getProviderName())) {
                            Thread.sleep(500);
                        }
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
            return YahooTickerHelper.getYahooTickersWithFallback(symbol, exchange);
        }
        return Collections.singletonList(symbol);
    }

    @Cacheable(value = "asset-price", key = "{#symbol, #exchangeCode, #valueDate}", unless = "#result == null")
    public BigDecimal getPrice(@NonNull String symbol, @NonNull String exchangeCode,@NonNull LocalDate valueDate) {
        log.info("getPrice({}, {}, {})", symbol, exchangeCode, valueDate);
        valueDate = getMarketLastOpeningDate(valueDate);
        if (valueDate.equals(END_2025) || valueDate.equals(LocalDate.now())) {
            Optional<InstrumentEntity> instrument = instrumentService.findBySymbolAndExchange(symbol, exchangeCode);
            if (instrument.isPresent()) {
                if (valueDate.equals(END_2025) && instrument.get().getClosePrice2025() != null) {
                    return instrument.get().getClosePrice2025();
                }
                if (valueDate.equals(LocalDate.now()) && instrument.get().getPrice() != null) {
                    return instrument.get().getPrice();
                }
            }
        }

        for (MarketDataProvider provider : providers) {
            log.info("getPrice({})", provider.getProviderName());
            try {
                List<String> candidateSymbols = getCandidateSymbols(symbol, exchangeCode, provider.getProviderName());
                for (String adjustedSymbol : candidateSymbols) {
                    BigDecimal price = provider.getPrice(adjustedSymbol, exchangeCode, valueDate);
                    if (price != null) {
                        updateInstrumentPriceAsync(symbol, exchangeCode, valueDate, price);
                        return price;
                    }
                    if ("YAHOO_FINANCE".equals(provider.getProviderName())) {
                        Thread.sleep(500);
                    }
                }

            }catch (Exception e) {
                log.error("Erreur lors de la récupération du prix pour {} : {}", symbol, e.getMessage());
            }
        }
        throw new IllegalArgumentException("No provider can retrieve price");
    }

    @Async
    public void updateInstrumentPriceAsync(@NonNull String symbol, @NonNull String exchangeCode, LocalDate date, BigDecimal price) {
        instrumentService.findBySymbolAndExchange(symbol, exchangeCode).ifPresent(instrument -> {
            boolean updated = false;

            if (date.equals(LocalDate.now())) {
                instrument.setPrice(price);
                instrument.setLastUpdatePrice(LocalDateTime.now());
                updated = true;
            } else if (date.equals(END_2025)) {
                instrument.setClosePrice2025(price);
                updated = true;
            }

            if (updated) {
                instrumentService.save(instrument);
            }
        });
    }

    private LocalDate getMarketLastOpeningDate(@NonNull LocalDate valueDate){
        DayOfWeek day = valueDate.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY) {
            return valueDate.minusDays(1);
        } else if (day == DayOfWeek.SUNDAY) {
            return valueDate.minusDays(2);
        }

        return valueDate;
    }
}