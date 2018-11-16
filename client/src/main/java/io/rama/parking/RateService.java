package io.rama.parking;

import io.rama.parking.exception.RateNotFoundException;
import lombok.Data;


@Data
public class RateService {
    private final RateClient client;

    public RateService(RateClient client) {
        this.client = client;
    }

    public Integer getRate(DateTimeRange range) throws RateNotFoundException {
        long start = range.getStart().toInstant().getEpochSecond();
        long end = range.getEnd().toInstant().getEpochSecond();
        return client.getRate(start,end).orElseThrow(RateNotFoundException::new);
    }

}
