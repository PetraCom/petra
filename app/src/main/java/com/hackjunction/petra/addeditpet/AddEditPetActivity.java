/*
 * Copyright 2016, The Android Open Source Project
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

package com.hackjunction.petra.addeditpet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.hackjunction.petra.R;
import com.hackjunction.petra.addeditpet.AddEditPetContract;
import com.hackjunction.petra.addeditpet.AddEditPetFragment;
import com.hackjunction.petra.util.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Displays an add or edit pet screen.
 */
public class AddEditPetActivity extends DaggerAppCompatActivity {

    public static final int REQUEST_ADD_PET = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    @Inject
    AddEditPetContract.Presenter mAddEditPetsPresenter;
    
    @Inject
    AddEditPetFragment mFragment;

    @Inject
    @Nullable
    String mPetId;

    private ActionBar mActionBar;

    // In a rotation it's important to know if we want to let the framework restore view state or
    // need to load data from the repository. This is saved into the state bundle.
    private boolean mIsDataMissing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpet_act);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        setToolbarTitle(mPetId);

        AddEditPetFragment addEditPetFragment =
                (AddEditPetFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (addEditPetFragment == null) {
            addEditPetFragment = mFragment;

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditPetFragment, R.id.contentFrame);
        }
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            mIsDataMissing = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }
    }

    private void setToolbarTitle(@Nullable String petId) {
        if(petId == null) {
            mActionBar.setTitle(R.string.add_pet);
        } else {
            mActionBar.setTitle(R.string.edit_pet);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the state so that next time we know if we need to refresh data.
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddEditPetsPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    boolean isDataMissing() {
        return mIsDataMissing;
    }
}
