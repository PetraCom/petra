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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hackjunction.petra.addeditpet.AddEditPetContract;
import com.hackjunction.petra.addeditpet.AddEditPetFragment;
import com.hackjunction.petra.addeditpet.AddEditPetModule;
import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.data.source.PetsDataSource;
import com.hackjunction.petra.data.source.PetsRepository;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Listens to user actions from the UI ({@link AddEditPetFragment}), retrieves the data and
 * updates
 * the UI as required.
 * <p/>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the AddEditPetPresenter (if it fails, it emits a compiler error). It uses
 * {@link AddEditPetModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually bypassing Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
final class AddEditPetPresenter implements AddEditPetContract.Presenter,
        PetsDataSource.GetPetCallback {

    @NonNull
    private final PetsDataSource mPetsRepository;

    @Nullable
    private AddEditPetContract.View mAddPetView;

    @Nullable
    private String mPetId;

    // This is provided lazily because its value is determined in the Activity's onCreate. By
    // calling it in takeView(), the value is guaranteed to be set.
    private Lazy<Boolean> mIsDataMissingLazy;

    // Whether the data has been loaded with this presenter (or comes from a system restore)
    private boolean mIsDataMissing;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     *
     * @param petId the pet ID or null if it's a new pet
     * @param petsRepository the data source
     * @param shouldLoadDataFromRepo a flag that controls whether we should load data from the
     *                               repository or not. It's lazy because it's determined in the
     *                               Activity's onCreate.
     */
    @Inject
    AddEditPetPresenter(@Nullable String petId, @NonNull PetsRepository petsRepository,
                        Lazy<Boolean> shouldLoadDataFromRepo) {
        mPetId = petId;
        mPetsRepository = petsRepository;
        mIsDataMissingLazy = shouldLoadDataFromRepo;
    }

    @Override
    public void savePet(String title, String description) {
        if (isNewPet()) {
            createPet(title, description);
        } else {
            updatePet(title, description);
        }
    }

    @Override
    public void populatePet() {
        if (isNewPet()) {
            throw new RuntimeException("populatePet() was called but pet is new.");
        }
        mPetsRepository.getPet(mPetId, this);
    }

    @Override
    public void takeView(AddEditPetContract.View view) {
        mAddPetView = view;
        mIsDataMissing = mIsDataMissingLazy.get();
        if (!isNewPet() && mIsDataMissing) {
            populatePet();
        }
    }

    @Override
    public void dropView() {
        mAddPetView = null;
    }

    @Override
    public void onPetLoaded(Pet pet) {
        // The view may not be able to handle UI updates anymore
        if (mAddPetView != null && mAddPetView.isActive()) {
            mAddPetView.setTitle(pet.getName());
            mAddPetView.setDescription(pet.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddPetView != null && mAddPetView.isActive()) {
            mAddPetView.showEmptyPetError();
        }
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewPet() {
        return mPetId == null;
    }

    private void createPet(String title, String description) {
        Pet newPet = new Pet(title, description);
        if (newPet.isEmpty()) {
            if (mAddPetView != null) {
                mAddPetView.showEmptyPetError();
            }
        } else {
            mPetsRepository.savePet(newPet);
            if (mAddPetView != null) {
                mAddPetView.showPetsList();
            }
        }
    }

    private void updatePet(String title, String description) {
        if (isNewPet()) {
            throw new RuntimeException("updatePet() was called but pet is new.");
        }
        mPetsRepository.savePet(new Pet(title, description, mPetId));
        if (mAddPetView != null) {
            mAddPetView.showPetsList(); // After an edit, go back to the list.
        }
    }
}
