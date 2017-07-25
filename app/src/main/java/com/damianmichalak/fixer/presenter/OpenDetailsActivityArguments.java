package com.damianmichalak.fixer.presenter;


import java.io.Serializable;

public class OpenDetailsActivityArguments implements Serializable {

    private final String name;
    private final String rating;
    private final String date;

    public OpenDetailsActivityArguments(String name, String rating, String date) {
        this.name = name;
        this.rating = rating;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override

    public String toString() {
        return "OpenDetailsActivityArguments{" +
                "name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenDetailsActivityArguments)) return false;

        OpenDetailsActivityArguments that = (OpenDetailsActivityArguments) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
