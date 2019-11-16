package com.hackjunction.petra.addeditpet;

import android.support.annotation.Nullable;

import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to auto create the AdEditTaskSubComponent and bind
 * the {@link AddEditPetPresenter} to the graph
 */
@Module
public abstract class AddEditPetModule {

    // Rather than having the activity deal with getting the intent extra and passing it to the presenter
    // we will provide the petId directly into the AddEditPetActivitySubcomponent
    // which is what gets generated for us by Dagger.Android.
    // We can then inject our PetId and state into our Presenter without having pass through dependency from
    // the Activity. Each UI object gets the dependency it needs and nothing else.
    @Provides
    @ActivityScoped
    @Nullable
    static String providePetId(AddEditPetActivity activity) {
        return activity.getIntent().getStringExtra(AddEditPetFragment.ARGUMENT_EDIT_PET_ID);
    }

    @Provides
    @ActivityScoped
    static boolean provideStatusDataMissing(AddEditPetActivity activity) {
        return activity.isDataMissing();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract AddEditPetFragment addEditPetFragment();

    @ActivityScoped
    @Binds
    abstract AddEditPetContract.Presenter petPresenter(AddEditPetPresenter presenter);

    //NOTE:  IF you want to have something be only in the Fragment scope but not activity mark a
    //@provides or @Binds method as @FragmentScoped.  Use case is when there are multiple fragments
    //in an activity but you do not want them to share all the same objects.
}
