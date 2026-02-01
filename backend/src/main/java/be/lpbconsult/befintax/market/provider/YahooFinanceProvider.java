package be.lpbconsult.befintax.market.provider;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class YahooFinanceProvider implements MarketDataProvider {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceProvider.class);

    @Override
    public String getProviderName() {
        return "YAHOO_FINANCE";
    }

    @Override
    public boolean supports(AssetType type) {
        // Yahoo gère tout, y compris les indices et le Forex
        return true;
    }

    @Override
    public BigDecimal getPrice(String symbol, String exchangeCode, LocalDate valueDate) {
        List<PricePoint> prices = getHistory(symbol, exchangeCode, valueDate, valueDate);
        if (prices.isEmpty()) {
            throw new RuntimeException("No price found for " + symbol);
        }
        return prices.getFirst().price();
    }

    @Override
    public List<PricePoint> getHistory(String symbol, String exchangeCode, LocalDate fromDate, LocalDate toDate) {
        if (symbol == null || symbol.isBlank()) return Collections.emptyList();

        long start = fromDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long end = toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toEpochSecond();

        String url = String.format(
                "https://query2.finance.yahoo.com/v8/finance/chart/%s?period1=%d&period2=%d&interval=1d&events=history",
                symbol, start, end
        );

        try {
            RestTemplate restTemplate = new RestTemplate();

            // On configure les headers manuellement
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.ALL));
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Ici getBody() est déjà une Map, plus besoin de caster ou de parser manuellement le texte
                return parseYahooJson(response.getBody());
            }

        } catch (Exception e) {
            log.error("Échec manuel Yahoo pour {} : {}", symbol, e.getMessage());
        }

        return Collections.emptyList();
    }

    private List<PricePoint> parseYahooJson(Map<String, Object> body) {
        try {
            Map chart = (Map) body.get("chart");
            List resultList = (List) chart.get("result");
            if (resultList == null || resultList.isEmpty()) return Collections.emptyList();

            Map result = (Map) resultList.get(0);
            List<Integer> timestamps = (List<Integer>) result.get("timestamp");
            Map indicators = (Map) result.get("indicators");
            List quoteList = (List) indicators.get("quote");
            Map quote = (Map) quoteList.get(0);
            List<Double> closePrices = (List<Double>) quote.get("close");

            if (timestamps == null || closePrices == null) return Collections.emptyList();

            List<PricePoint> points = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                Double price = closePrices.get(i);
                if (price != null) {
                    LocalDate date = LocalDate.ofEpochDay(timestamps.get(i) / 86400);
                    // Note : timestamp est en secondes, EpochDay en jours
                    points.add(new PricePoint(date, BigDecimal.valueOf(price)));
                }
            }
            return points;
        } catch (Exception e) {
            log.error("Erreur lors du parsing JSON Yahoo", e);
            return Collections.emptyList();
        }
    }
}