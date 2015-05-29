package com.daniel.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.daniel.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by daniel on 5/29/15.
 */

public class Utility {
  public static String getPreferredLocation(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    return prefs.getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
  }

  public static boolean isCelsius(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    return prefs.getString(
      context.getString(R.string.pref_units_key),
      context.getString(R.string.pref_units_celsius)
    ).equals(context.getString(R.string.pref_units_celsius));
  }

  static String formatTemperature(double temperature, boolean isCelsius) {
    double temp = isCelsius ? temperature : ((temperature * 9) / 5) + 32;

    return String.format("%.0f", temp);
  }

  static String formatDate(String dateString) {
    Date date = WeatherContract.getDateFromDb(dateString);
    return DateFormat.getDateInstance().format(date);
  }
}
