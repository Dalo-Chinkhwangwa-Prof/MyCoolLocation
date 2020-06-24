package com.bigbang.supercoollocation.model.network;

import com.bigbang.supercoollocation.model.data.LocationDetails;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bigbang.supercoollocation.util.Constants.API_KEY;
import static com.bigbang.supercoollocation.util.Constants.BASE_URL;

public class LocationRetrofitInstance {

    private static LocationRetrofitInstance locationRetrofitInstance = null;
    private LocationService locationService;

    private LocationRetrofitInstance() {
        locationService = createLocationService(getRetrofitInstance());
    }

    private LocationService createLocationService(Retrofit retrofitInstance) {
        return retrofitInstance.create(LocationService.class);
    }

    public static LocationRetrofitInstance getLocationRetrofitInstance() {
        if (locationRetrofitInstance == null)
            locationRetrofitInstance = new LocationRetrofitInstance();
        return locationRetrofitInstance;
    }

    private Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Observable<LocationDetails> getLocationAddress(String latLng) {
        return locationService.getLocationDetails(latLng, API_KEY);
    }

}
