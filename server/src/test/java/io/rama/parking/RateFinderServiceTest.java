package io.rama.parking;

import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RateFinderServiceTest {
    private static final long DEC_18_2018_0800_UTC = 1545033600L;
    private static final long DEC_18_2018_1600_UTC = 1545062400L;

    private static final LocalTime EIGHT_AM = LocalTime.of(8,0,0);
    private static final LocalTime FOUR_PM = LocalTime.of(16,0,0);

    private List<Rate> rates;
    private RateFinderService rateFinderService;

    @Before
    public void setup() {
        rates = new ArrayList<>();
        rates.add(Rate.builder().daysOfWeek(Arrays.asList(0,1,2)).rate(10).start(EIGHT_AM).end(FOUR_PM).build());
        rateFinderService = new RateFinderService(rates);
    }

    @Test
    public void fallsWithin_whenUnderlyingRangeFallsWithinProvidedRange_returnsFalse() throws Exception {
        RateFinderService.DateTimeRange rangeToCheck = new RateFinderService.DateTimeRange(DEC_18_2018_0800_UTC, DEC_18_2018_1600_UTC);
        LocalTime start = LocalTime.of(8,0,0);
        LocalTime end = LocalTime.of(16,0,0);
        assertTrue(rangeToCheck.fallsWithin(start,end));
    }

    @Test
    public void fallsWithin_whenUnderlyingRangeDoesNotFallWithinProvidedRange_returnsFalse() throws Exception {
        RateFinderService.DateTimeRange rangeToCheck = new RateFinderService.DateTimeRange(DEC_18_2018_0800_UTC, DEC_18_2018_1600_UTC + 1);
        LocalTime start = LocalTime.of(8,0,0);
        LocalTime end = LocalTime.of(16,0,0);
        assertFalse(rangeToCheck.fallsWithin(start,end));
    }

    @Test
    public void find_whenMatchIsFound_returnsRate() throws Exception {
        MockRateResponseStreamObserver observer = new MockRateResponseStreamObserver();
        ParkingRatesProtos.RateRequest request = ParkingRatesProtos.RateRequest.newBuilder().setStart(DEC_18_2018_0800_UTC).setEnd(DEC_18_2018_1600_UTC).build();
        rateFinderService.find(request,observer);
        assertThat(observer.getRate(), is(10));
    }

    @Test
    public void find_whenMatchIsNotFound_returnsZeroRate() throws Exception {
        MockRateResponseStreamObserver observer = new MockRateResponseStreamObserver();
        ParkingRatesProtos.RateRequest request = ParkingRatesProtos.RateRequest.newBuilder().setStart(DEC_18_2018_0800_UTC).setEnd(1 + DEC_18_2018_1600_UTC).build();
        rateFinderService.find(request,observer);
        assertThat(observer.getRate(), is(0));
    }

    private static class MockRateResponseStreamObserver implements StreamObserver<ParkingRatesProtos.RateResponse> {
        private int rate;

        public int getRate() {
            return rate;
        }

        @Override
        public void onNext(ParkingRatesProtos.RateResponse value) {
            this.rate = value.getRate();
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {

        }
    }
}
