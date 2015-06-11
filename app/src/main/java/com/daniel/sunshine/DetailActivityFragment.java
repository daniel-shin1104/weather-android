package com.daniel.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.daniel.sunshine.data.WeatherContract;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_detail)
@OptionsMenu(R.menu.menu_detail)
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  @Bean Utility utility;

  private static final String LOG_TAG = "sunshine:" + DetailActivityFragment.class.getSimpleName();
  private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

  public static final String DATE_KEY = "forecast_date";
  private static final String LOCATION_KEY = "location";

  private ShareActionProvider shareActionprovider;
  private String location;
  private String forecast;

  private static final int DETAIL_LOADER = 0;

  private static final String[] FORECAST_COLUMNS = {
    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
    WeatherContract.WeatherEntry.COLUMN_DATETEXT,
    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
    WeatherContract.WeatherEntry.COLUMN_PRESSURE,
    WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
    WeatherContract.WeatherEntry.COLUMN_DEGREES,
    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    // This works because the WeatherProvider returns locaiton data joined with weather data, even though they're stroed in two different tables
    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
  };

  @ViewById(R.id.detail_icon) ImageView iconView;
  @ViewById(R.id.detail_day_textview) TextView friendlyDateView;
  @ViewById(R.id.detail_date_textview) TextView dateView;
  @ViewById(R.id.detail_forecast_textview) TextView descriptionView;
  @ViewById(R.id.detail_high_textview) TextView highTempView;
  @ViewById(R.id.detail_low_textview) TextView lowTempView;
  @ViewById(R.id.detail_humidity_textview) TextView humidityView;
  @ViewById(R.id.detail_wind_textview) TextView windView;
  @ViewById(R.id.detail_pressure_textview) TextView pressureView;

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString(LOCATION_KEY, location);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (location != null && location.equals(utility.getPreferredLocation())) {
      getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    Log.v(LOG_TAG, "in onCreateOptionsMenu");

    // Inflate the menu; this adds items to the action bar if it is present
    inflater.inflate(R.menu.menu_detail, menu);

    // Retrieve the share menu item
    MenuItem menuItem = menu.findItem(R.id.action_share);

    // Get the provider and hold onto it to set/change the share intent.
    shareActionprovider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    // If onLoadFinished happens before this, we can go ahead and set the share intent now.
    if (forecast != null) {
      shareActionprovider.setShareIntent(createShareForecastIntent());
    }
  }

  private Intent createShareForecastIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + FORECAST_SHARE_HASHTAG);
    return shareIntent;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    if (savedInstanceState != null) {
      location = savedInstanceState.getString(LOCATION_KEY);
    }

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.v(LOG_TAG, "In onCreateLoader");

    String forecast_date = ((DetailActivity) getActivity()).forecast_date;

    // Sort order: Ascending, by date.
    String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

    location = utility.getPreferredLocation();
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, forecast_date);

    Log.v(LOG_TAG, weatherForLocationUri.toString());

    // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
    return new CursorLoader(
      getActivity(),
      weatherForLocationUri,
      FORECAST_COLUMNS,
      null,
      null,
      sortOrder
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    Log.v(LOG_TAG, "In onLoadFinished");
    if (!data.moveToFirst()) { return; }

    // Read weather condition ID from cursor
    int weatherId = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));

    // Use weather art image
    iconView.setImageResource(utility.getArtResourceForWeatherCondition(weatherId));

    // Read date from cursor and update views for day of week and date
    String date = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
    String friendlyDateText = utility.getDayName(date);
    String dateText = utility.getFormattedMonthDay(date);
    friendlyDateView.setText(friendlyDateText);
    dateView.setText(dateText);

    // Read description from cursor and update view
    String description = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
    descriptionView.setText(description);

    boolean isCelsius = utility.isCelsius();

    // Read high temperature from cursor and update view
    double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
    String highString = utility.formatTemperature(getActivity(), high, isCelsius);
    highTempView.setText(highString);

    // Read low temperature from cursor and update view
    double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
    String lowString = utility.formatTemperature(getActivity(), low, isCelsius);
    lowTempView.setText(lowString);

    // Read humidity from cursor and update view
    float humidity = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
    humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

    // Read wind speed and direction from cursor and update view
    float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
    float windDirStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
    windView.setText(utility.getFormattedWind(windSpeedStr, windDirStr));

    // Read pressure from cursor and update view
    float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
    pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

    // We still need this for the share intent
    forecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

    Log.v(LOG_TAG, "Forecast String: " + forecast);

    // If onCreateOptionsMenu has already happened, we need to update the share intent now.
    if (shareActionprovider != null) {
      shareActionprovider.setShareIntent(createShareForecastIntent());
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) { }
}







