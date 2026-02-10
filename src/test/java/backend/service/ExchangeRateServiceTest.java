package backend.service;


import backend.exception.ExchangeRateApiException;
import backend.services.ExchangeRateService;
import backend.services.dto.ExchangeRateApiResponse;
import backend.services.dto.ExchangeRateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeRateServiceTest {

    private RestTemplate restTemplate;
    private ExchangeRateService service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        service = new ExchangeRateService(restTemplate);
    }

    @Nested
    class SharedTestData {
        ExchangeRateRequest usdToEur;
        ExchangeRateApiResponse successResponse;
        Map<String, Double> quotes;

        @BeforeEach
        void init() {
            usdToEur = new ExchangeRateRequest("USD", "EUR");

            quotes = new HashMap<>();
            quotes.put("USDEUR", 0.85);
            quotes.put("USDGBP", 0.75);

            successResponse = new ExchangeRateApiResponse();
            successResponse.setSuccess(true);
            successResponse.setQuotes(quotes);
        }
    }

    @Nested
    class GetExchangeRateTests extends SharedTestData {

        @Test
        void testSuccess() {
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class)))
                    .thenReturn(successResponse);

            double rate = service.getExchangeRate(usdToEur);
            assertEquals(0.85, rate);
        }

        @Test
        void testNullRequest() {
            assertThrows(IllegalArgumentException.class, () -> service.getExchangeRate(null));
        }

        @Test
        void testMissingRate() {
            successResponse.setQuotes(new HashMap<>());
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(successResponse);

            assertThrows(ExchangeRateApiException.class, () -> service.getExchangeRate(usdToEur));
        }
    }

    @Nested
    class GetAllRatesTests extends SharedTestData {

        @Test
        void testSuccess() {
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(successResponse);

            Map<String, Double> result = service.getAllRates("USD");
            assertEquals(2, result.size());
            assertEquals(0.85, result.get("USDEUR"));
        }

        @Test
        void testEmptyCurrency() {
            assertThrows(IllegalArgumentException.class, () -> service.getAllRates(""));
        }
    }

    @Nested
    class ConvertMultipleTests extends SharedTestData {

        @Test
        void testSuccess() {
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(successResponse);

            Map<String, Double> result = service.convertMultiple("USD", List.of("EUR", "GBP"), 100);
            assertEquals(2, result.size());
            assertEquals(85.0, result.get("EUR"));
            assertEquals(75.0, result.get("GBP"));
        }

        @Test
        void testInvalidAmount() {
            assertThrows(IllegalArgumentException.class, () -> service.convertMultiple("USD", List.of("EUR"), -10));
        }
    }

    @Nested
    class ConvertTests extends SharedTestData {

        @Test
        void testSuccess() {
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(successResponse);

            double converted = service.convert(usdToEur, 200);
            assertEquals(170.0, converted);
        }

        @Test
        void testInvalidAmount() {
            assertThrows(IllegalArgumentException.class, () -> service.convert(usdToEur, 0));
        }
    }

    @Nested
    class CallExternalApiTests extends SharedTestData {

        @Test
        void testApiFailure() {
            when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(null);

            assertThrows(ExchangeRateApiException.class, () -> service.getExchangeRate(usdToEur));
        }
    }
}