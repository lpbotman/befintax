package be.lpbconsult.befintax.wallet.entity;

import be.lpbconsult.befintax.wallet.enums.AssetType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assets")
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol;

    @Enumerated(EnumType.STRING)
    private AssetType type;

    private Boolean taxCollectedByBroker;
    private BigDecimal stockTaxRate; // ex: 0.35
    private BigDecimal priceEnd2025;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetTransactionEntity> transactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public Boolean getTaxCollectedByBroker() {
        return taxCollectedByBroker;
    }

    public void setTaxCollectedByBroker(Boolean taxCollectedByBroker) {
        this.taxCollectedByBroker = taxCollectedByBroker;
    }

    public BigDecimal getStockTaxRate() {
        return stockTaxRate;
    }

    public void setStockTaxRate(BigDecimal stockTaxRate) {
        this.stockTaxRate = stockTaxRate;
    }

    public List<AssetTransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AssetTransactionEntity> transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getPriceEnd2025() {return priceEnd2025;}

    public void setPriceEnd2025(BigDecimal priceEnd2025) {
        this.priceEnd2025 = priceEnd2025;
    }

    public WalletEntity getWallet() {
        return wallet;
    }

    public void setWallet(WalletEntity wallet) {
        this.wallet = wallet;
    }
}

