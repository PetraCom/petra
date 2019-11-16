package com.hackjunction.petra.pets;

import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link PetsPresenter}.
 */
@Module
public abstract class PetsModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract PetsFragment petsFragment();

    @ActivityScoped
    @Binds abstract PetsContract.Presenter petsPresenter(PetsPresenter presenter);
}
