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
package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.sunshine.R;
import com.example.android.sunshine.di.Injectable;
import com.example.android.sunshine.ui.detail.DetailActivity;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Displays a list of the next 14 days of forecasts
 */
public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnItemClickHandler, Injectable {

    private ForecastAdapter mForecastAdapter;

    private int mPosition = RecyclerView.NO_POSITION;
    private MainActivityViewModel mViewModel;

    @BindView(R.id.recyclerview_forecast)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    MainViewModelFactory mFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * The third parameter (shouldReverseLayout) should be true if you want to reverse your
         * layout. Generally, this is only true with horizontal lists that need to support a
         * right-to-left layout.
         */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         *
         * Although passing in "this" twice may seem strange, it is actually a sign of separation
         * of concerns, which is best programming practice. The ForecastAdapter requires an
         * Android Context (which all Activities are) as well as an onClickHandler. Since our
         * MainActivity implements the ForecastAdapter ForecastOnClickHandler interface, "this"
         * is also an instance of that type of handler.
         */
        mForecastAdapter = new ForecastAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);
        mViewModel = ViewModelProviders.of(this, mFactory).get(MainActivityViewModel.class);

        mViewModel.getForecast().observe(this, weatherEntries -> {
            mForecastAdapter.swapForecast(weatherEntries);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);

            // Show the weather list or the loading screen based on whether the forecast data exists
            // and is loaded
            if (weatherEntries != null && weatherEntries.size() != 0) showWeatherDataView();
            else showLoading();
        });
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param view
     * @param date Date of forecast
     */
    @Override
    public void onItemClick(View view, Date date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        long timestamp = date.getTime();
        weatherDetailIntent.putExtra(DetailActivity.EXTRA_WEATHER_ID, timestamp);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,

                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<View, String>(view.findViewById(R.id.weather_icon),
                        DetailActivity.VIEW_NAME_HEADER_IMAGE),
                new Pair<View, String>(view.findViewById(R.id.date),
                        DetailActivity.VIEW_NAME_HEADER_TITLE),
                new Pair<View, String>(view.findViewById(R.id.weather_description),
                        DetailActivity.VIEW_NAME_WEATHER_DESCRIPTION),
                new Pair<View, String>(view.findViewById(R.id.high_temperature),
                        DetailActivity.VIEW_NAME_HIGH_TEMPERATURE),
                new Pair<View, String>(view.findViewById(R.id.low_temperature),
                        DetailActivity.VIEW_NAME_LOW_TEMPERATURE));
        ActivityCompat.startActivity(this, weatherDetailIntent, activityOptions.toBundle());
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        // First, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Finally, make sure the weather data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        // Then, hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
