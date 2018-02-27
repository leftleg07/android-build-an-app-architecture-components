package com.example.android.sunshine.data.network;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;

import com.example.android.sunshine.AppExecutors;
import com.example.android.sunshine.util.CountingAppExecutors;
import com.example.android.sunshine.util.InstantAppExecutors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

/**
 * WeatherNetworkDataSourceTest class
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class WeatherNetworkDataSourceTest {

    private CountingAppExecutors countingAppExecutors;
    private final boolean useRealExecutors;

    @Mock
    WeatherService weatherService;
    @Mock
    Context context;


    WeatherNetworkDataSource weatherNetworkDataSource;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                        {true},
                        {false},
                }
        );
    }

    public WeatherNetworkDataSourceTest(boolean useRealExecutors) {
        this.useRealExecutors = useRealExecutors;
        if (useRealExecutors) {
            countingAppExecutors = new CountingAppExecutors();
        }
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppExecutors appExecutors = useRealExecutors
                ? countingAppExecutors.getAppExecutors()
                : new InstantAppExecutors();
        weatherNetworkDataSource = new WeatherNetworkDataSource(context, appExecutors, weatherService);

    }


    @Test
    @Config(manifest = Config.NONE)
    public void fetchWeather() throws InterruptedException {
//        LiveData<ApiResponse<WeatherResponse>> value;
//        when(weatherService.getWeather(anyString(), anyString(), anyString(), anyInt())).thenReturn(value);
//        weatherNetworkDataSource.getWeather();
//        while(true) {
//            Thread.sleep(1600);
//        }
    }
}
