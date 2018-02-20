package com.example.android.sunshine.data.network;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.sunshine.TestSunShineApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import static com.google.common.truth.Truth.assertThat;

/**
 * WeatherServiceTest class
 */
@RunWith(AndroidJUnit4.class)
public class WeatherServiceTest {
    private CountDownLatch mLatch;

    @Inject
    WeatherService service;

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

    @Test
    public void getWeather() throws Exception {
        String locationQuery = "Mountain View, CA";
        WeatherResponse response = service.getWeather(locationQuery, WeatherService.format, WeatherService.units, WeatherService.NUM_DAYS).blockingSingle();
        assertThat(response.mCode).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.mWeatherForecast).isNotEmpty();
    }
}
