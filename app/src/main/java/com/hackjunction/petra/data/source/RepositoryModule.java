package com.hackjunction.petra.data.source;

import com.hackjunction.petra.util.AppExecutors;
import com.hackjunction.petra.util.DiskIOThreadExecutor;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link PetsRepository}.
 */
@Module
public class RepositoryModule {

    private static final int THREAD_COUNT = 3;

    @Singleton
    @Provides
    static AppExecutors provideAppExecutors() {
        return new AppExecutors(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new AppExecutors.MainThreadExecutor());
    }
}
