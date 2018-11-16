package io.rama.parking;

import io.rama.parking.exception.InvalidDateRangeException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeRange {
    private ZonedDateTime start;
    private ZonedDateTime end;

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    private DateTimeRange(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static DateTimeRangeBuilder builder() {
        return new DateTimeRangeBuilder();
    }

    public static class DateTimeRangeBuilder {
        private ZonedDateTime start;
        private ZonedDateTime end;

        public DateTimeRangeBuilder start(String start) throws DateTimeParseException {
            try {
                this.start = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
            } catch (NullPointerException ex) {
                throw new DateTimeParseException("Start date not provided","null",0);
            }

            return this;
        }

        public DateTimeRangeBuilder end(String end) throws DateTimeParseException {
            try {
                this.end = ZonedDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
            } catch (NullPointerException ex) {
                throw new DateTimeParseException("End date not provided","null",0);
            }
            return this;
        }

        public DateTimeRange build() {
            if(start.getYear() != end.getYear() || start.getDayOfYear() != end.getDayOfYear()) {
                throw new InvalidDateRangeException();
            }
            return new DateTimeRange(start,end);
        }
    }
}
