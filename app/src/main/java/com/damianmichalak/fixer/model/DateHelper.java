package com.damianmichalak.fixer.model;

import java.util.Calendar;

import javax.inject.Inject;

public class DateHelper {

    @Inject
    DateHelper() {
    }

    public String today() {
        return getDateFromMillis(System.currentTimeMillis());
    }

    public static String getDateFromMillis(long millis) {
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);

        final int day = instance.get(Calendar.DAY_OF_MONTH);
        final int month = instance.get(Calendar.MONTH) + 1;
        final int year = instance.get(Calendar.YEAR);

        final String dayString = (day < 10) ? ("0" + day) : String.valueOf(day);
        final String monthString = (month < 10) ? ("0" + month) : String.valueOf(month);

        return year + "-" + monthString + "-" + dayString;
    }

    public static String previousDate(String previousDate) {
        final int day = Integer.parseInt(previousDate.substring(8, 10));
        final int month = Integer.parseInt(previousDate.substring(5, 7)) - 1;
        final int year = Integer.parseInt(previousDate.substring(0, 4));

        final Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, day);
        instance.set(Calendar.MONTH, month);
        instance.set(Calendar.YEAR, year);

        instance.add(Calendar.DAY_OF_YEAR, -1);

        return getDateFromMillis(instance.getTimeInMillis());
    }

}
