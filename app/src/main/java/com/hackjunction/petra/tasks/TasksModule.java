package com.hackjunction.petra.tasks;

import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link TasksPresenter}.
 */
@Module
public abstract class TasksModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract TasksFragment tasksFragment();

    @ActivityScoped
    @Binds abstract TasksContract.Presenter taskPresenter(TasksPresenter presenter);
}
