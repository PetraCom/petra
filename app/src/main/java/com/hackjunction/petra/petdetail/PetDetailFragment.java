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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;
import com.hackjunction.petra.R;
import com.hackjunction.petra.addeditpet.AddEditPetActivity;
import com.hackjunction.petra.addeditpet.AddEditPetFragment;
import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.util.AppExecutors;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private static final int REQUEST_PERMISSION_CODE = 100;

    @Inject
    AppExecutors mAppExecutors;
    @Inject
    String petId;
    @Inject
    PetDetailContract.Presenter mPresenter;
    private TextView mDetailDescription;
    private FloatingActionButton fab;

    private MapView mMapView;
    private GoogleMap googleMap;
    private Marker mMarker;
    private Circle mCircle;
    private GoogleApiClient mGoogleApiClient;

    private ScheduledExecutorService scheduledExecutorService;
    private boolean isAlertShown = false;

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
        scheduledExecutorService.shutdown();
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
        fab = getActivity().findViewById(R.id.fab_edit_pet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startDrawRegion();
            }
        });

        initMap(root, savedInstanceState);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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
                    LatLng petLocation = new LatLng(60.1864925,24.8225124);
                    mMarker = googleMap.addMarker(new MarkerOptions().position(petLocation).title("Marker Title").snippet("Marker Description"));

//                    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                    LatLng petLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                    mMarker = googleMap.addMarker(new MarkerOptions().position(petLocation).title("Marker Title").snippet("Marker Description"));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(mMarker.getPosition())
                            .radius(150)
                            .strokeColor(Color.argb(255, 0, 255, 0))
                            .fillColor(Color.argb(80, 0, 255, 0)); // In meters
                    mCircle = googleMap.addCircle(circleOptions);

//                    scheduleMovePet();




//                    float[] results = new float[5];
//                    Location.distanceBetween(mMarker.getPosition().latitude, mMarker.getPosition().longitude, circle.getCenter().latitude, circle.getCenter().longitude, results);
//                    Toast.makeText(PetDetailFragment.this.getContext(), Arrays.toString(results), Toast.LENGTH_SHORT).show();

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(petLocation).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    Toast.makeText(PetDetailFragment.this.getContext(), "Please give permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(PetDetailFragment.this.getActivity(), new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION },
                            REQUEST_PERMISSION_CODE);
                }
            }
        });
    }

    private void scheduleMovePet() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isAlertShown) {
                    displayNewPosition();
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void displayNewPosition() {
        mAppExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (mMarker != null && mCircle != null) {
                    LatLng pos = mMarker.getPosition();
                    LatLng petLocation = new LatLng(pos.latitude + 0.00001, pos.longitude + 0.00001);
                    mMarker.setPosition(petLocation);

                    float[] results = new float[5];
                    Location.distanceBetween(petLocation.latitude, petLocation.longitude, mCircle.getCenter().latitude, mCircle.getCenter().longitude, results);

                    if (results[0] > 150) {
                        isAlertShown = true;
                        showPetAlertDialog();
                    }
                }
            }
        });
    }

    @Override
    public void startShowDrawRegion() {
        scheduleMovePet();
//        if (fab != null) {
//            fab.setVisibility(View.INVISIBLE);
//            Toast.makeText(PetDetailFragment.this.getContext(), "Select position", Toast.LENGTH_SHORT).show();
//
//            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng point) {
//                    Toast.makeText(PetDetailFragment.this.getContext(), point.toString(), Toast.LENGTH_SHORT).show();
//                    mPresenter.stopDrawRegion();
//                }
//            });
//        }
    }

    @Override
    public void stopShowDrawRegion() {
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
            googleMap.setOnMapClickListener(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
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
            case R.id.menu_edit:
                mPresenter.editPet();
                return true;
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

    @Override
    public void showPetAlertDialog() {
        new AlertDialog.Builder(this.getContext())
                .setTitle("Pet run away")
                .setMessage("Warning. Pet left safe area!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
