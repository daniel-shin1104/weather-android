package com.daniel.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.daniel.sunshine.data.WeatherContract;
import com.daniel.sunshine.service.SunshineService_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Date;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
@EFragment(R.layout.fragment_main)
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {
  @ViewById(R.id.listview_forecast) ListView listView;

  private final String LOG_TAG = "sunshine:" + ForecastFragment.class.getSimpleName();
  private ForecastAdapter forecastAdapter;

  private static final int FORECAST_LOADER = 0;
  private String location;

  // For the forecast view we're showing only a small subset of the stored data.
  // Specify the columns we need.
  private static final String[] FORECAST_COLUMNS = {
    // In this case the id needs to be fully qualified with a table name, since the content provider joins the location & weather tables in the background (both have an _id column)
    // On the one hand, that's annoying. On the other, you can search the weather table using the location set by the user, which is only in the Location table.
    // So the conveneinece is worth it.
    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
    WeatherContract.WeatherEntry.COLUMN_DATETEXT,
    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    WeatherContract.LocationEntry.COLUMN_COORD_LAT,
    WeatherContract.LocationEntry.COLUMN_COORD_LONG
  };

  // These indices are tied to FORECAST_COLUMNS. If the FORECAST_COLUMNS changes, these must change.
  public static final int COL_WEATHER_ID = 0;
  public static final int COL_WEATHER_DATE = 1;
  public static final int COL_WEATHER_DESC = 2;
  public static final int COL_WEATHER_MAX_TEMP = 3;
  public static final int COL_WEATHER_MIN_TEMP = 4;
  public static final int COL_LOCATION_SETTING = 5;
  public static final int COL_WEATHER_CONDITION_ID = 6;
  public static final int COL_COORD_LAT = 7;
  public static final int COL_COORD_LONG = 8;

  public ForecastFragment() {
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // This prevents creating multiple menus.
    if (menu.size() > 0) { return; }

    inflater.inflate(R.menu.main, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here.
    // The action bar will automatically handle clicks on the Home/Up button, as long as you specify a parent activity in AndroidManifest.xml.
    Log.d(LOG_TAG, "Tapped!");

    int id = item.getItemId();
    switch (id) {
      case R.id.action_refresh:
        updateWeather();
        return true;

      case R.id.action_settings:
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @AfterViews
  void onViewCreated() {
    forecastAdapter = new ForecastAdapter(getActivity(), null, 0);

    listView.setAdapter(forecastAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = forecastAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
          Intent intent = new Intent(getActivity(), DetailActivity.class)
            .putExtra(DetailActivityFragment.DATE_KEY, cursor.getString(COL_WEATHER_DATE));

          startActivity(intent);
        }
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  public void updateWeather() {
    SunshineService_.intent(getActivity().getApplication())

      .start();



//    Intent intent = new Intent(getActivity(), SunshineService.class);
//    intent.putExtra(
//      SunshineService.LOCATION_QUERY_EXTRA,
//      Utility.getPreferredLocation(getActivity())
//    );
//
//    getActivity().startService(intent);
  }

  @Override
  public void onStart() {
    super.onStart();
    updateWeather();
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // This is called when a new Loader needs to be created. This fragment only uses one loader, so we don't care about checking the id.

    // To only show current and future dates, get thet String representation for today
    // and filter the query to return whether only for dates after or including today
    // Only return date after today
    String startDate = WeatherContract.getDbDateString(new Date());

    // Sort order: Ascending, by date.
    String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

    location = Utility.getPreferredLocation(getActivity());
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, startDate);

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
    forecastAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    forecastAdapter.swapCursor(null);
  }
}






