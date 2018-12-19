package io.rama.parking;

import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

public class RateFinderService extends RateFinderGrpc.RateFinderImplBase {
    private final Collection<Rate> rates;

    public RateFinderService(Collection<Rate> rates) {
        this.rates = rates;
    }

    @Override
    public void find(ParkingRatesProtos.RateRequest request, StreamObserver<ParkingRatesProtos.RateResponse> responseObserver) {
        DateTimeRange range = new DateTimeRange(request.getStart(), request.getEnd());

        Optional<Rate> rate = rates.stream().filter(r ->
                r.getDaysOfWeek().contains(range.start.getDayOfWeek().getValue()) &&
                        range.fallsWithin(r.getStart(), r.getEnd())
        ).findFirst();
        ParkingRatesProtos.RateResponse response = rate
                .map(r -> ParkingRatesProtos.RateResponse.newBuilder().setRate(r.getRate()).setExists(true).build())
                .orElse(ParkingRatesProtos.RateResponse.newBuilder().setRate(0).setExists(false).build());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static class DateTimeRange {
        private LocalDateTime start;
        private LocalDateTime end;

        public DateTimeRange(long start, long end) {
            this.start = LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC);
            this.end = LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC);
        }

        public boolean fallsWithin(LocalTime start, LocalTime end) {
            return  (
                        start.equals(this.start.toLocalTime()) ||
                        start.isBefore(this.start.toLocalTime())
                    ) && (
                        end.equals(this.end.toLocalTime()) ||
                        end.isAfter(this.end.toLocalTime())
                    );
        }
    }
}
