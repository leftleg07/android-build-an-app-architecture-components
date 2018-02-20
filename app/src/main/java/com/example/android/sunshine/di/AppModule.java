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

package com.example.android.sunshine.di;


import android.app.Application;
import android.content.Context;

import com.example.android.sunshine.AppExecutors;
import com.example.android.sunshine.data.SunshineRepository;
import com.example.android.sunshine.data.database.SunshineDatabase;
import com.example.android.sunshine.data.database.WeatherEntry;
import com.example.android.sunshine.data.network.WeatherNetworkDataSource;
import com.example.android.sunshine.data.network.WeatherResponse;
import com.example.android.sunshine.data.network.WeatherService;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {

    // Weather information. Each day's forecast info is an element of the "list" array
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    // All temperatures are children of the "temp" object
    private static final String OWM_TEMPERATURE = "temp";

    // Max temperature for the day
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";

    @Provides
    public Context provideApplication(Application application) {
        return application;
    }

    @Singleton
    @Provides
    WeatherService provideWeatherService() {
        return new Retrofit.Builder()
                .baseUrl("https://andfun-weather.udacity.com/")
                .addConverterFactory(createGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WeatherService.class);
    }

    private static Converter.Factory createGsonConverter() {
        JsonDeserializer<WeatherResponse> deserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            WeatherResponse response = new WeatherResponse();
            response.mCode = jsonObject.get(OWM_MESSAGE_CODE).getAsInt();
            response.mWeatherForecast = fromJson(jsonObject);
            return response;
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(WeatherResponse.class, deserializer).create();
        return GsonConverterFactory.create(gson);
    }

    private static WeatherEntry[] fromJson(final JsonObject forecastJson) {
        JsonArray jsonWeatherArray = forecastJson.getAsJsonArray(OWM_LIST);

        WeatherEntry[] weatherEntries = new WeatherEntry[jsonWeatherArray.size()];

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
        long normalizedUtcStartDay = SunshineDateUtils.getNormalizedUtcMsForToday();

        for (int i = 0; i < jsonWeatherArray.size(); i++) {
            // Get the JSON object representing the day
            JsonObject dayForecast = jsonWeatherArray.get(i).getAsJsonObject();

            // Create the weather entry object
            long dateTimeMillis = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            WeatherEntry weather = fromJson(dayForecast, dateTimeMillis);

            weatherEntries[i] = weather;
        }
        return weatherEntries;
    }

    private static WeatherEntry fromJson(final JsonObject dayForecast,
                                         long dateTimeMillis) {
        // We ignore all the datetime values embedded in the JSON and assume that
        // the values are returned in-order by day (which is not guaranteed to be correct).

        double pressure = dayForecast.get(OWM_PRESSURE).getAsDouble();
        int humidity = dayForecast.get(OWM_HUMIDITY).getAsInt();
        double windSpeed = dayForecast.get(OWM_WINDSPEED).getAsDouble();
        double windDirection = dayForecast.get(OWM_WIND_DIRECTION).getAsDouble();


        // Description is in a child array called "weather", which is 1 element long.
        // That element also contains a weather code.
        JsonObject weatherObject =
                dayForecast.getAsJsonArray(OWM_WEATHER).get(0).getAsJsonObject();

        int weatherId = weatherObject.get(OWM_WEATHER_ID).getAsInt();


        //  Temperatures are sent by Open Weather Map in a child object called "temp".
        JsonObject temperatureObject = dayForecast.getAsJsonObject(OWM_TEMPERATURE);
        double max = temperatureObject.get(OWM_MAX).getAsDouble();
        double min = temperatureObject.get(OWM_MIN).getAsDouble();

        // Create the weather entry object
        return new WeatherEntry(weatherId, new Date(dateTimeMillis), max, min,
                humidity, pressure, windSpeed, windDirection);
    }

    @Singleton
    @Provides
    SunshineRepository provideRepository(Context context, WeatherNetworkDataSource networkDataSource, AppExecutors executors) {
        SunshineDatabase database = SunshineDatabase.getInstance(context.getApplicationContext());
        return SunshineRepository.getInstance(database.weatherDao(), networkDataSource, executors);
    }

    @Singleton
    @Provides
    WeatherNetworkDataSource provideNetworkDataSource(Context context, AppExecutors executors, WeatherService service) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        return new WeatherNetworkDataSource(context.getApplicationContext(), executors, service);
    }

    @Singleton
    @Provides
    AppExecutors provideAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(3),
                new AppExecutors.MainThreadExecutor());
    }

}
