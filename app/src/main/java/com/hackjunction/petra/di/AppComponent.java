package com.hackjunction.petra.di;

import android.app.Application;

import com.hackjunction.petra.PetraApplication;
import com.hackjunction.petra.data.source.PetsRepository;
import com.hackjunction.petra.data.source.PetsRepositoryModule;
import com.hackjunction.petra.data.source.RepositoryModule;
import com.hackjunction.petra.data.source.TasksRepository;
import com.hackjunction.petra.data.source.TasksRepositoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * This is a Dagger component. Refer to {@link PetraApplication} for the list of Dagger components
 * used in this application.
 * <p>
 * Even though Dagger allows annotating a {@link Component} as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in {@link
 * PetraApplication}.
 * //{@link AndroidSupportInjectionModule}
 * // is the module from Dagger.Android that helps with the generation
 * // and location of subcomponents.
 */
@Singleton
@Component(modules = {
        RepositoryModule.class,
        TasksRepositoryModule.class,
        PetsRepositoryModule.class,
        ApplicationModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})

public interface AppComponent extends AndroidInjector<DaggerApplication> {

    void inject(PetraApplication application);

    TasksRepository getTasksRepository();

    PetsRepository getPetsRepository();

    @Override
    void inject(DaggerApplication instance);

    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
