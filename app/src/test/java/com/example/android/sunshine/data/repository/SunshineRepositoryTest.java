/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.data.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;

import com.example.android.sunshine.AppExecutors;
import com.example.android.sunshine.data.database.WeatherDao;
import com.example.android.sunshine.data.network.WeatherNetworkDataSource;
import com.example.android.sunshine.data.network.WeatherService;
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

@RunWith(ParameterizedRobolectricTestRunner.class)
public class SunshineRepositoryTest {

    private SunshineRepository repository;
    private WeatherNetworkDataSource weatherNetworkDataSource;
    private CountingAppExecutors countingAppExecutors;
    private final boolean useRealExecutors;

    @Mock
    WeatherDao weatherDao;

    @Mock
    Context context;


    @Mock
    WeatherService weatherService;

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

    public SunshineRepositoryTest(boolean useRealExecutors) {
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
        repository = new SunshineRepository(weatherDao, weatherNetworkDataSource, appExecutors);
    }

    @Test
    @Config(manifest = Config.NONE)
    public void getCurrentWeatherForecasts() {
        repository.getCurrentWeatherForecasts();
    }

//    @Test
//    @Config(manifest = Config.NONE)
//    public void getWeatherByDate() {
////        Date date = TestData.WEATHER_ENTRIES[0].getDate();
////        repository.getWeatherByDate(date);
//    }
}