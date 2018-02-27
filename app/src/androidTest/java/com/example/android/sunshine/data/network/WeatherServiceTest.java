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

import static com.example.android.sunshine.util.LiveDataTestUtil.getValue;
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
    public void setup() {
        TestSunShineApp application = (TestSunShineApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getTestAppComponent().injectTest(this);

        mLatch = new CountDownLatch(1);

    }

    @After
    public void tearDown() {
        mLatch.countDown();
    }

    @Test
    public void getWeather() throws Exception {
        String locationQuery = "Mountain View, CA";
        ApiResponse<WeatherResponse> response = getValue(
                service.getWeather(locationQuery, WeatherService.format, WeatherService.units, WeatherService.NUM_DAYS));
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body.getCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body.getWeatherForecast()).isNotEmpty();

    }
}
