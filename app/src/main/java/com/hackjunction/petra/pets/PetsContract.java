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

import android.support.annotation.NonNull;

import com.hackjunction.petra.BasePresenter;
import com.hackjunction.petra.BaseView;
import com.hackjunction.petra.data.Pet;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface PetsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showPets(List<Pet> pets);

        void showAddPet();

        void showPetDetailsUi(String petId);

        void showLoadingPetsError();

        void showNoPets();

        void showSuccessfullySavedMessage();

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void result(int requestCode, int resultCode);

        void loadPets(boolean forceUpdate);

        void addNewPet();

        void openPetDetails(@NonNull Pet requestedPet);

        void takeView(PetsContract.View view);

        void dropView();
    }
}
