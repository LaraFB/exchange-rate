package backend.dto;

public class ExchangeRateResponse {
    private final String base;
    private final String target;
    private final double rate;

    public ExchangeRateResponse(String base, String target, double rate) {
        this.base = base;
        this.target = target;
        this.rate = rate;
    }

    public String getBase() {
        return base;
    }

    public String getTarget() {
        return target;
    }

    public double getRate() {
        return rate;
    }
}
