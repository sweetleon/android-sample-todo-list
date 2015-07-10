package com.example.network;

import com.google.gson.annotations.SerializedName;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;
import java.util.Map;

public interface SabreClient {
    @GET("/v1/shop/flights?onlineitinerariesonly=N&limit=10&offset=1&eticketsonly=N&sortby=totalfare&order=asc&sortby2=departuretime&order2=asc&pointofsalecountry=US")
    void searchFlights(@Query("origin") String origin,
                       @Query("destination") String destination,
                       @Query("departuredate") String depDate,
                       @Query("returndate") String rtnDate,
                       Callback<Map> callback);
}
