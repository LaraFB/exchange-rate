package backend.services.dto;

public class ExchangeRateRequest {

    private String base;
    private String target;

    public ExchangeRateRequest() {
    }

    public ExchangeRateRequest(String base, String target) {
        this.base = base;
        this.target = target;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}