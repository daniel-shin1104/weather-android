package com.daniel.sunshine;

import android.content.Context;
import com.daniel.sunshine.data.WeatherContract;
import com.daniel.sunshine.etc.Pref_;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EBean(scope = EBean.Scope.Singleton)
public class Utility {
  public static final String DATE_FORMAT = "yyyMMdd";

  @RootContext Context context;
  @Pref Pref_ pref;

  public String getPreferredLocation() {
    return pref.location().get();
  }

  public boolean isCelsius() {
    return pref.temperatureUnit().get().equals(context.getString(R.string.pref_units_celsius));
  }

  public String formatTemperature(Context context, double temperature, boolean isCelsius) {
    double temp = isCelsius ? temperature : ((temperature * 9) / 5) + 32;

    return context.getString(R.string.format_temperature, temp);
  }

  public String formatDate(String dateString) {
    Date date = WeatherContract.getDateFromDb(dateString);
    return DateFormat.getDateInstance().format(date);
  }

  // Format used for storing dates in the database. Also used for converting those strings back into date objects for comparison/processing.

  public String getFriendlyDayString(long date) {
    Date todayDate = new Date();
    String todayStr = WeatherContract.getDbDateString(todayDate);
    Date inputDate = new Date(date * 1000);

    // if the date we're building the String for is today's date, the format is "Today, June 24"
    if (todayStr.equals(dateStr)) {
      String today = context.getString(R.string.today);
      int formatId = R.string.format_full_friendly_date;
      return String.format(context.getString(
        formatId,
        today,
        getFormattedMonthDay(dateStr)
        ));
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(todayDate);
      cal.add(Calendar.DATE, 7);

      String weekFutureString = WeatherContract.getDbDateString(cal.getTime());

      if (dateStr.compareTo(weekFutureString) < 0) {
        // If the input date is less than a week in the future, just return the day name.
        return getDayName(dateStr);
      } else {
        // Otherwise use the form "Mon JUn 3"
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(inputDate);
      }
    }
  }

  public String getDayName(long date) {
    Date inputDate = new Date(date * 1000);
    Date todayDate = new Date();

    // TODO: START FROM HERE!!!!!!
    // If the date is today, return the localized version of "Today" instead of the actual day name.
    if (WeatherContract.getDbDateString(todayDate).equals(dateStr)) {
      return context.getString(R.string.today);
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(todayDate);
      cal.add(Calendar.DATE, 1);

      Date tomorrowDate = cal.getTime();

      // If the date is set for tomorrow, the format is "Tomorrow"
      if (WeatherContract.getDbDateString(tomorrowDate).equals(dateStr)) {
        return context.getString(R.string.tomorrow);

      // Otherwise the format is just the day of the week
      } else {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        return dayFormat.format(inputDate);
      }
    }
  }

  public String getFormattedMonthDay(long date) {
    Date inputDate = new Date(date * 1000);
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
    return monthDayFormat.format(inputDate);
  }

  public String getFormattedWind(double wind_speed, double wind_degrees) {
    int windFormat;
    if (isCelsius()) {
      windFormat = R.string.format_wind_kmh;
    } else {
      windFormat = R.string.format_wind_mph;
      wind_speed = .621371192237334f * wind_speed;
    }

    String direction = "Unknown";
    if (wind_degrees >= 337.5 || wind_degrees < 22.5) {
      direction = "N";
    } else if (wind_degrees >= 22.5 && wind_degrees < 67.5) {
      direction = "NE";
    } else if (wind_degrees >= 67.5 && wind_degrees < 112.5) {
      direction = "E";
    } else if (wind_degrees >= 112.5 && wind_degrees < 157.5) {
      direction = "SE";
    } else if (wind_degrees >= 157.5 && wind_degrees < 202.5) {
      direction = "S";
    } else if (wind_degrees >= 202.5 && wind_degrees < 247.5) {
      direction = "SW";
    } else if (wind_degrees >= 247.5 && wind_degrees < 292.5) {
      direction = "W";
    } else if (wind_degrees >= 292.5 || wind_degrees < 22.5) {
      direction = "NW";
    }

    return context.getString(windFormat, wind_speed, direction);
  }

  public int getIconResourceFromWeatherCondition(int weatherId) {
    if (weatherId >= 200 && weatherId <= 232) {
      return R.drawable.ic_storm;
    } else if (weatherId >= 300 && weatherId <= 321) {
      return R.drawable.ic_light_rain;
    } else if (weatherId >= 500 && weatherId <= 504) {
      return R.drawable.ic_rain;
    } else if (weatherId == 511) {
      return R.drawable.ic_snow;
    } else if (weatherId >= 520 && weatherId <= 531) {
      return R.drawable.ic_rain;
    } else if (weatherId >= 600 && weatherId <= 622) {
      return R.drawable.ic_snow;
    } else if (weatherId >= 701 && weatherId <= 761) {
      return R.drawable.ic_fog;
    } else if (weatherId == 761 || weatherId == 781) {
      return R.drawable.ic_storm;
    } else if (weatherId == 800) {
      return R.drawable.ic_clear;
    } else if (weatherId == 801) {
      return R.drawable.ic_light_clouds;
    } else if (weatherId >= 802 && weatherId <= 804) {
      return R.drawable.ic_cloudy;
    }
    return -1;
  }

  public int getArtResourceForWeatherCondition(int weatherId) {
    if (weatherId >= 200 && weatherId <= 232) {
      return R.drawable.art_storm;
    } else if (weatherId >= 300 && weatherId <= 321) {
      return R.drawable.art_light_rain;
    } else if (weatherId >= 500 && weatherId <= 504) {
      return R.drawable.art_rain;
    } else if (weatherId == 511) {
      return R.drawable.art_snow;
    } else if (weatherId >= 520 && weatherId <= 531) {
      return R.drawable.art_rain;
    } else if (weatherId >= 600 && weatherId <= 622) {
      return R.drawable.art_rain;
    } else if (weatherId >= 701 && weatherId <= 761) {
      return R.drawable.art_fog;
    } else if (weatherId == 761 || weatherId == 781) {
      return R.drawable.art_storm;
    } else if (weatherId == 800) {
      return R.drawable.art_clear;
    } else if (weatherId == 801) {
      return R.drawable.art_light_clouds;
    } else if (weatherId >= 802 && weatherId <= 804) {
      return R.drawable.art_clouds;
    }
    return -1;
  }
}
