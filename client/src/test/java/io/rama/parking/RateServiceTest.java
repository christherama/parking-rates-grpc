package io.rama.parking;

import io.rama.parking.exception.RateNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RateServiceTest {
    private RateService rateService;

    @Mock
    private RateClient rateClient;

    @Before
    public void setup() {
        rateService = new RateService(rateClient);
    }

    @Test
    public void service_whenRateIsAvailable_returnsAccurateRate() throws Exception {
        when(rateClient.getRate(anyLong(),anyLong())).thenReturn(Optional.of(1500));
        DateTimeRange range = DateTimeRange.builder()
                .start("2018-11-08T10:00:00Z")
                .end("2018-11-08T12:00:00Z")
                .build();
        assertThat(rateService.getRate(range),is(1500));
    }

    @Test(expected = RateNotFoundException.class)
    public void service_whenRateIsUnavailable_throwsException() throws Exception {
        when(rateClient.getRate(anyLong(),anyLong())).thenReturn(Optional.empty());
        DateTimeRange range = DateTimeRange.builder()
                .start("2018-11-08T08:00:00Z")
                .end("2018-11-08T12:00:00Z")
                .build();
        rateService.getRate(range);
    }
}