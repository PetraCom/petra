package com.hackjunction.petra.petdetail;

import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.hackjunction.petra.petdetail.PetDetailActivity.EXTRA_PET_ID;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link PetDetailPresenter}.
 */
@Module
public abstract class PetDetailModule {


    @FragmentScoped
    @ContributesAndroidInjector
    abstract PetDetailFragment petDetailFragment();

    @ActivityScoped
    @Binds
    abstract PetDetailContract.Presenter petDetailPresenter(PetDetailPresenter presenter);

    @Provides
    @ActivityScoped
    static String providePetId(PetDetailActivity activity) {
        return activity.getIntent().getStringExtra(EXTRA_PET_ID);
    }
}
