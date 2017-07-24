package com.damianmichalak.fixer;

import com.damianmichalak.fixer.model.DateHelper;

import org.junit.Test;

import static com.google.common.truth.Truth.assert_;

public class DateHelperTest {

    @Test
    public void testDates_normalInstance() throws Exception {
        assert_().that(DateHelper.previousDate("2011-04-04")).isEqualTo("2011-04-03");
    }

    @Test
    public void testDates_endOfMonth() throws Exception {
        assert_().that(DateHelper.previousDate("2011-02-01")).isEqualTo("2011-01-31");
    }

    @Test
    public void testDates_endOfYear() throws Exception {
        assert_().that(DateHelper.previousDate("2012-01-01")).isEqualTo("2011-12-31");
    }

    @Test
    public void testDates_leapYear() throws Exception {
        assert_().that(DateHelper.previousDate("2012-02-29")).isEqualTo("2012-02-28");
    }

    @Test
    public void testDates_notLeapYear() throws Exception {
        assert_().that(DateHelper.previousDate("2011-03-01")).isEqualTo("2011-02-28");
    }

    @Test
    public void timeFromMillis1() throws Exception {
        assert_().that(DateHelper.getDateFromMillis(1304632097000l)).isEqualTo("2011-05-05");
    }

    @Test
    public void timeFromMillis2() throws Exception {
        assert_().that(DateHelper.getDateFromMillis(1298929697000l)).isEqualTo("2011-02-28");
    }

    @Test
    public void timeFromMillis3() throws Exception {
        assert_().that(DateHelper.getDateFromMillis(1451598497000l)).isEqualTo("2015-12-31");
    }

}