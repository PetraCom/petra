package com.hackjunction.petra.devices;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.hackjunction.petra.R;
import com.hackjunction.petra.devices.dummy.DummyContent;
import com.hackjunction.petra.util.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class DevicesActivity extends DaggerAppCompatActivity implements DeviceFragment.OnListFragmentInteractionListener {

    @Inject
    DeviceFragment mFragment;

    @Inject
    DeviceContract.Presenter mDeviceContractPresenter;

    Toolbar mActionBarToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_act);

        DeviceFragment deviceFragment =
                (DeviceFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (deviceFragment == null) {
            deviceFragment = mFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    deviceFragment, R.id.contentFrame);
        }

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Devices");
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
