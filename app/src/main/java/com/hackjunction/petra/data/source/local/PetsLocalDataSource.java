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

package com.hackjunction.petra.data.source.local;

import android.support.annotation.NonNull;

import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.data.source.PetsDataSource;
import com.hackjunction.petra.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class PetsLocalDataSource implements PetsDataSource {

    private final AppExecutors mAppExecutors;
    private final Map<String, Pet> mPets = new ConcurrentHashMap<>();

    @Inject
    public PetsLocalDataSource(@NonNull AppExecutors executors) {
        mAppExecutors = executors;
    }

    /**
     * Note: {@link LoadPetsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getPets(@NonNull final LoadPetsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Pet> pets = new ArrayList<>(mPets.values());
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (pets.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onPetsLoaded(pets);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetPetCallback#onDataNotAvailable()} is fired if the {@link Pet} isn't
     * found.
     */
    @Override
    public void getPet(@NonNull final String petId, @NonNull final GetPetCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Pet pet = mPets.get(petId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (pet != null) {
                            callback.onPetLoaded(pet);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void savePet(@NonNull final Pet pet) {
        checkNotNull(pet);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mPets.put(pet.getId(), pet);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshPets() {
        // Not required because the {@link PetsRepository} handles the logic of refreshing the
        // pets from all the available data sources.
    }

    @Override
    public void deleteAllPets() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mPets.clear();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deletePet(@NonNull final String petId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mPets.remove(petId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
