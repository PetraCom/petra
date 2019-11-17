/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackjunction.petra.petdetail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Preconditions;
import com.hackjunction.petra.R;
import com.hackjunction.petra.addeditpet.AddEditPetActivity;
import com.hackjunction.petra.addeditpet.AddEditPetFragment;
import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.petdetail.PetDetailContract;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * Main UI for the pet detail screen.
 */
@ActivityScoped
public class PetDetailFragment extends DaggerFragment implements PetDetailContract.View {

    @NonNull
    private static final String ARGUMENT_PET_ID = "PET_ID";

    @NonNull
    private static final int REQUEST_EDIT_PET = 1;
    @Inject
    String petId;
    @Inject
    PetDetailContract.Presenter mPresenter;
    private TextView mDetailDescription;

    MapView mMapView;
    private GoogleMap googleMap;

    @Inject
    public PetDetailFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.petdetail_frag, container, false);
        setHasOptionsMenu(true);
        mDetailDescription = root.findViewById(R.id.pet_detail_description);

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_pet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editPet();
            }
        });

        initMap(root, savedInstanceState);

        return root;
    }

    private void initMap(View rootView, Bundle savedInstanceState) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ContextCompat.checkSelfPermission(PetDetailFragment.this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(PetDetailFragment.this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {

                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    Toast.makeText(PetDetailFragment.this.getContext(), "Please give permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(PetDetailFragment.this.getActivity(), new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION },
                            123);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PetDetailFragment.this.getContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetDetailFragment.this.getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deletePet();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.petdetail_fragment_menu, menu);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        Activity activity = this.getActivity();
        if (activity instanceof PetDetailActivity) {
            ((PetDetailActivity) activity).setTitle("");
        }
    }

    @Override
    public void showDescription(@NonNull String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showEditPet(@NonNull String petId) {
        Intent intent = new Intent(getContext(), AddEditPetActivity.class);
        intent.putExtra(AddEditPetFragment.ARGUMENT_EDIT_PET_ID, petId);
        startActivityForResult(intent, REQUEST_EDIT_PET);
    }

    @Override
    public void showPetDeleted() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_PET) {
            // If the pet was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void showTitle(@NonNull String title) {
        Activity activity = this.getActivity();
        if (activity instanceof PetDetailActivity) {
            ((PetDetailActivity) activity).setTitle(title);
        }
    }

    @Override
    public void showMissingPet() {
        mDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
