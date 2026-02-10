package backend.services;

import backend.exception.ExchangeRateApiException;
import backend.dto.ExchangeRateApiResponse;
import backend.dto.ExchangeRateRequest;
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
        if (request == null || request.getFromCurrency() == null || request.getToCurrency() == null) {
            throw new IllegalArgumentException("Invalid request");
        }

        String from = normalize(request.getFromCurrency());
        String to = normalize(request.getToCurrency());

        ExchangeRateApiResponse response = callExternalApi(from, to);

        if (!response.isSuccess()) throw new ExchangeRateApiException("Failed to fetch exchange rate");

        Double rate = response.getQuotes().get(from + to);
        if (rate == null) throw new ExchangeRateApiException("Rate not found for " + from + to);

        return rate;
    }

    public Map<String, Double> getAllRates(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }

        String base = normalize(currency);
        ExchangeRateApiResponse response = callExternalApi(base, null);

        return response.getQuotes();
    }

    public Map<String, Double> convertMultiple(String fromCurrency, List<String> targets, double amount) {
        if (fromCurrency == null || fromCurrency.isEmpty() || targets == null || targets.isEmpty() || amount <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        fromCurrency = normalize(fromCurrency);
        String toSymbols = String.join(",", targets).toUpperCase(Locale.ROOT);

        ExchangeRateApiResponse response = callExternalApi(fromCurrency, toSymbols);

        Map<String, Double> result = new HashMap<>();
        for (String target : targets) {
            String key = fromCurrency + normalize(target);
            Double rate = response.getQuotes().get(key);

            if (rate == null) throw new ExchangeRateApiException("Rate not found for " + key);

            result.put(normalize(target), rate * amount);
        }

        return result;
    }

    public double convert(ExchangeRateRequest request, double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        double rate = this.getExchangeRate(request);

        if(rate <0.0) {
            throw new ExchangeRateApiException("Invalid rate");
        }

        return rate * amount;
    }

    private ExchangeRateApiResponse callExternalApi(String fromCurrency, String toCurrency) {
        String url = EXCHANGERATE_API_URL + "?access_key=" + EXCHANGEAPI_KEY + "&base=" + fromCurrency;

        if (toCurrency != null && !toCurrency.isEmpty()) {
            url += "&symbols=" + toCurrency;
        }

        ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new ExchangeRateApiException("Failed to fetch exchange rates for base: " + fromCurrency);
        }

        return response;
    }

    private String normalize(String currency) {
        return currency.toUpperCase(Locale.ROOT);
    }
}