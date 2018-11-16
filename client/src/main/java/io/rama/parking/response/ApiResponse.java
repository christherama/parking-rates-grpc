package io.rama.parking.response;



public interface ApiResponse {
    default String getStatus() {
        return "success";
    }

    default Integer getRate() {
        return null;
    }
}
