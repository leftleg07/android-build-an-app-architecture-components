package com.example.android.sunshine.di;


import android.app.Application;

import com.example.android.sunshine.data.network.WeatherServiceTest;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * TestAppComponent class
 */
@Singleton
@Component(modules = {AppModule.class, TestAppModule.class})
public interface TestAppComponent {


    void injectTest(WeatherServiceTest weatherServiceTest);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder app(Application app);
        Builder testAppModule(TestAppModule module);
        TestAppComponent build();
    }
}
