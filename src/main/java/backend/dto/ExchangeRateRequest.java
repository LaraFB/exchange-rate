package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ExchangeRateRequest {
    @Schema(description = "Source currency (ISO 4217 code)", example = "USD")
    private String fromCurrency;

    @Schema(description = "Target currency (ISO 4217 code)", example = "EUR")
    private String toCurrency;

    public ExchangeRateRequest() {
    }

    public ExchangeRateRequest(String fromCurrency, String toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }
}