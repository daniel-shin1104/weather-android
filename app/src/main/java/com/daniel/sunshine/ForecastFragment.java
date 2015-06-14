package com.daniel.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;
import com.activeandroid.content.ContentProvider;
import com.daniel.sunshine.persistence.Weather;
import com.daniel.sunshine.service.SunshineService_;
import org.androidannotations.annotations.*;

@EFragment(R.layout.fragment_main)
@OptionsMenu(R.menu.main)
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {
      @ViewById(R.id.listview_forecast) ListView listView;
    @Bean ForecastAdapter forecastAdapter;
    @Bean Utility utility;

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
      Cursor cursor = forecastAdapter.getCursor();
      if (cursor != null && cursor.moveToPosition(position)) {
        DetailActivity_.intent(this)
          .weather_id(cursor.getInt(cursor.getColumnIndex(Weather.Columns.WEATHER_ID.columnName)))
          .date(cursor.getLong(cursor.getColumnIndex(Weather.Columns.DATE.columnName)))
          .description(cursor.getString(cursor.getColumnIndex(Weather.Columns.SHORT_DESCRIPTION.columnName)))
          .high(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MAX.columnName)))
          .low(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MIN.columnName)))
          .humidity(cursor.getInt(cursor.getColumnIndex(Weather.Columns.HUMIDITY.columnName)))
          .wind_speed(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.WIND_SPEED.columnName)))
          .wind_degrees(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.WIND_DEGREES.columnName)))
          .pressure(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.PRESSURE.columnName)))
          .start();
      }
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
    // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
    return new CursorLoader(
      getActivity(),
      ContentProvider.createUri(Weather.class, null),
      null,
      null,
      null,
      Weather.Columns.DATE.columnName + " ASC"
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






