package backend.services;

import backend.services.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Locale;

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

    public double getExchangeRate(String base, String target) {
        base = base.toUpperCase(Locale.ROOT);
        target = target.toUpperCase(Locale.ROOT);

        System.out.println("base: " + base);
        System.out.println("target: " + target);
        String url = EXCHANGERATE_API_URL + "?access_key=" + EXCHANGEAPI_KEY + "&symbols=" + base + "," + target;
        System.out.println("url: " + url);
        ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);
        
        if (response != null && response.isSuccess()) {
            String pair = base + target;
            return response.getQuotes().getOrDefault(pair, 0.0);
        }

        throw new RuntimeException("Failed to fetch exchange rate");
    }
}