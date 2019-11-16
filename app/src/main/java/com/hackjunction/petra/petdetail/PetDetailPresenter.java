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

package com.hackjunction.petra.petdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.data.source.PetsDataSource;
import com.hackjunction.petra.data.source.PetsRepository;

import javax.inject.Inject;

/**
 * Listens to user actions from the UI ({@link PetDetailFragment}), retrieves the data and updates
 * the UI as required.
 * <p>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the PetDetailPresenter (if it fails, it emits a compiler error). It uses
 * {@link PetDetailModule} to do so.
 * <p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
final class PetDetailPresenter implements PetDetailContract.Presenter {

    private PetsRepository mPetsRepository;
    @Nullable
    private PetDetailContract.View mPetDetailView;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Nullable
    private String mPetId;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    PetDetailPresenter(@Nullable String petId,
                       PetsRepository petsRepository) {
        mPetsRepository = petsRepository;
        mPetId = petId;
    }


    private void openPet() {
        if (Strings.isNullOrEmpty(mPetId)) {
            if (mPetDetailView != null) {
                mPetDetailView.showMissingPet();
            }
            return;
        }

        if (mPetDetailView != null) {
            mPetDetailView.setLoadingIndicator(true);
        }
        mPetsRepository.getPet(mPetId, new PetsDataSource.GetPetCallback() {
            @Override
            public void onPetLoaded(Pet pet) {
                // The view may not be able to handle UI updates anymore
                if (mPetDetailView==null||!mPetDetailView.isActive()) {
                    return;
                }
                mPetDetailView.setLoadingIndicator(false);
                if (null == pet) {
                    mPetDetailView.showMissingPet();
                } else {
                    showPet(pet);
                }
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mPetDetailView.isActive()) {
                    return;
                }
                mPetDetailView.showMissingPet();
            }
        });
    }

    @Override
    public void editPet() {
        if (Strings.isNullOrEmpty(mPetId)) {
            if (mPetDetailView != null) {
                mPetDetailView.showMissingPet();
            }
            return;
        }
        if (mPetDetailView != null) {
            mPetDetailView.showEditPet(mPetId);
        }
    }

    @Override
    public void deletePet() {
        if (Strings.isNullOrEmpty(mPetId)) {
            if (mPetDetailView != null) {
                mPetDetailView.showMissingPet();
            }
            return;
        }
        mPetsRepository.deletePet(mPetId);
        if (mPetDetailView != null) {
            mPetDetailView.showPetDeleted();
        }
    }

    @Override
    public void takeView(PetDetailContract.View petDetailView) {
        mPetDetailView = petDetailView;
        openPet();
    }

    @Override
    public void dropView() {
        mPetDetailView = null;
    }

    private void showPet(@NonNull Pet pet) {
        String title = pet.getTitle();
        String description = pet.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            if (mPetDetailView != null) {
                mPetDetailView.hideTitle();
            }
        } else {
            if (mPetDetailView != null) {
                mPetDetailView.showTitle(title);
            }
        }

        if (Strings.isNullOrEmpty(description)) {
            if (mPetDetailView != null) {
                mPetDetailView.hideDescription();
            }
        } else {
            if (mPetDetailView != null) {
                mPetDetailView.showDescription(description);
            }
        }
        if (mPetDetailView != null) {
            mPetDetailView.showCompletionStatus(pet.isCompleted());
        }
    }
}
