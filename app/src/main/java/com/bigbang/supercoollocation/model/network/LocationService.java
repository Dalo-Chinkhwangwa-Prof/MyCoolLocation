package com.bigbang.supercoollocation.model.network;

import com.bigbang.supercoollocation.model.data.LocationDetails;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {
    @GET("maps/api/geocode/json")
    public Observable<LocationDetails> getLocationDetails(@Query("latlng") String latLong, @Query("key") String apiKey);
}
