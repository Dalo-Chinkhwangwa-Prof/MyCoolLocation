package com.bigbang.supercoollocation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bigbang.supercoollocation.model.LocationRepository;
import com.bigbang.supercoollocation.model.data.LocationDetails;

public class LocationViewModel extends ViewModel {

    private LocationRepository locationRepository = LocationRepository.getLocationRepository();

    public LiveData<LocationDetails> getLocationDetails(String latLng){
        return locationRepository.getLocationDetails(latLng);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        locationRepository.clearDisposables();
    }
}
