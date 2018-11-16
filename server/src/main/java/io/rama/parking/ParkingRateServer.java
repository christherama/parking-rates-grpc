package io.rama.parking;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;

@Log
public class ParkingRateServer {
    private final int port;
    private final Server server;
    private static final int PORT = 1234;

    public ParkingRateServer(int port) throws IOException {
        this(port,ParkingRateUtils.getDefaultFilePath());
    }

    public ParkingRateServer(int port, URI uriToJson) throws IOException {
        this(ServerBuilder.forPort(port), port, ParkingRateUtils.parseFromJson(uriToJson));
    }

    public ParkingRateServer(ServerBuilder<?> serverBuilder, int port, Collection<Rate> rates) {
        this.port = port;
        server = serverBuilder.addService(new RateFinderService(rates)).build();
    }

    public void start() throws IOException {
        server.start();
        log.info("Server listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may has been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            ParkingRateServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        ParkingRateServer server;
        if(args.length > 0) {
            server = new ParkingRateServer(PORT,Paths.get(args[0]).toUri());
        } else {
            server = new ParkingRateServer(PORT);
        }
        server.start();
        server.blockUntilShutdown();
    }
}
