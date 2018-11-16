package io.rama.parking;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RatesApiTest {
    public static class ParkingRatesApiTestApplication implements SparkApplication {
        private RateService rateService = mock(RateService.class);

        public RateService getRateService() {
            return rateService;
        }

        @Override
        public void init() {
            new RatesApi(rateService);
        }
    }

    @ClassRule
    public static SparkServer<ParkingRatesApiTestApplication> testServer = new SparkServer<>(ParkingRatesApiTestApplication.class, 4567);

    @Test
    public void ratesEndpoint_servesJson() throws Exception {
        GetMethod getRates = testServer.get("/rates?start=2015-07-01T07:00:00Z&end=2015-07-01T12:00:00Z", false);
        getRates.addHeader("Accept","application/json");
        HttpResponse response = testServer.execute(getRates);
        assertThat(response.headers().get("Content-Type").get(0), is("application/json"));
    }

    @Test
    public void ratesEndpoint_servesXml() throws Exception {
        GetMethod getRates = testServer.get("/rates?start=2015-07-01T07:00:00Z&end=2015-07-01T12:00:00Z", false);
        getRates.addHeader("Accept","application/xml");
        HttpResponse response = testServer.execute(getRates);
        assertThat(response.headers().get("Content-Type").get(0), is("application/xml"));
    }

    @Test
    public void ratesEndpoint_whenDateRangeHasRate_respondsWithRate() throws Exception{
        when(testServer.getApplication().getRateService().getRate(any())).thenReturn(1500);
        GetMethod getRates = testServer.get("/rates?start=2018-11-13T09:00:00Z&end=2018-11-13T10:01:02Z", false);
        getRates.addHeader("Accept","application/json");
        HttpResponse response = testServer.execute(getRates);
        RateDto dto = new ObjectMapper().readValue(response.body(), RateDto.class);
        assertThat(response.code(),is(200));
        assertThat(dto.getRate(),is(1500));
    }

    @Test
    public void ratesEndpoint_whenDateIsInvalid_respondsWith400() throws Exception {
        GetMethod getRates = testServer.get("/rates?start=2015-07-01T07:00:00Z&end=hello", false);
        HttpResponse response = testServer.execute(getRates);
        assertThat(response.code(),is(400));
    }

    @Test
    public void ratesEndpoint_whenDateRangeSpansMultipleDays_respondsWith400() throws Exception {
        GetMethod getRates = testServer.get("/rates?start=2018-07-01T07:00:00Z&end=2018-07-02T09:00:00Z", false);
        HttpResponse response = testServer.execute(getRates);
        assertThat(response.code(), is(400));
    }

    @Data
    private static class RateDto {
        private String status;
        private Integer rate;
    }
}
