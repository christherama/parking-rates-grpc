package io.rama.parking;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Optional;

public class RateClient {
    private final ManagedChannel channel;
    private final RateFinderGrpc.RateFinderBlockingStub blockingStub;

    public RateClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public RateClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = RateFinderGrpc.newBlockingStub(channel);
    }

    public Optional<Integer> getRate(long start, long end) {
        ParkingRatesProtos.RateResponse rate = blockingStub.find(
                ParkingRatesProtos.RateRequest.newBuilder()
                        .setStart(start)
                        .setEnd(end)
                        .build()
        );
        return Optional.ofNullable(rate.getExists() ? rate.getRate() : null);
    }
}
