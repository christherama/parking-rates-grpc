package io.rama.parking.response;

public class ErrorResponse implements ApiResponse {
    private String status;

    public ErrorResponse(String status) {
        this.status = status;
    }

    @Override
    public String getStatus() {
        return status;
    }
}
