package io.rama.parking;


import io.rama.parking.exception.InvalidDateRangeException;
import org.junit.Test;

import java.time.DateTimeException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateTimeRangeTest {
    @Test(expected = DateTimeException.class)
    public void createRange_whenDateIsInvalid_throwsException() throws Exception {
        DateTimeRange.builder().start("2015-07-01T07:00:00Z").end("invalid-date").build();
    }

    @Test
    public void createsRange() throws Exception {
        DateTimeRange range = DateTimeRange.builder().start("2018-11-13T07:00:00Z").end("2018-11-13T08:01:02Z").build();

        // Start

        assertThat(range.getStart().getYear(),is(2018));
        assertThat(range.getStart().getMonth().getValue(),is(11));
        assertThat(range.getStart().getDayOfMonth(),is(13));
        assertThat(range.getStart().getHour(),is(7));
        assertThat(range.getStart().getMinute(),is(0));
        assertThat(range.getStart().getSecond(),is(0));

        // End
        assertThat(range.getEnd().getYear(),is(2018));
        assertThat(range.getEnd().getMonth().getValue(),is(11));
        assertThat(range.getEnd().getDayOfMonth(),is(13));
        assertThat(range.getEnd().getHour(),is(8));
        assertThat(range.getEnd().getMinute(),is(1));
        assertThat(range.getEnd().getSecond(),is(2));
    }

    @Test(expected = InvalidDateRangeException.class)
    public void createRange_whenRangeSpansMultipleDays_throwsException() throws Exception {
        DateTimeRange.builder().start("2018-07-01T07:00:00Z").end("2018-07-02T09:00:00Z").build();
    }
}