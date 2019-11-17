package com.hackjunction.petra.di;

import com.hackjunction.petra.addeditpet.AddEditPetActivity;
import com.hackjunction.petra.addeditpet.AddEditPetModule;
import com.hackjunction.petra.addedittask.AddEditTaskModule;
import com.hackjunction.petra.addedittask.AddEditTaskActivity;
import com.hackjunction.petra.devices.DeviceModule;
import com.hackjunction.petra.devices.DevicesActivity;
import com.hackjunction.petra.petdetail.PetDetailActivity;
import com.hackjunction.petra.petdetail.PetDetailModule;
import com.hackjunction.petra.pets.PetsActivity;
import com.hackjunction.petra.pets.PetsModule;
import com.hackjunction.petra.statistics.StatisticsActivity;
import com.hackjunction.petra.statistics.StatisticsModule;
import com.hackjunction.petra.taskdetail.TaskDetailActivity;
import com.hackjunction.petra.taskdetail.TaskDetailPresenterModule;
import com.hackjunction.petra.tasks.TasksActivity;
import com.hackjunction.petra.tasks.TasksModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module ActivityBindingModule is on,
 * in our case that will be AppComponent. The beautiful part about this setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified modules and be aware of a scope annotation @ActivityScoped
 * When Dagger.Android annotation processor runs it will create 4 subcomponents for us.
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = TasksModule.class)
    abstract TasksActivity tasksActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AddEditTaskModule.class)
    abstract AddEditTaskActivity addEditTaskActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = StatisticsModule.class)
    abstract StatisticsActivity statisticsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = TaskDetailPresenterModule.class)
    abstract TaskDetailActivity taskDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = PetsModule.class)
    abstract PetsActivity petsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = DeviceModule.class)
    abstract DevicesActivity devicesActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AddEditPetModule.class)
    abstract AddEditPetActivity addEditPetActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = PetDetailModule.class)
    abstract PetDetailActivity petDetailActivity();
}
