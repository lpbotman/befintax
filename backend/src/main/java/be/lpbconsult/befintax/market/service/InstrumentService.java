package be.lpbconsult.befintax.market.service;

import be.lpbconsult.befintax.market.model.InstrumentEntity;
import be.lpbconsult.befintax.market.model.InstrumentType;
import be.lpbconsult.befintax.market.repository.InstrumentRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstrumentService {

    private final InstrumentRepository repository;
    private final RestTemplate restTemplate;

    public InstrumentService(InstrumentRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void syncInstruments(InstrumentType type, String url) {
        repository.deleteByCategory(type);

        TwelveDataSymbolResponse response = restTemplate.getForObject(url, TwelveDataSymbolResponse.class);

        if (response != null && response.symbols() != null) {
            List<InstrumentEntity> instruments = response.symbols().stream().map(dto -> {
                InstrumentEntity i = new InstrumentEntity();

                i.setSymbol(dto.symbol());
                i.setName(dto.name());
                i.setCurrency(dto.currency());
                i.setExchange(dto.exchange());
                i.setMicCode(dto.micCode());
                i.setCountry(dto.country());
                i.setCategory(type);
                return i;
            }).collect(Collectors.toList());

            repository.saveAll(instruments);
        }
    }

    public List<InstrumentEntity> searchInstruments(@NonNull String query, @NonNull InstrumentType category, @NonNull Pageable offset) {
        return repository.searchInstruments(query, category, offset);
    }

    public Optional<InstrumentEntity> findBySymbolAndExchange(@NonNull String symbol, String exchange) {
        return repository.findBySymbolAndExchange(symbol, exchange);
    }

    public void save(@NonNull InstrumentEntity instrument) {
        repository.save(instrument);
    }
}

record TwelveDataSymbolResponse(
        @JsonProperty("data") List<TwelveDataSymbol> symbols, // Accesseur = symbols()
        String status,
        String message
) {}

record TwelveDataSymbol(
        String symbol,    // Accesseur = symbol()
        String name,      // Accesseur = name()
        String currency,
        String exchange,
        @JsonProperty("mic_code") String micCode, // Accesseur = micCode()
        String country,
        String type
) {}