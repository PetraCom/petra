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
import android.support.annotation.Nullable;

import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.di.AppComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load pets from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * <p />
 * By marking the constructor with {@code @Inject} and the class with {@code @Singleton}, Dagger
 * injects the dependencies required to create an instance of the PetsRespository (if it fails, it
 * emits a compiler error). It uses {@link PetsRepositoryModule} to do so, and the constructed
 * instance is available in {@link AppComponent}.
 * <p />
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
public class PetsRepository implements PetsDataSource {

    private final PetsDataSource mPetsRemoteDataSource;

    private final PetsDataSource mPetsLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Pet> mCachedPets;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the PetsRepository. Because {@link PetsDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link PetsRepositoryModule}.
     * <P>
     * When two arguments or more have the same type, we must provide to Dagger a way to
     * differentiate them. This is done using a qualifier.
     * <p>
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    PetsRepository(@Remote PetsDataSource petsRemoteDataSource,
                   @Local PetsDataSource petsLocalDataSource) {
        mPetsRemoteDataSource = petsRemoteDataSource;
        mPetsLocalDataSource = petsLocalDataSource;
    }

    /**
     * Gets pets from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadPetsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getPets(@NonNull final LoadPetsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedPets != null && !mCacheIsDirty) {
            callback.onPetsLoaded(new ArrayList<>(mCachedPets.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            //getPetsFromRemoteDataSource(callback);
            List<Pet> pets = new ArrayList<>();
            refreshCache(pets);
            refreshLocalDataSource(pets);
            callback.onPetsLoaded(new ArrayList<>(mCachedPets.values()));
        } else {
            // Query the local storage if available. If not, query the network.
            mPetsLocalDataSource.getPets(new LoadPetsCallback() {
                @Override
                public void onPetsLoaded(List<Pet> pets) {
                    refreshCache(pets);
                    callback.onPetsLoaded(new ArrayList<>(mCachedPets.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    //getPetsFromRemoteDataSource(callback);
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void savePet(@NonNull Pet pet) {
        checkNotNull(pet);
        //mPetsRemoteDataSource.savePet(pet);
        mPetsLocalDataSource.savePet(pet);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedPets == null) {
            mCachedPets = new LinkedHashMap<>();
        }
        mCachedPets.put(pet.getId(), pet);
    }

    /**
     * Gets pets from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetPetCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getPet(@NonNull final String petId, @NonNull final GetPetCallback callback) {
        checkNotNull(petId);
        checkNotNull(callback);

        Pet cachedPet = getPetWithId(petId);

        // Respond immediately with cache if available
        if (cachedPet != null) {
            callback.onPetLoaded(cachedPet);
            return;
        }

        // Load from server/persisted if needed.

        // Is the pet in the local data source? If not, query the network.
        mPetsLocalDataSource.getPet(petId, new GetPetCallback() {
            @Override
            public void onPetLoaded(Pet pet) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedPets == null) {
                    mCachedPets = new LinkedHashMap<>();
                }
                mCachedPets.put(pet.getId(), pet);
                callback.onPetLoaded(pet);
            }

            @Override
            public void onDataNotAvailable() {
//                mPetsRemoteDataSource.getPet(petId, new GetPetCallback() {
//                    @Override
//                    public void onPetLoaded(Pet pet) {
//                        // Do in memory cache update to keep the app UI up to date
//                        if (mCachedPets == null) {
//                            mCachedPets = new LinkedHashMap<>();
//                        }
//                        mCachedPets.put(pet.getId(), pet);
//                        callback.onPetLoaded(pet);
//                    }
//
//                    @Override
//                    public void onDataNotAvailable() {
//                        callback.onDataNotAvailable();
//                    }
//                });
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void refreshPets() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllPets() {
        //mPetsRemoteDataSource.deleteAllPets();
        mPetsLocalDataSource.deleteAllPets();

        if (mCachedPets == null) {
            mCachedPets = new LinkedHashMap<>();
        }
        mCachedPets.clear();
    }

    @Override
    public void deletePet(@NonNull String petId) {
        //mPetsRemoteDataSource.deletePet(checkNotNull(petId));
        mPetsLocalDataSource.deletePet(checkNotNull(petId));

        mCachedPets.remove(petId);
    }

//    private void getPetsFromRemoteDataSource(@NonNull final LoadPetsCallback callback) {
//        mPetsRemoteDataSource.getPets(new LoadPetsCallback() {
//            @Override
//            public void onPetsLoaded(List<Pet> pets) {
//                refreshCache(pets);
//                refreshLocalDataSource(pets);
//                callback.onPetsLoaded(new ArrayList<>(mCachedPets.values()));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                callback.onDataNotAvailable();
//            }
//        });
//    }

    private void refreshCache(List<Pet> pets) {
        if (mCachedPets == null) {
            mCachedPets = new LinkedHashMap<>();
        }
        mCachedPets.clear();
        for (Pet pet : pets) {
            mCachedPets.put(pet.getId(), pet);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Pet> pets) {
        mPetsLocalDataSource.deleteAllPets();
        for (Pet pet : pets) {
            mPetsLocalDataSource.savePet(pet);
        }
    }

    @Nullable
    private Pet getPetWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedPets == null || mCachedPets.isEmpty()) {
            return null;
        } else {
            return mCachedPets.get(id);
        }
    }
}
