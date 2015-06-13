package com.daniel.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;
import com.activeandroid.content.ContentProvider;
import com.daniel.sunshine.data.WeatherContract;
import com.daniel.sunshine.persistence.Weather;
import com.daniel.sunshine.service.SunshineService_;
import org.androidannotations.annotations.*;

import java.util.Date;

@EFragment(R.layout.fragment_main)
@OptionsMenu(R.menu.main)
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {
  @ViewById(R.id.listview_forecast) ListView listView;
  @Bean ForecastAdapter forecastAdapter;
  @Bean Utility utility;

  // For the forecast view we're showing only a small subset of the stored data.
  // Specify the columns we need.
//  private static final String[] FORECAST_COLUMNS = {
//    // In this case the id needs to be fully qualified with a table name, since the content provider joins the location & weather tables in the background (both have an _id column)
//    // On the one hand, that's annoying. On the other, you can search the weather table using the location set by the user, which is only in the Location table.
//    // So the conveneinece is worth it.
//    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
//    WeatherContract.WeatherEntry.COLUMN_DATETEXT,
//    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
//    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
//    WeatherContract.LocationEntry.COLUMN_COORD_LAT,
//    WeatherContract.LocationEntry.COLUMN_COORD_LONG
//  };
//
//  // These indices are tied to FORECAST_COLUMNS. If the FORECAST_COLUMNS changes, these must change.
//  public static final int COL_WEATHER_ID = 0;
//  public static final int COL_WEATHER_DATE = 1;
//  public static final int COL_WEATHER_DESC = 2;
//  public static final int COL_WEATHER_MAX_TEMP = 3;
//  public static final int COL_WEATHER_MIN_TEMP = 4;
//  public static final int COL_LOCATION_SETTING = 5;
//  public static final int COL_WEATHER_CONDITION_ID = 6;
//  public static final int COL_COORD_LAT = 7;
//  public static final int COL_COORD_LONG = 8;

  @AfterViews
  void onViewCreated() {
    listView.setAdapter(forecastAdapter);
  }

  @OptionsItem(R.id.action_refresh)
  void onOptionsActionRefreshSelected() {
    updateWeather();
  }

  @OptionsItem(R.id.action_settings)
  void onOptionsActionSettingsSelected() {
    startActivity(new Intent(getActivity(), SettingsActivity_.class));
  }

  @ItemClick(R.id.listview_forecast)
  void onListViewClick(int position) {
    // TODO: replace with query()
//    Cursor cursor = forecastAdapter.getCursor();
//    if (cursor != null && cursor.moveToPosition(position)) {
//
//      DetailActivity_.intent(this)
//        .forecast_date(cursor.getString(COL_WEATHER_DATE))
//        .start();
//    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(0, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  public void updateWeather() {
    SunshineService_.intent(getActivity().getApplication())
      .requestWeatherInformation(utility.getPreferredLocation())
      .start();
  }

  @Override
  public void onStart() {
    super.onStart();
    updateWeather();
  }

  ////////////////////////////////////////////
  // CURSOR_LOADER CALLBACKS
  ////////////////////////////////////////////
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String location = utility.getPreferredLocation();
    String startDate = WeatherContract.getDbDateString(new Date());

    Uri uri = ContentProvider.createUri(Weather.class, null);

//    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, startDate);

    // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
    return new CursorLoader(
      getActivity(),
      uri,
      null,
//      FORECAST_COLUMNS,
      null,
      null,
      WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC"
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    forecastAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    forecastAdapter.swapCursor(null);
  }
}






