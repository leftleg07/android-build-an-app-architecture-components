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

import com.example.android.sunshine.data.database.WeatherEntry;
import com.google.gson.annotations.SerializedName;

/**
 * Weather response from the backend. Contains the weather forecasts.
 */
public class WeatherResponse {

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

    @SerializedName(OWM_MESSAGE_CODE)
    public int mCode;

    @SerializedName(OWM_LIST)
    public WeatherEntry[] mWeatherForecast;

    public WeatherEntry[] getWeatherForecast() {
        return mWeatherForecast;
    }
}