package com.hackjunction.petra.devices;

import android.support.annotation.Nullable;

import com.hackjunction.petra.addeditpet.AddEditPetContract;

import javax.inject.Inject;

import dagger.Lazy;

public class DevicePresenter implements DeviceContract.Presenter {
    @Nullable
    private DeviceContract.View mAddPetView;

    @Inject
    public DevicePresenter() {};

    @Override
    public void takeView(DeviceContract.View view) {
        mAddPetView = view;
    }

    public void dropView() {
        mAddPetView = null;
    }
}
