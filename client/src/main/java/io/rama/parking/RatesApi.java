package io.rama.parking;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.rama.parking.exception.InvalidDateRangeException;
import io.rama.parking.exception.RateNotFoundException;
import io.rama.parking.response.ApiResponse;
import io.rama.parking.response.ErrorResponse;
import io.rama.parking.response.SuccessResponse;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;
import static spark.Spark.get;

@Log
public class RatesApi {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private final RateService service;

    private final MetricRegistry metrics = new MetricRegistry();
    private final Timer responses = metrics.timer(name("GET /rates"));

    public RatesApi(RateService service) {
        this.service = service;

        // GET /rates
        get("/rates", Type.JSON, this::handleRatesRequest, this::json);
        get("/rates", Type.XML, this::handleRatesRequest, this::xml);

        // GET /metrics
        get("/metrics", Type.JSON, this::handleMetricsRequest, this::json);
        get("/metrics", Type.XML, this::handleMetricsRequest, this::xml);
    }

    public RatesApi() {
        this(new RateService(new RateClient("server",1234)));
    }

    public static void main(String[] args) {
        new RatesApi();
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private String xml(Object o) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(o);
    }

    private ApiResponse handleRatesRequest(Request req, Response res) {
        final Timer.Context context = responses.time();
        setResponseType(req,res);
        try {
            DateTimeRange range = DateTimeRange.builder()
                    .start(req.queryParams("start"))
                    .end(req.queryParams("end"))
                    .build();
            Integer rate = service.getRate(range);
            res.status(200);
            return new SuccessResponse(rate);
        } catch (DateTimeParseException ex) {
            res.status(400);
            return new ErrorResponse("Invalid time(s) provided");
        } catch (InvalidDateRangeException ex) {
            res.status(400);
            return new ErrorResponse("Time range cannot span multiple days");
        } catch (RateNotFoundException ex) {
            res.status(404);
            return new ErrorResponse("No rates available during the provided time range");
        } finally {
            context.stop();
        }
    }

    private Object handleMetricsRequest(Request req, Response res) {
        setResponseType(req,res);
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("metrics",this.metrics.getTimers());
        return metrics;
    }

    private void setResponseType(Request req, Response res) {
        switch(req.headers("Accept").toLowerCase()) {
            case Type.XML:
                res.type(Type.XML);
                break;
            case Type.JSON:
            default:
                res.type(Type.JSON);
                break;
        }
    }

    private static class Type {
        private static final String JSON = "application/json";
        private static final String XML = "application/xml";
    }
}
