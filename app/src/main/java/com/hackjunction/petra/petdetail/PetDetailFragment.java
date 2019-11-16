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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

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
    private TextView mDetailTitle;
    private TextView mDetailDescription;
    private CheckBox mDetailCompleteStatus;

    @Inject
    public PetDetailFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.petdetail_frag, container, false);
        setHasOptionsMenu(true);
        mDetailTitle = root.findViewById(R.id.pet_detail_title);
        mDetailDescription = root.findViewById(R.id.pet_detail_description);
        mDetailCompleteStatus = root.findViewById(R.id.pet_detail_complete);

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_pet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editPet();
            }
        });

        return root;
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
            mDetailTitle.setText("");
            mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
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
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void showMissingPet() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}