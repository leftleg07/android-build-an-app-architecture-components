package com.example.android.sunshine.data.network;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * WeatherService class
 */

public interface WeatherService {

    /* The format we want our API to return */
    String format = "json";
    /* The units we want our API to return */
    String units = "metric";

    // The number of days we want our API to return, set to 14 days or two weeks
    int NUM_DAYS = 14;

    /* The query parameter allows us to provide a location string to the API */
    String QUERY_PARAM = "q";

    /* The format parameter allows us to designate whether we want JSON or XML from our API */
    String FORMAT_PARAM = "mode";
    /* The units parameter allows us to designate whether we want metric units or imperial units */
    String UNITS_PARAM = "units";
    /* The days parameter allows us to designate how many days of weather data we want */
    String DAYS_PARAM = "cnt";

    @GET("weather?mode=json&units=metric&cnt=14")
    Flowable<WeatherResponse> getWeather(@Query(QUERY_PARAM) String query, @Query(FORMAT_PARAM) String format, @Query(UNITS_PARAM) String unit, @Query(DAYS_PARAM) int days);
}
