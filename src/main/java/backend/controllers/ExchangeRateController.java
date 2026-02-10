package backend.controllers;

import backend.services.ExchangeRateService;
import backend.services.dto.ExchangeRateRequest;
import backend.services.dto.ExchangeRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Exchange Rates", description = "Currency exchange and conversion endpoints")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(
            summary = "Get exchange rate between two currencies",
            description = "Returns the exchange rate from one currency to another"
    )
    @PostMapping("/exchange-rate")
    public double getExchangeRate(@RequestBody ExchangeRateRequest request) {
        return exchangeRateService.getExchangeRate(request);
    }

    @Operation(
            summary = "Get all exchange rates from a base currency",
            description = "Returns all available exchange rates using the given base currency"
    )
    @GetMapping("/rates")
    public Map<String, Double> getAllRates(@RequestParam String currency) {
        return exchangeRateService.getAllRates(currency);
    }

    @Operation(
            summary = "Convert currency",
            description = "Converts an amount from one currency to another"
    )
    @PostMapping("/convert")
    public double convert(@RequestBody ExchangeRateRequest request, @RequestParam(defaultValue = "1") double amount) {

        return exchangeRateService.convert(request, amount);
    }

    @Operation(
            summary = "Convert currency to multiple targets",
            description = "Converts an amount from a base currency to multiple target currencies"
    )
    @GetMapping("/convert-multiple")
    public Map<String, Double> convertMultiple(@RequestParam String from, @RequestParam List<String> targets,
                                               @RequestParam(defaultValue = "1") double amount) {
        return exchangeRateService.convertMultiple(from, targets, amount);
    }
}