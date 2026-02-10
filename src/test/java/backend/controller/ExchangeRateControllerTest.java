package backend.controller;

import backend.controllers.ExchangeRateController;
import backend.exception.ApiExceptionHandler;
import backend.exception.ExchangeRateApiException;
import backend.services.ExchangeRateService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ExchangeRateController.class)
@Import(ApiExceptionHandler.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private backend.auth.JwtAuth jwtAuth;

    @MockBean
    private backend.auth.JwtFilter jwtFilter;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    private String jwtToken;

    @Nested
    class GetExchangeRateTests {

        @Test
        void success() throws Exception {
            String requestJson = "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}";
            when(exchangeRateService.getExchangeRate(any())).thenReturn(0.85);

            mockMvc.perform(post("/api/exchange-rate").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0.85"));
        }


        @Test
        void badRequest() throws Exception {
            when(exchangeRateService.getExchangeRate(any())).thenThrow(new IllegalArgumentException("Invalid request"));

            mockMvc.perform(post("/api/exchange-rate").header("Authorization", "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"fromCurrency\":null,\"toCurrency\":\"EUR\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid request"));
        }

        @Test
        void serverError() throws Exception {
            String requestJson = "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}";
            when(exchangeRateService.getExchangeRate(any())).thenThrow(new ExchangeRateApiException("API failure"));

            mockMvc.perform(post("/api/exchange-rate").header("Authorization", "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("API failure"));
        }
    }

    @Nested
    class GetAllRatesTests {

        @Test
        void success() throws Exception {
            Map<String, Double> mockRates = Map.of("USDEUR", 0.85, "USDGBP", 0.75);
            when(exchangeRateService.getAllRates("USD")).thenReturn(mockRates);

            mockMvc.perform(get("/api/rates").header("Authorization", "Bearer " + jwtToken).param("currency", "USD"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.USDEUR").value(0.85))
                    .andExpect(jsonPath("$.USDGBP").value(0.75));
        }

        @Test
        void badRequest() throws Exception {
            when(exchangeRateService.getAllRates(anyString())).thenThrow(new IllegalArgumentException("Currency cannot be empty"));

            mockMvc.perform(get("/api/rates").header("Authorization", "Bearer " + jwtToken).param("currency", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Currency cannot be empty"));
        }

        @Test
        void serverError() throws Exception {
            when(exchangeRateService.getAllRates(anyString())).thenThrow(new ExchangeRateApiException("API failure"));

            mockMvc.perform(get("/api/rates").header("Authorization", "Bearer " + jwtToken).param("currency", "USD"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("API failure"));
        }
    }
    @Nested
    class ConvertMultipleTests {

        private static List<String> targets;
        private static Map<String, Double> converted;

        @BeforeAll
        static void setup() {
            targets = List.of("EUR", "GBP");
            converted = Map.of("EUR", 85.0, "GBP", 75.0);
        }

        @Test
        void success() throws Exception {
            when(exchangeRateService.convertMultiple("USD", targets, 100.0)).thenReturn(converted);

            mockMvc.perform(get("/api/convert-multiple")
                            .header("Authorization", "Bearer " + jwtToken)
                            .param("from", "USD")
                            .param("targets", "EUR")
                            .param("targets", "GBP")
                            .param("amount", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.EUR").value(85.0))
                    .andExpect(jsonPath("$.GBP").value(75.0));
        }

        @Test
        void serverError() throws Exception {
            when(exchangeRateService.convertMultiple(anyString(), anyList(), anyDouble())).thenThrow(new ExchangeRateApiException("Conversion failed"));

            mockMvc.perform(get("/api/convert-multiple")
                            .header("Authorization", "Bearer " + jwtToken)
                            .param("from", "USD")
                            .param("targets", "EUR")
                            .param("amount", "100"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Conversion failed"));
        }
    }
}