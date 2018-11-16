package io.rama.parking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class Rate {
    private List<Integer> daysOfWeek;
    private LocalTime start;
    private LocalTime end;
    private int rate;
}
