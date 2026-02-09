package backend.controllers;

import backend.services.ExchangeRateService;
import backend.services.dto.ExchangeRateRequest;
import backend.services.dto.ExchangeRateResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange-rate")
    public double getExchangeRate(@RequestParam ExchangeRateRequest request) {
        return exchangeRateService.getExchangeRate(request);
    }

    @GetMapping("/rates")
    public Map<String, Double> getAllRates(@RequestParam String currency) {
        return exchangeRateService.getAllRates(currency);
    }

    @GetMapping("/convert")
    public ExchangeRateResponse convert(@RequestParam ExchangeRateRequest request, @RequestParam(defaultValue = "1") double amount) {

        double rate = exchangeRateService.getExchangeRate(request);
        double value = rate * amount;

        return new ExchangeRateResponse(request.getFromCurrency(),request.getToCurrency(), value);
    }

    @GetMapping("/convert-multiple")
    public Map<String, Double> convertMultiple(@RequestParam String from, @RequestParam List<String> targets,
                                               @RequestParam(defaultValue = "1") double amount) {

        return exchangeRateService.convertMultiple(from, targets, amount);
    }
}