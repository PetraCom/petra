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

package com.hackjunction.petra.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.hackjunction.petra.data.source.PetsDataSource;

import javax.inject.Inject;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakePetsRemoteDataSource implements PetsDataSource {
    
    @Inject
    public FakePetsRemoteDataSource() {}

    @Override
    public void getPets(@NonNull LoadPetsCallback callback) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void getPet(@NonNull String petId, @NonNull GetPetCallback callback) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void savePet(@NonNull Pet pet) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void refreshPets() {
        // Not required because the {@link PetsRepository} handles the logic of refreshing the
        // pets from all the available data sources.
    }

    @Override
    public void deletePet(@NonNull String petId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAllPets() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @VisibleForTesting
    public void addPets(Pet... pets) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
