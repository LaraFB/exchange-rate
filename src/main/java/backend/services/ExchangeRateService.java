package backend.services;

import backend.services.dto.ExchangeRateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.url}")
    private String EXCHANGERATE_API_URL;

    @Value("${exchangerate.api.key}")
    private String EXCHANGEAPI_KEY;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getExchangeRate(ExchangeRateRequest request) {
        String fromCurrency = request.getFromCurrency().toUpperCase(Locale.ROOT);
        String toCurrency = request.getToCurrency().toUpperCase(Locale.ROOT);
        String url =  EXCHANGERATE_API_URL + "?access_key=" + EXCHANGEAPI_KEY + "&symbols=" + fromCurrency + "," + toCurrency;

        ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);
        
        if (response != null && response.isSuccess()) {
            String pair = fromCurrency + toCurrency;
            return response.getQuotes().getOrDefault(pair, 0.0);
        }

        throw new RuntimeException("Failed to fetch exchange rate");
    }

    public Map<String, Double> getAllRates(String currency) {
        currency = currency.toUpperCase(Locale.ROOT);
        String url =  EXCHANGERATE_API_URL + "?access_key=" + EXCHANGEAPI_KEY + "&symbols=" + currency;

        ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch rates");
        }

        return response.getQuotes();
    }

    public Map<String, Double> convertMultiple(String fromCurrency, List<String> targets, double amount) {
        fromCurrency = fromCurrency.toUpperCase(Locale.ROOT);
        String toCurrencies = String.join(",", targets).toUpperCase();
        String url = EXCHANGERATE_API_URL + "?access_key=" + EXCHANGEAPI_KEY + "&symbols=" + fromCurrency + "," + toCurrencies;

        ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Conversion failed");
        }

        Map<String, Double> result = new HashMap<>();

        for (String target : targets) {
            String key = fromCurrency + target.toUpperCase();
            result.put(target.toUpperCase(), response.getQuotes().get(key) * amount);
        }

        return result;
    }

}