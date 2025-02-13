package com.daniel.sunshine;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.daniel.sunshine.etc.Pref_;
import com.google.common.base.Optional;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.LocalDate;

import java.util.Date;

@EBean(scope = EBean.Scope.Singleton)
public class Utility {
  @RootContext Context context;
  @Pref Pref_ pref;
  @SystemService ConnectivityManager connectivityManager;

  public boolean isCelsius() {
    return pref.temperatureUnit().get().equals(context.getString(R.string.pref_units_celsius));
  }

  public String formatTemperature(Context context, double temperature, boolean isCelsius) {
    double temp = isCelsius ? temperature : ((temperature * 9) / 5) + 32;

    return context.getString(R.string.format_temperature, temp);
  }

  public String getFriendlyDayString(Date date) {
    LocalDate inputDate = new LocalDate(date);
    LocalDate todayDate = new LocalDate();

    if (inputDate.equals(todayDate)) {
      // Today, June 24th
      return context.getString(R.string.format_full_friendly_date, context.getString(R.string.today), getFormattedMonthDay(date));

    } else if (inputDate.isBefore(todayDate.plusWeeks(1))) {

      // Tomorrow, Wednesday ...
      return getDayName(date);
    } else {

      // Mon, Jun 1st
      return inputDate.toString("EEE, MMM dd");
    }
  }

  public String getDayName(Date date) {
    LocalDate inputDate = new LocalDate(date);
    LocalDate todayDate = new LocalDate();

    if (inputDate.equals(todayDate)) {
      return context.getString(R.string.today);

    } else if (inputDate.equals(todayDate.plusDays(1))) {
      return context.getString(R.string.tomorrow);

    } else {
      return inputDate.dayOfWeek().getAsText();
    }
  }

  public String getFormattedMonthDay(Date date) {
    return new LocalDate(date).toString("MMMM dd");
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

  enum WeatherCondition {
    STORM,
    LIGHT_RAIN,
    RAIN,
    SNOW,
    FOG,
    CLEAR,
    LIGHT_CLOUDS,
    CLOUDS;

    int getIconResourceId() {
      switch (this) {
        case STORM: return R.drawable.ic_storm;
        case LIGHT_RAIN: return R.drawable.ic_light_rain;
        case RAIN: return R.drawable.ic_rain;
        case SNOW: return R.drawable.ic_snow;
        case FOG: return R.drawable.ic_fog;
        case CLEAR: return R.drawable.ic_clear;
        case LIGHT_CLOUDS: return R.drawable.ic_light_clouds;
        case CLOUDS: return R.drawable.ic_cloudy;
        default: return -1;
      }
    }

    int getArtResourceId() {
      switch (this) {
        case STORM: return R.drawable.art_storm;
        case LIGHT_RAIN: return R.drawable.art_light_rain;
        case RAIN: return R.drawable.art_rain;
        case SNOW: return R.drawable.art_snow;
        case FOG: return R.drawable.art_fog;
        case CLEAR: return R.drawable.art_clear;
        case LIGHT_CLOUDS: return R.drawable.art_light_clouds;
        case CLOUDS: return R.drawable.art_clouds;
        default: return -1;
      }
    }

    String getArtResourceUrl() {
      return App.getContext().getString(R.string.format_art_pack_url, this.name().toLowerCase());
    }

    static Optional<WeatherCondition> fromWeatherId(int weatherId) {
      if (weatherId >= 200 && weatherId <= 232) {
        return Optional.of(STORM);
      } else if (weatherId >= 300 && weatherId <= 321) {
        return Optional.of(LIGHT_RAIN);
      } else if (weatherId >= 500 && weatherId <= 504) {
        return Optional.of(RAIN);
      } else if (weatherId == 511) {
        return Optional.of(SNOW);
      } else if (weatherId >= 520 && weatherId <= 531) {
        return Optional.of(RAIN);
      } else if (weatherId >= 600 && weatherId <= 622) {
        return Optional.of(SNOW);
      } else if (weatherId >= 701 && weatherId <= 761) {
        return Optional.of(FOG);
      } else if (weatherId == 761 || weatherId == 781) {
        return Optional.of(STORM);
      } else if (weatherId == 800) {
        return Optional.of(CLEAR);
      } else if (weatherId == 801) {
        return Optional.of(LIGHT_CLOUDS);
      } else if (weatherId >= 802 && weatherId <= 804) {
        return Optional.of(CLOUDS);
      }

      return Optional.absent();
    }
  }

  public int getIconResourceFromWeatherCondition(int weatherId) {
    Optional<WeatherCondition> weatherCondition = WeatherCondition.fromWeatherId(weatherId);
    return weatherCondition.isPresent() ? weatherCondition.get().getIconResourceId() : -1;
  }

  public int getArtResourceForWeatherCondition(int weatherId) {
    Optional<WeatherCondition> weatherCondition = WeatherCondition.fromWeatherId(weatherId);
    return weatherCondition.isPresent() ? weatherCondition.get().getArtResourceId() : -1;
  }

  public String getArtResourceURLForWeatherCondition(int weatherId) {
    Optional<WeatherCondition> weatherCondition = WeatherCondition.fromWeatherId(weatherId);
    return weatherCondition.isPresent() ? weatherCondition.get().getArtResourceUrl() : "";
  }

  public boolean isNetworkAvailable() {
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }
}
