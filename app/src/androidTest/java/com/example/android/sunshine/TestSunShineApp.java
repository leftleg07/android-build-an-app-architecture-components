package com.example.android.sunshine;

import com.example.android.sunshine.di.DaggerTestAppComponent;
import com.example.android.sunshine.di.TestAppComponent;
import com.example.android.sunshine.di.TestAppModule;

/**
 * TestSunShineApp
 */

public class TestSunShineApp extends SunShineApp {
    private TestAppComponent testAppComponent;

    public TestAppComponent getTestAppComponent() {
        return testAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        testAppComponent = DaggerTestAppComponent.builder().app(this).testAppModule(new TestAppModule()).build();
    }
}
