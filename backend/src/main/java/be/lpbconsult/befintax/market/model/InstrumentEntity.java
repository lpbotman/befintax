package be.lpbconsult.befintax.market.model;

import jakarta.persistence.*;

@Entity
@Table(name = "instruments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "mic_code"})
})
public class InstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;
    private String currency;
    private String exchange;
    private String micCode;
    private String country;

    @Enumerated(EnumType.STRING)
    private InstrumentType category; // STOCK, ETF, CRYPTO

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getMicCode() {
        return micCode;
    }

    public void setMicCode(String micCode) {
        this.micCode = micCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public InstrumentType getCategory() {
        return category;
    }

    public void setCategory(InstrumentType category) {
        this.category = category;
    }
}