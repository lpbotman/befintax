package be.lpbconsult.befintax.market.provider;

import be.lpbconsult.befintax.market.dto.PricePoint;
import be.lpbconsult.befintax.wallet.enums.AssetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

record TwelveDataResponse(List<TwelveDataValue> values, String status, String message){}
record TwelveDataValue(String datetime, String close) {}

@Service
public class TwelveDataProvider implements MarketDataProvider {

    private static final Logger log = LoggerFactory.getLogger(TwelveDataProvider.class);

    @Value("${app.market-data.twelve-data.key}")
    private String apiKey;

    private final RestClient restClient;

    public TwelveDataProvider(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.twelvedata.com").build();
    }

    @Override
    public String getProviderName() {
        return "TWELVE_DATA";
    }

    @Override
    public boolean supports(AssetType type) {
        // Twelve Data gère bien Stocks, ETF et certaines Cryptos
        return List.of(AssetType.STOCK, AssetType.ETF, AssetType.CRYPTO).contains(type);
    }

    @Override
    public List<PricePoint> getHistory(String symbol) {
        if(symbol==null) return Collections.emptyList();

        try {
            TwelveDataResponse response = restClient.get()
                    .uri(uri -> uri.path("/time_series")
                            .queryParam("symbol", symbol)
                            .queryParam("interval", "1day")
                            .queryParam("outputsize", "90")
                            .queryParam("apikey", apiKey)
                            .build())
                    .retrieve()
                    .body(TwelveDataResponse.class);

            // 1. Vérification du statut spécifique à Twelve Data
            if (response != null && "error".equals(response.status())) {
                log.warn("TwelveData a renvoyé une erreur pour {} : {}", symbol, response.message());
                // On jette une exception pour NE PAS mettre l'erreur en cache
                throw new RuntimeException("Quota dépassé ou symbole invalide");
            }

            if (response == null || response.values() == null) {
                log.warn("Aucune donnée trouvée chez TwelveData pour {}", symbol);
                return Collections.emptyList();
            }

            return response.values().stream()
                    .map(v -> new PricePoint(
                            LocalDate.parse(v.datetime()),
                            new BigDecimal(v.close())
                    ))
                    .sorted(Comparator.comparing(PricePoint::date))
                    .toList();

        } catch (Exception e) {
            log.error("Erreur critique lors de l'appel TwelveData pour {}", symbol, e);
            // En jetant une exception ici, Spring Cache comprend qu'il ne doit rien stocker
            throw e;
        }
    }
}
