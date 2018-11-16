package io.rama.parking.response;

public class SuccessResponse implements ApiResponse {
    private final Integer rate;

    public SuccessResponse(Integer rate) {
        this.rate = rate;
    }

    @Override
    public Integer getRate() {
        return rate;
    }
}
