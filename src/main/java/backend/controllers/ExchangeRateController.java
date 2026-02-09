package backend.controllers;

import backend.services.ExchangeRateService;
import backend.services.dto.ExchangeRateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange-rate")
    public double getExchangeRate(ExchangeRateRequest request) {
        return exchangeRateService.getExchangeRate(request.getBase(), request.getTarget());
    }
}