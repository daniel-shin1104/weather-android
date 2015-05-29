package com.daniel.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.daniel.sunshine.data.WeatherContract;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by daniel on 5/29/15.
 */

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
  private final String LOG_TAG = "sunshine:" + FetchWeatherTask.class.getSimpleName();


  private final Context context;

  public FetchWeatherTask(Context context) {
    this.context = context;
  }

  protected long addLocation(String locationSetting, String cityName, double latitude, double longitude) {
    Log.v(LOG_TAG, "inserting " + cityName + ", with coord: " + latitude + ", " + longitude);

    // First, check if the location with this city name exists in the db
    Cursor cursor = context.getContentResolver().query(
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

      Uri locationInsertUri = context.getContentResolver()
        .insert(WeatherContract.LocationEntry.CONTENT_URI, locationValues);

      return ContentUris.parseId(locationInsertUri);
    }
  }


  @Override
  protected String[] doInBackground(String... params) {
    if (params.length == 0) { return null; }

    String locationQuery = params[0];

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;

    String format = "json";
    String units = "metric";
    int numDays = 7;

    try {
      final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
      final String QUERY_PARAM = "q";
      final String FORMAT_PARAM = "mode";
      final String UNITS_PARAM = "units";
      final String DAYS_PARAM = "cnt";

      Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
        .appendQueryParameter(QUERY_PARAM, locationQuery)
        .appendQueryParameter(FORMAT_PARAM, format)
        .appendQueryParameter(UNITS_PARAM, units)
        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
        .build();

      URL url = new URL(builtUri.toString());

      Log.d(LOG_TAG, "Built URI " + builtUri.toString());

      // Create the request to OpenWeatherMap, and open the connection
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // Read the input stream into a String
      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        // Nothing to do.
        return null;
      }

      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // But it does make debugging a *lot* easier if you print out the completed
        // buffer for debugging.
        buffer.append(line + "\n");
      }

      if (buffer.length() == 0) {
        // Stream was empty.  No point in parsing.
        return null;
      }
      forecastJsonStr = buffer.toString();

      Log.d(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Error ", e);

      // If the code didn't successfully get the weather data, there's no point in attemping
      // to parse it.
      return null;

    } finally{
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException e) {
          Log.e(LOG_TAG, "Error closing stream", e);
        }
      }
    }

    try {
      return new JSONParser(context, this).getWeatherDataFromJson(forecastJsonStr, numDays, locationQuery);
    } catch (JSONException e) {
      Log.e(LOG_TAG, e.getMessage(), e);
      e.printStackTrace();
    }

    return null;
  }
}