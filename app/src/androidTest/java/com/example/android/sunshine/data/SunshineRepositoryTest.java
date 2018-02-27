package com.example.android.sunshine.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.sunshine.TestSunShineApp;
import com.example.android.sunshine.data.repository.SunshineRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

/**
 * SunshineRepositoryTest class
 */
@RunWith(AndroidJUnit4.class)
public class SunshineRepositoryTest {
    private CountDownLatch mLatch;

    @Inject
    SunshineRepository repository;

    @Before
    public void setup() throws InterruptedException {
        TestSunShineApp application = (TestSunShineApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getTestAppComponent().injectTest(this);

        mLatch = new CountDownLatch(1);

    }

    @After
    public void tearDown() throws Exception {
        mLatch.countDown();
    }
}
