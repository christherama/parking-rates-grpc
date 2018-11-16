package io.rama.parking;

import lombok.Data;

import java.util.List;

@Data
public class RateInput {
    private List<RateDto> rates;
}
