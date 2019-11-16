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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Immutable model class for a Pet.
 */
@Entity(tableName = "pets")
public final class Pet {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "name")
    private final String mName;

    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    /**
     * Use this constructor to create a new active Pet.
     *
     * @param name       name of the pet
     * @param description description of the pet
     */
    @Ignore
    public Pet(@Nullable String name, @Nullable String description) {
        this(name, description, UUID.randomUUID().toString());
    }

    /**
     * Use this constructor to create an active Pet if the Pet already has an id (copy of another
     * Pet).
     *
     * @param name       name of the pet
     * @param description description of the pet
     * @param id          id of the pet
     */
    @Ignore
    public Pet(@Nullable String name, @Nullable String description, @NonNull String id) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mName)) {
            return mName;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) &&
               Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equal(mId, pet.mId) &&
               Objects.equal(mName, pet.mName) &&
               Objects.equal(mDescription, pet.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mName, mDescription);
    }

    @Override
    public String toString() {
        return "Pet with name " + mName;
    }
}
