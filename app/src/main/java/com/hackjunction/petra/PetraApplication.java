package com.hackjunction.petra;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.hackjunction.petra.data.source.TasksRepository;
import com.hackjunction.petra.di.AppComponent;
import com.hackjunction.petra.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * We create a custom {@link Application} class that extends  {@link DaggerApplication}.
 * We then override applicationInjector() which tells Dagger how to make our @Singleton Component
 * We never have to call `component.inject(this)` as {@link DaggerApplication} will do that for us.
 */
public class PetraApplication extends DaggerApplication {
    @Inject
    TasksRepository tasksRepository;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

    /**
     * Our Espresso tests need to be able to get an instance of the {@link TasksRepository}
     * so that we can delete all tasks before running each test
     */
    @VisibleForTesting
    public TasksRepository getTasksRepository() {
        return tasksRepository;
    }
}
