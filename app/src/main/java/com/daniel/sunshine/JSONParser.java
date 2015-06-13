package com.daniel.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import com.daniel.sunshine.data.WeatherContract;
import com.daniel.sunshine.etc.Pref_;
import com.daniel.sunshine.service.SunshineService;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@EBean
public class JSONParser {
  @RootContext SunshineService sunshineService;
  @Pref Pref_ pref;

  private final String LOG_TAG = "sunshine:" + JSONParser.class.getSimpleName();

  private boolean DEBUG = true;

  /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
  private String getReadableDateString(long time){
    // Because the API returns a unix timestamp (measured in seconds),
    // it must be converted to milliseconds in order to be converted to valid date.
    Date date = new Date(time * 1000);
    SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
    return format.format(date);
  }

  /**
   * Prepare the weather high/lows for presentation.
   */
  private String formatHighLows(Context context, double high, double low) {
    String temperatureUnit = pref.temperatureUnit().get();

    if (temperatureUnit.equals(context.getString(R.string.pref_units_farenheit))) {
      high = (high * 1.8) + 32;
      low = (low * 1.8) + 32;
    }

    // For presentation, assume the user doesn't care about tenth of a degree
    long roundedHigh = Math.round(high);
    long roundedLow = Math.round(low);

    return roundedHigh + "/" + roundedLow;
  }

  /**
   * Take the String representing the complete forecast in JSON Format and
   * pull out the data we need to construct the Strings needed for the wireframes.
   *
   * Fortunately parsing is easy:  constructor takes the JSON string and converts it
   * into an Object hierarchy for us.
   */
  public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays, String locationSetting)
    throws JSONException {
    JSONObject forecastJson = new JSONObject(forecastJsonStr);

    /* Location Information */
    JSONObject cityJSON = forecastJson.getJSONObject("city");
    String cityName = cityJSON.getString("name");

    JSONObject coordJSON = cityJSON.getJSONObject("coord");
    double cityLatitude = coordJSON.getLong("lat");
    double cityLongitude = coordJSON.getLong("lon");

    Log.v(LOG_TAG, cityName + ", with coord: " + cityLatitude + " " + cityLongitude);

    // Insert the location into the database.
    long locationID = sunshineService.addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

    JSONArray weatherArray = forecastJson.getJSONArray("list");
    // Get and insert the new weather information into the database
    ArrayList<ContentValues> weatherValuesArray = new ArrayList<ContentValues>(weatherArray.length());

    String[] resultStrs = new String[numDays];
    for(int i = 0; i < weatherArray.length(); i++) {
      // Get the JSON object representing the day
      JSONObject dayForecast = weatherArray.getJSONObject(i);

      /* OpenWeatherMap Information */
      long dateTime = dayForecast.getLong("dt");
      double pressure = dayForecast.getDouble("pressure");
      int humidity = dayForecast.getInt("humidity");
      double windSpeed = dayForecast.getDouble("speed");
      double windDirection = dayForecast.getDouble("deg");

      // Description is in a child array called "weather", which is 1 element long.
      // Thta element also contains a weather code
      JSONObject weatherObject = dayForecast.getJSONArray("weather").getJSONObject(0);

      String description = weatherObject.getString("main");
      int weatherId = weatherObject.getInt("id");

      // Temperatures are in a child object called "temp".
      JSONObject temperatureObject = dayForecast.getJSONObject("temp");

      double high = temperatureObject.getDouble("max");
      double low = temperatureObject.getDouble("min");

      ContentValues weatherValues = new ContentValues();
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, WeatherContract.getDbDateString(new Date(dateTime * 1000L)));
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

      weatherValuesArray.add(weatherValues);

      String highAndLow = formatHighLows(sunshineService, high, low);
      String day = getReadableDateString(dateTime);
      resultStrs[i] = day + " - " + description + " - " + highAndLow;
    }

    if (weatherValuesArray.size() > 0) {
      int rowsInserted = sunshineService.getContentResolver()
        .bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValuesArray.toArray(new ContentValues[weatherValuesArray.size()]));

      Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");

      if (DEBUG) {
        Cursor weatherCursor = sunshineService.getContentResolver().query(
          WeatherContract.WeatherEntry.CONTENT_URI,
          null,
          null,
          null,
          null
        );

        if (weatherCursor.moveToFirst()) {
          ContentValues resultValues = new ContentValues();
          DatabaseUtils.cursorRowToContentValues(weatherCursor, resultValues);

          Log.v(LOG_TAG, "Query succeeded! *********");

          for (String key : resultValues.keySet()) {
            Log.v(LOG_TAG, key + ": " + resultValues.getAsString(key));
          }
        } else {
          Log.v(LOG_TAG, "Query Failed! :( ***********");
        }
      }
    }

    return resultStrs;
  }
}












