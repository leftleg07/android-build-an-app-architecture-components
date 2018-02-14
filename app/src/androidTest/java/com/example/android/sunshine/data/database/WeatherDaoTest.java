package com.example.android.sunshine.data.database;

import android.arch.lifecycle.LiveData;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.example.android.sunshine.LiveDataTestUtil.getValue;
import static com.example.android.sunshine.data.database.TestData.WEATHER_ENTRIES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * WeatherDaoTest class
 */
@RunWith(AndroidJUnit4.class)
public class WeatherDaoTest extends DbTest {

    @Test
    public void bulkInsertAndOperations() throws Exception {
        WeatherDao dao = db.weatherDao();
        dao.bulkInsert(WEATHER_ENTRIES);

        // getCurrentWeatherForecasts
        List<ListWeatherEntry> forecasts = getValue(dao.getCurrentWeatherForecasts(WEATHER_ENTRIES[0].getDate()));
        assertThat(forecasts.size(), is(WEATHER_ENTRIES.length));

        // countAllFutureWeather
        int count = dao.countAllFutureWeather(WEATHER_ENTRIES[0].getDate());
        assertThat(count, is(WEATHER_ENTRIES.length));

        // getWeatherByDate
        WeatherEntry weather = getValue(dao.getWeatherByDate(WEATHER_ENTRIES[0].getDate()));
        assertThat(weather.getWeatherIconId(), is(WEATHER_ENTRIES[0].getWeatherIconId()));

        // deleteOldWeather
        dao.deleteOldWeather(WEATHER_ENTRIES[3].getDate());
        count = dao.countAllFutureWeather(WEATHER_ENTRIES[3].getDate());
        assertThat(count, is(1));
    }

}
