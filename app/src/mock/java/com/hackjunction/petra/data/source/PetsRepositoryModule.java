package com.hackjunction.petra.data.source;

import com.hackjunction.petra.data.FakePetsRemoteDataSource;
import com.hackjunction.petra.data.source.local.PetsLocalDataSource;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the {@link PetsRepository}.
 */
@Module
abstract public class PetsRepositoryModule {

    @Singleton
    @Binds
    @Local
    abstract PetsDataSource providePetsLocalDataSource(PetsLocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract PetsDataSource providePetsRemoteDataSource(FakePetsRemoteDataSource dataSource);
}
