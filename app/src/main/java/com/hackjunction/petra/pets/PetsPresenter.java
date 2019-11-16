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

package com.hackjunction.petra.pets;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.hackjunction.petra.addeditpet.AddEditPetActivity;
import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.data.source.PetsDataSource;
import com.hackjunction.petra.data.source.PetsRepository;
import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Listens to user actions from the UI ({@link PetsFragment}), retrieves the data and updates the
 * UI as required.
 * <p/>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the PetsPresenter (if it fails, it emits a compiler error).  It uses
 * {@link PetsModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
@ActivityScoped
final class PetsPresenter implements PetsContract.Presenter {

    private final PetsRepository mPetsRepository;
    @Nullable
    private PetsContract.View mPetsView;

    private boolean mFirstLoad = true;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    PetsPresenter(PetsRepository petsRepository) {
        mPetsRepository = petsRepository;
    }


    @Override
    public void result(int requestCode, int resultCode) {
//         If a pet was successfully added, show snackbar
        if (AddEditPetActivity.REQUEST_ADD_PET == requestCode
                && Activity.RESULT_OK == resultCode) {
            if (mPetsView != null) {
                mPetsView.showSuccessfullySavedMessage();
            }
        }
    }

    @Override
    public void loadPets(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadPets(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link PetsDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadPets(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            if (mPetsView != null) {
                mPetsView.setLoadingIndicator(true);
            }
        }
        if (forceUpdate) {
            mPetsRepository.refreshPets();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mPetsRepository.getPets(new PetsDataSource.LoadPetsCallback() {
            @Override
            public void onPetsLoaded(List<Pet> pets) {
                List<Pet> petsToShow = new ArrayList<>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the pets based on the requestType
                for (Pet pet : pets) {
                    petsToShow.add(pet);
                }
                // The view may not be able to handle UI updates anymore
                if (mPetsView == null || !mPetsView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mPetsView.setLoadingIndicator(false);
                }

                processPets(petsToShow);
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mPetsView.isActive()) {
                    return;
                }
                mPetsView.showLoadingPetsError();
            }
        });
    }

    private void processPets(List<Pet> pets) {
        if (pets.isEmpty()) {
            // Show a message indicating there are no pets for that filter type.
            if (mPetsView != null)
                mPetsView.showNoPets();
        } else {
            // Show the list of pets
            if (mPetsView != null) {
                mPetsView.showPets(pets);
            }
        }
    }

    @Override
    public void addNewPet() {
        if (mPetsView != null) {
            mPetsView.showAddPet();
        }
    }

    @Override
    public void openPetDetails(@NonNull Pet requestedPet) {
        checkNotNull(requestedPet, "requestedPet cannot be null!");
        if (mPetsView != null) {
            mPetsView.showPetDetailsUi(requestedPet.getId());
        }
    }

    @Override
    public void takeView(PetsContract.View view) {
        this.mPetsView = view;
        loadPets(false);
    }

    @Override
    public void dropView() {
        mPetsView = null;
    }
}
