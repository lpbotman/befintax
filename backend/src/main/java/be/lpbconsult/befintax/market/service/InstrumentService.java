package be.lpbconsult.befintax.market.service;

import be.lpbconsult.befintax.market.model.InstrumentEntity;
import be.lpbconsult.befintax.market.model.InstrumentType;
import be.lpbconsult.befintax.market.repository.InstrumentRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.domain.Pageable;
import java.util.List;
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
        // 1. Nettoyage sélectif
        repository.deleteByCategory(type);

        // 2. Récupération des données
        // On ajoute l'API Key ici si elle n'est pas déjà dans l'URL passée en paramètre
        String finalUrl = url;//.contains("apikey") ? url : url + "&apikey=" + apiKey;

        TwelveDataSymbolResponse response = restTemplate.getForObject(finalUrl, TwelveDataSymbolResponse.class);

        // Utilisation des accesseurs de Record : .symbols() au lieu de .getData()
        if (response != null && response.symbols() != null) {
            List<InstrumentEntity> instruments = response.symbols().stream().map(dto -> {
                InstrumentEntity i = new InstrumentEntity();

                // ATTENTION : Les records n'utilisent pas "get", mais le nom du champ directement
                i.setSymbol(dto.symbol());
                i.setName(dto.name());
                i.setCurrency(dto.currency());
                i.setExchange(dto.exchange());
                i.setMicCode(dto.micCode()); // Utilise le nom défini dans le record
                i.setCountry(dto.country());

                i.setCategory(type);
                return i;
            }).collect(Collectors.toList());

            // 3. Insertion
            repository.saveAll(instruments);
        }
    }

    public List<InstrumentEntity> searchInstruments(String query, Pageable offset) {
        return repository.searchInstruments(query, offset);
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