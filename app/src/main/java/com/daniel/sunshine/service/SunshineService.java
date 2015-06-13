package com.daniel.sunshine.service;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.daniel.sunshine.JSONParser;
import com.daniel.sunshine.data.WeatherContract;
import com.daniel.sunshine.http.RestClient;
import com.daniel.sunshine.http.WeatherResponse;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniel on 6/8/15.
 */

@EIntentService
public class SunshineService extends AbstractIntentService {
  private final String LOG_TAG = SunshineService.class.getSimpleName();

  @Bean JSONParser jsonParser;

  public SunshineService() {
    super(SunshineService.class.getSimpleName());
  }

  @ServiceAction
  @Background
  void requestWeatherInformation(String locationQuery) {

    // TODO: replace this ugly callback with lambda.
    RestClient.get().getForecast(locationQuery, new Callback<WeatherResponse>() {
      @Override
      public void success(WeatherResponse weatherResponse, Response response) {
        weatherResponse.printAll();


      }

      @Override
      public void failure(RetrofitError error) {
        error.printStackTrace();
      }
    });
  }

  public long addLocation(String locationSetting, String cityName, double latitude, double longitude) {
    Log.v(LOG_TAG, "inserting " + cityName + ", with coord: " + latitude + ", " + longitude);

    // First, check if the location with this city name exists in the db
    Cursor cursor = this.getContentResolver().query(
      WeatherContract.LocationEntry.CONTENT_URI,
      new String[]{WeatherContract.LocationEntry._ID},
      WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
      new String[]{locationSetting},
      null
    );

    if (cursor.moveToFirst()) {
      Log.v(LOG_TAG, "Found it in the database");
      int locationIdIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
      return cursor.getLong(locationIdIndex);
    } else {
      Log.v(LOG_TAG, "Didn't find it in the database, inserting now!");

      ContentValues locationValues = new ContentValues();
      locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
      locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
      locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, latitude);
      locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, longitude);

      Uri locationInsertUri = this.getContentResolver()
        .insert(WeatherContract.LocationEntry.CONTENT_URI, locationValues);

      return ContentUris.parseId(locationInsertUri);
    }
  }
}
