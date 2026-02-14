package be.lpbconsult.befintax.market.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YahooTickerHelper {

    private static final Map<String, String> EXCHANGE_SUFFIXES = new HashMap<>();

    static {
        // --- AMÉRIQUE DU NORD ---
        EXCHANGE_SUFFIXES.put("TSX", ".TO");   // Toronto
        EXCHANGE_SUFFIXES.put("TSXV", ".V");   // TSX Venture
        EXCHANGE_SUFFIXES.put("NEO", ".NE");   // NEO Exchange (Canada)
        EXCHANGE_SUFFIXES.put("CSE", ".CN");   // Canadian Securities Exchange
        EXCHANGE_SUFFIXES.put("BMV", ".MX");   // Mexico
        EXCHANGE_SUFFIXES.put("OTC", ".OB");   // OTC Markets (souvent .OB ou .PK, parfois sans suffixe)
        // Note: NASDAQ, NYSE, CBOE sont gérés comme "US" (pas de suffixe)
        EXCHANGE_SUFFIXES.put("NASDAQ", "");
        EXCHANGE_SUFFIXES.put("NYSE", "");
        EXCHANGE_SUFFIXES.put("CBOE", "");

        // --- AMÉRIQUE DU SUD ---
        EXCHANGE_SUFFIXES.put("BCBA", ".BA");  // Buenos Aires
        EXCHANGE_SUFFIXES.put("Bovespa", ".SA"); // Sao Paulo
        EXCHANGE_SUFFIXES.put("BVS", ".SN");   // Santiago (Chili)
        EXCHANGE_SUFFIXES.put("BVL", ".LM");   // Lima

        // --- EUROPE DE L'OUEST (Euronext & Co) ---
        //EXCHANGE_SUFFIXES.put("Euronext", ".PA"); // Défaut vers Paris si non spécifié
        EXCHANGE_SUFFIXES.put("EPA", ".PA");   // Paris
        EXCHANGE_SUFFIXES.put("ASE", ".AT");   // Athènes
        EXCHANGE_SUFFIXES.put("BME", ".MC");   // Madrid
        EXCHANGE_SUFFIXES.put("ISE", ".IR");   // Irlande (Euronext Dublin)
        EXCHANGE_SUFFIXES.put("LSE", ".L");    // Londres
        EXCHANGE_SUFFIXES.put("MTA", ".MI");   // Milan
        EXCHANGE_SUFFIXES.put("SIX", ".SW");   // Zurich (Suisse)
        EXCHANGE_SUFFIXES.put("VSE", ".VI");   // Vienne
        EXCHANGE_SUFFIXES.put("ICEX", ".IC");  // Islande

        // --- ALLEMAGNE (Xetra & Régionales) ---
        EXCHANGE_SUFFIXES.put("XETR", ".DE");  // Xetra
        EXCHANGE_SUFFIXES.put("FRA", ".F");    // Francfort
        EXCHANGE_SUFFIXES.put("FSX", ".F");    // Francfort (Variante)
        EXCHANGE_SUFFIXES.put("XBER", ".BE");  // Berlin
        EXCHANGE_SUFFIXES.put("XDUS", ".DU");  // Dusseldorf
        EXCHANGE_SUFFIXES.put("XHAM", ".HM");  // Hambourg
        EXCHANGE_SUFFIXES.put("XHAN", ".HA");  // Hanovre
        EXCHANGE_SUFFIXES.put("Munich", ".MU"); // Munich
        EXCHANGE_SUFFIXES.put("XSTU", ".SG");  // Stuttgart

        // --- SCANDINAVIE & BALTIQUE (OMX) ---
        EXCHANGE_SUFFIXES.put("OMX", ".ST");    // Stockholm (Défaut souvent suédois)
        EXCHANGE_SUFFIXES.put("OMXC", ".CO");   // Copenhague
        EXCHANGE_SUFFIXES.put("OMXH", ".HE");   // Helsinki
        EXCHANGE_SUFFIXES.put("OMXR", ".RG");   // Riga
        EXCHANGE_SUFFIXES.put("OMXT", ".TL");   // Tallinn
        EXCHANGE_SUFFIXES.put("OMXV", ".VS");   // Vilnius
        EXCHANGE_SUFFIXES.put("OSE", ".OL");    // Oslo

        // --- EUROPE DE L'EST ---
        EXCHANGE_SUFFIXES.put("GPW", ".WA");    // Varsovie
        EXCHANGE_SUFFIXES.put("BVB", ".RO");    // Bucarest
        EXCHANGE_SUFFIXES.put("PSE", ".PR");    // Prague (Attention: PSE peut aussi être Philippines)
        EXCHANGE_SUFFIXES.put("MOEX", ".ME");   // Moscou
        EXCHANGE_SUFFIXES.put("BIST", ".IS");   // Istanbul

        // --- ASIE ---
        EXCHANGE_SUFFIXES.put("JPX", ".T");     // Tokyo
        EXCHANGE_SUFFIXES.put("HKEX", ".HK");   // Hong Kong
        EXCHANGE_SUFFIXES.put("SSE", ".SS");    // Shanghai
        EXCHANGE_SUFFIXES.put("SZSE", ".SZ");   // Shenzhen
        EXCHANGE_SUFFIXES.put("TWSE", ".TW");   // Taiwan
        EXCHANGE_SUFFIXES.put("KRX", ".KS");    // Corée (KOSPI)
        EXCHANGE_SUFFIXES.put("SGX", ".SI");    // Singapour
        EXCHANGE_SUFFIXES.put("MYX", ".KL");    // Malaisie (Kuala Lumpur)
        EXCHANGE_SUFFIXES.put("IDX", ".JK");    // Indonésie (Jakarta)
        EXCHANGE_SUFFIXES.put("SET", ".BK");    // Thaïlande (Bangkok)
        EXCHANGE_SUFFIXES.put("PSE_PH", ".PS"); // Philippines (Suffixe distinct pour éviter conflit Prague)
        EXCHANGE_SUFFIXES.put("NSE", ".NS");    // Inde (National)
        EXCHANGE_SUFFIXES.put("BSE", ".BO");    // Inde (Bombay)
        EXCHANGE_SUFFIXES.put("PSX", ".KA");    // Pakistan (Karachi)

        // --- MOYEN-ORIENT ---
        EXCHANGE_SUFFIXES.put("TASE", ".TA");   // Tel Aviv
        EXCHANGE_SUFFIXES.put("Tadawul", ".SR");// Arabie Saoudite
        EXCHANGE_SUFFIXES.put("QSE", ".QA");    // Qatar (QE)
        EXCHANGE_SUFFIXES.put("QE", ".QA");     // Qatar (Variante)
        EXCHANGE_SUFFIXES.put("ADX", ".AE");    // Abu Dhabi
        EXCHANGE_SUFFIXES.put("DFM", ".AE");    // Dubai
        EXCHANGE_SUFFIXES.put("EGX", ".CA");    // Égypte (Le Caire)
        EXCHANGE_SUFFIXES.put("XKUW", ".KW");   // Koweït

        // --- PACIFIQUE / AFRIQUE ---
        EXCHANGE_SUFFIXES.put("ASX", ".AX");    // Australie
        EXCHANGE_SUFFIXES.put("CXA", ".AX");    // Cboe Australie (Souvent map vers ASX)
        EXCHANGE_SUFFIXES.put("NZX", ".NZ");    // Nouvelle-Zélande
        EXCHANGE_SUFFIXES.put("JSE", ".J");     // Johannesburg
    }


    public static List<String> getYahooTickersWithFallback(String symbol, String exchange) {
        List<String> tickers = new ArrayList<>();
        String cleanSymbol = symbol.trim().toUpperCase();
        String mainTicker = toYahooTicker(cleanSymbol, exchange);

        // 1. Ajouter le ticker spécifique demandé
        tickers.add(mainTicker);

        // 2. Ajouter un Fallback basé sur la région
        if (exchange != null) {
            String upperEx = exchange.toUpperCase();

            // Fallback Allemand -> Xetra (.DE)
            if (isGermanExchange(upperEx) && !mainTicker.endsWith(".DE")) {
                tickers.add(cleanSymbol + ".DE");
            }
            // Fallback Euronext -> Paris (.PA)
            else if (isEuronextExchange(upperEx) && !mainTicker.endsWith(".PA")  && !mainTicker.endsWith(".AS") ) {
                tickers.add(cleanSymbol + ".PA"); //Paris
                tickers.add(cleanSymbol + ".AS"); //Amsterdam
            }
            // Fallback Canada -> Toronto (.TO)
            else if (upperEx.equals("TSXV") || upperEx.equals("NEO")) {
                tickers.add(cleanSymbol + ".TO");
            }
        }

        return tickers;
    }

    private static boolean isGermanExchange(String ex) {
        // FSX, XBER, XDUS, XHAM, XHAN, XSTU, MUNICH
        return ex.startsWith("X") || ex.equals("FSX") || ex.equals("MUNICH");
    }

    private static boolean isEuronextExchange(String ex) {
        return List.of("EAM", "EBR", "ELI", "AMS", "BRU", "EURONEXT").contains(ex);
    }

    public static String toYahooTicker(String symbol, String exchange) {
        if (symbol == null || symbol.isEmpty()) return null;

        String cleanSymbol = symbol.trim().toUpperCase();

        if (exchange == null || exchange.isEmpty()) return cleanSymbol;

        String cleanExchange = exchange.trim(); // Pas de toUpperCase ici si tes clés Map sont sensibles, sinon oui.
        String suffix = EXCHANGE_SUFFIXES.get(exchange);

        // Si on trouve une entrée
        if (suffix != null) {
            // Cas spécial : suffixe vide (ex: NASDAQ) -> on renvoie le symbole pur
            if (suffix.isEmpty()) {
                return cleanSymbol;
            }
            // Sinon on ajoute le suffixe s'il n'est pas déjà là
            if (!cleanSymbol.endsWith(suffix)) {
                return cleanSymbol + suffix;
            }
            return cleanSymbol;
        }

        // Fallback : Si l'échange n'est pas dans la map, on vérifie si c'est un code US connu non mappé
        if (isUSExchange(exchange)) {
            return cleanSymbol;
        }

        // Par défaut, on renvoie le symbole tel quel
        return cleanSymbol;
    }

    /**
     * Vérifie si l'échange est un marché US standard (pas de suffixe).
     */
    private static boolean isUSExchange(String exchange) {
        return "NASDAQ".equals(exchange) ||
                "NYSE".equals(exchange) ||
                "AMEX".equals(exchange) ||
                "US".equals(exchange);
    }
}