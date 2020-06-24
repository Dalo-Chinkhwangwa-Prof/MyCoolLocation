package com.bigbang.supercoollocation.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.model.network.LocationRetrofitInstance;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LocationRepository {


    private static LocationRepository locationRepository = null;

    private LocationRepository() {
    }

    public static LocationRepository getLocationRepository() {
        if (locationRepository == null)
            locationRepository = new LocationRepository();
        return locationRepository;
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LocationRetrofitInstance locationRetrofitInstance = LocationRetrofitInstance.getLocationRetrofitInstance();

    private MutableLiveData<LocationDetails> locationLiveData = new MutableLiveData<>();

    public MutableLiveData<LocationDetails> getLocationDetails(String latLng) {
        clearDisposables();
        compositeDisposable.add(
                locationRetrofitInstance.getLocationAddress(latLng)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(locationDetails -> {
                            Log.d("TAG_X", "Received - > "+locationDetails.toString());
                            locationLiveData.setValue(locationDetails);
                        }, throwable -> {
                            Log.d("TAG_X", "An error occured! " + throwable.getStackTrace());

                            throwable.printStackTrace();
                        })
        );
        return locationLiveData;
    }

    public void clearDisposables() {
        compositeDisposable.clear();
    }

}
