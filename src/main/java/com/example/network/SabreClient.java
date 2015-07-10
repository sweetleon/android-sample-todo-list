package com.example.network;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SabreClient {
    @GET("/v1/shop/flights/fares?onlineitinerariesonly=N&limit=1&offset=1&eticketsonly=N&sortby=totalfare&order=asc&sortby2=departuretime&order2=asc&pointofsalecountry=US&lengthofstay=10")
    void searchFlights(@Query("origin") String origin,
                       @Query("destination") String destination,
                       @Query("departuredate") String depDate,
                       @Query("returndate") String rtnDate,
                       Callback<FlightSearchResponse> callback);

    class FlightSearchResponse {
    }
}
