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

import com.hackjunction.petra.BasePresenter;
import com.hackjunction.petra.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface PetDetailContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMissingPet();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showEditPet(String petId);

        void showPetDeleted();

        void startShowDrawRegion();

        void stopShowDrawRegion();

        void showPetAlertDialog();

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void editPet();

        void deletePet();

        void startDrawRegion();

        void stopDrawRegion();

        void takeView(PetDetailContract.View petDetailFragment);

        void dropView();
    }
}
