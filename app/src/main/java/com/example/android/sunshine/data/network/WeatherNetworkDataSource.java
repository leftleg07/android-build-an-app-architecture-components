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
package com.example.android.sunshine.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.sunshine.AppExecutors;
import com.example.android.sunshine.data.database.WeatherEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Provides an API for doing all operations with the server data
 */
public class WeatherNetworkDataSource {
    // The number of days we want our API to return, set to 14 days or two weeks
    public static final int NUM_DAYS = 14;
    private static final String LOG_TAG = WeatherNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.SECONDS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final MutableLiveData<WeatherEntry[]> mDownloadedWeatherForecasts;
    private final AppExecutors mExecutors;
    private final WeatherService mService;
    private final MediatorLiveData<ApiResponse<WeatherResponse>> result;

    public WeatherNetworkDataSource(Context context, AppExecutors executors, WeatherService service) {
        mContext = context;
        mExecutors = executors;
        mService = service;
        mDownloadedWeatherForecasts = new MutableLiveData<>();
        result = new MediatorLiveData<>();
        result.observeForever(weatherResponseApiResponse -> {});
    }


    public LiveData<WeatherEntry[]> getCurrentWeatherForecasts() {
        return mDownloadedWeatherForecasts;
    }

    /**
     * Starts an intent service to fetch the weather.
     */
    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(mContext, SunshineSyncIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }

    /**
     * Schedules a repeating job service which fetches the weather.
     */
    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Sunshine
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(SunshineFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(SUNSHINE_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncSunshineJob);
        Log.d(LOG_TAG, "Job scheduled");
    }

    /**
     * Gets the newest weather
     */
    void fetchWeather() {
        Log.d(LOG_TAG, "Fetch weather started");
        mExecutors.networkIO().execute(() -> {
            try {
                String locationQuery = "Mountain View, CA";
                LiveData<ApiResponse<WeatherResponse>> apiResponse = mService.getWeather(locationQuery, WeatherService.format, WeatherService.units, WeatherService.NUM_DAYS);

                result.addSource(apiResponse, weatherResponseApiResponse -> {
                    result.removeSource(apiResponse);
                    WeatherResponse response = weatherResponseApiResponse.body;
                    // As long as there are weather forecasts, update the LiveData storing the most recent
                    // weather forecasts. This will trigger observers of that LiveData, such as the
                    // SunshineRepository.
                    if (response != null && response.getWeatherForecast().length != 0) {
                        Log.d(LOG_TAG, "JSON not null and has " + response.getWeatherForecast().length
                                + " values");
                        Log.d(LOG_TAG, String.format("First value is %1.0f and %1.0f",
                                response.getWeatherForecast()[0].getMin(),
                                response.getWeatherForecast()[0].getMax()));

                        // When you are off of the main thread and want to update LiveData, use postValue.
                        // It posts the update to the main thread.
                        mDownloadedWeatherForecasts.postValue(response.getWeatherForecast());

                        // If the code reaches this point, we have successfully performed our sync
                    }
                });
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }

}