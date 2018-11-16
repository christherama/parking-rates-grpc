package io.rama.parking;

import lombok.Data;

@Data
public class RateDto {
    private String days;
    private String times;
    private int price;
}
