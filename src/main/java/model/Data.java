package model;

public class Data {
    private final String value;
    private final long expiry;

    public Data(String value) {
        this.value = value;
        this.expiry = Long.MAX_VALUE;
    }

    public Data(String value, long expiry) {
        this.value = value;
        this.expiry = expiry;
    }

    public long getExpiry() {
        return expiry;
    }

    public String getValue () {
        return value;
    }
}