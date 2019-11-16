package com.hackjunction.petra.devices;


import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DeviceModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract DeviceFragment devicesFragment();

    @ActivityScoped
    @Binds
    abstract DeviceContract.Presenter devicesPresenter(DevicePresenter presenter);
}
