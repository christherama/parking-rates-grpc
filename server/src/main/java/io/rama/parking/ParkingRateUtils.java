package io.rama.parking;

import com.google.gson.Gson;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.exit;

@Log
public class ParkingRateUtils {

    /**
     * Parses the JSON input file containing the list of features.
     */
    public static List<Rate> parseFromJson(URI file) throws IOException {
        Gson gson = new Gson();
        RateInput rateInput = gson.fromJson(getJsonString(file),RateInput.class);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        return rateInput.getRates().stream().map(in ->
                Rate.builder()
                        .daysOfWeek(
                                Arrays.stream(in.getDays().split(","))
                                        .map(day -> 1 + Arrays.asList("mon","tues","wed","thurs","fri","sat","sun").indexOf(day.toLowerCase()))
                                        .collect(Collectors.toList())
                        )
                        .start(LocalTime.parse(in.getTimes().split("-")[0],timeFormatter))
                        .end(LocalTime.parse(in.getTimes().split("-")[1],timeFormatter))
                        .rate(in.getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    private static String getJsonString(URI uri) {
        try {
            final Map<String, String> env = new HashMap<>();
            final String[] array = uri.toString().split("!");
            final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
            final Path path = fs.getPath(array[1]);
            return Files.lines(path).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.severe("Unable to load parking rates from " + uri.getPath());
            exit(-1);
            return null;
        }
    }

    public static URI getDefaultFilePath() {
        try {
            return ParkingRateUtils.class.getClassLoader().getResource("rates.json").toURI();
        } catch (URISyntaxException e) {
            log.severe("Unable to load parking rates from rates.json");
            exit(-1);
            return null;
        }
    }
}
