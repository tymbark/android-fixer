package com.damianmichalak.fixer.model;

import java.util.Map;

public class FixerResponse {
    private final String base;
    private final String date;
    private final Map<String, Float> rates;

    public FixerResponse(String base, String date, Map<String, Float> rates) {
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public Map<String, Float> getRates() {
        return rates;
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixerResponse)) return false;

        FixerResponse that = (FixerResponse) o;

        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return rates != null ? rates.equals(that.rates) : that.rates == null;

    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (rates != null ? rates.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FixerResponse{" +
                "base='" + base + '\'' +
                ", date='" + date + '\'' +
                ", rates=" + rates +
                '}';
    }
}
