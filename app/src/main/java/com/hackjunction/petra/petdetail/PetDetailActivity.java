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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.hackjunction.petra.R;
import com.hackjunction.petra.petdetail.PetDetailFragment;
import com.hackjunction.petra.util.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Displays pet details screen.
 */
public class PetDetailActivity extends DaggerAppCompatActivity {
    public static final String EXTRA_PET_ID = "PET_ID";
    @Inject
    PetDetailFragment injectedFragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.petdetail_act);

        // Set up the toolbar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        PetDetailFragment petDetailFragment = (PetDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (petDetailFragment == null) {
            petDetailFragment = injectedFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    petDetailFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }
}
