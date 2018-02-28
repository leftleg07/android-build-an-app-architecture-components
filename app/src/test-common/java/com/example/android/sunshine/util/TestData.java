package com.example.android.sunshine.util;

import com.example.android.sunshine.data.database.WeatherEntry;

import java.util.Calendar;
import java.util.Date;

/**
 * TestData
 */

public class TestData {
//    TestDataWeatherEntry(int weatherIconId, Date date, double min, double max, double humidity, double pressure, double wind, double degrees)
    private static final Calendar calendar = Calendar.getInstance();
    private static final WeatherEntry ENTRY1 = new WeatherEntry(802,  getDate(), 13.07569571097914, 6.481880771841584, 50.0, 1036.5336281345433, 35.43301810422571, 268.0);
    private static final WeatherEntry ENTRY2 = new WeatherEntry(620,  getDate(), 13.07569571097914, 6.481880771841584, 50.0, 1036.5336281345433, 35.43301810422571, 268.0);
    private static final WeatherEntry ENTRY3 = new WeatherEntry(227,  getDate(), 13.07569571097914, 6.481880771841584, 50.0, 1036.5336281345433, 35.43301810422571, 268.0);
    private static final WeatherEntry ENTRY4 = new WeatherEntry(955,  getDate(), 13.07569571097914, 6.481880771841584, 50.0, 1036.5336281345433, 35.43301810422571, 268.0);
    public static final WeatherEntry[] WEATHER_ENTRIES = {ENTRY1, ENTRY2, ENTRY3, ENTRY4};

    private static Date getDate() {
        long millis = calendar.getTimeInMillis() + 1000;
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
