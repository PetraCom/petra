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

package com.hackjunction.petra.data.source;

import android.support.annotation.NonNull;

import com.hackjunction.petra.data.Pet;

import java.util.List;

/**
 * Main entry point for accessing pets data.
 * <p>
 * For simplicity, only getPets() and getPet() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new pet is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface PetsDataSource {

    interface LoadPetsCallback {

        void onPetsLoaded(List<Pet> pets);

        void onDataNotAvailable();
    }

    interface GetPetCallback {

        void onPetLoaded(Pet pet);

        void onDataNotAvailable();
    }

    void getPets(@NonNull LoadPetsCallback callback);

    void getPet(@NonNull String petId, @NonNull GetPetCallback callback);

    void savePet(@NonNull Pet pet);

    void refreshPets();

    void deleteAllPets();

    void deletePet(@NonNull String petId);
}
