package gr.det.spinnovators.EnvDataMODEL;

public class EnvEntry {
    private final String jsonKey;
    private double amount;

    public EnvEntry(String jsonKey, double amount) {
        this.jsonKey = jsonKey;
        this.amount = amount;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{" + jsonKey + ": " + amount + "}";
    }
}
