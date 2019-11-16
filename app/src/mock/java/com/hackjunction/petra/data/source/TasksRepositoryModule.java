package com.hackjunction.petra.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.hackjunction.petra.data.FakeTasksRemoteDataSource;
import com.hackjunction.petra.data.source.local.TasksDao;
import com.hackjunction.petra.data.source.local.TasksLocalDataSource;
import com.hackjunction.petra.data.source.local.ToDoDatabase;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link TasksRepository}.
 */
@Module
abstract public class TasksRepositoryModule {

    @Singleton
    @Binds
    @Local
    abstract TasksDataSource provideTasksLocalDataSource(TasksLocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract TasksDataSource provideTasksRemoteDataSource(FakeTasksRemoteDataSource dataSource);

    @Singleton
    @Provides
    static ToDoDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), ToDoDatabase.class, "Tasks.db")
                .build();
    }

    @Singleton
    @Provides
    static TasksDao provideTasksDao(ToDoDatabase db) {
        return db.taskDao();
    }
}
