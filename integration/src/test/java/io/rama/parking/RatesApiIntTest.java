package io.rama.parking;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.rama.parking.ApiClient.ApiRateResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RatesApiIntTest {
    private ApiClient apiClient;

    @Before
    public void setup() {
        apiClient = new ApiClient("http://localhost:4567");
    }

    @Test
    public void rateEndpoint_whenDateRangeHasRate_respondsWithRate() throws Exception{
        ApiRateResponse response = apiClient.get("/rate?start=2018-11-13T09:00:00Z&end=2018-11-13T10:01:02Z");
        assertThat(response.getCode(),is(200));
        assertThat(response.getRate().getRate(),is(1500));
    }

    @Test
    public void rateEndpoint_whenDateIsInvalid_respondsWith400() throws Exception {
        ApiRateResponse response = apiClient.get("/rate?start=2018-11-13T09:00:00Z&end=hello");
        assertThat(response.getCode(),is(400));
    }

    @Test
    public void rateEndpoint_whenRateIsNotFound_respondsWith404() throws Exception {
        ApiRateResponse response = apiClient.get("/rate?start=2018-11-13T09:00:00Z&end=2018-11-13T23:01:02Z");
        assertThat(response.getCode(),is(404));
    }
}
