package com.daniel.sunshine.service;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.daniel.sunshine.http.RestClient;
import com.daniel.sunshine.http.WeatherResponse;
import com.daniel.sunshine.persistence.Location;
import com.daniel.sunshine.persistence.Weather;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;

import java.util.Date;

@EIntentService
public class SunshineService extends AbstractIntentService {
  public SunshineService() {
    super(SunshineService.class.getSimpleName());
  }

  @ServiceAction
  @Background
  void requestWeatherInformation(android.location.Location loc) {
    RestClient.get().getForecast(loc.getLatitude(), loc.getLongitude())
      .subscribe(weatherResponse -> {
        weatherResponse.printAll();

        ActiveAndroid.beginTransaction();
        // Reset before saving.
        new Delete().from(Weather.class).execute();
        try {
          // Persist Location
          Location location = new Location();
          location.city_name = weatherResponse.city.name;
          location.coord_lat = weatherResponse.city.coord.lat;
          location.coord_lon = weatherResponse.city.coord.lon;
          location.save();

          for (WeatherResponse._List item : weatherResponse.list) {
            // Persist Weather
            Weather weather = new Weather();
            weather.weather_id = item.weather.get(0).id;
            weather.location = location;
            weather.date = new Date(item.dt * 1000);
            weather.short_description = item.weather.get(0).main;
            weather.temperature_min = item.temp.min;
            weather.temperature_max = item.temp.max;
            weather.humidity = item.humidity;
            weather.pressure = item.pressure;
            weather.wind_speed = item.speed;
            weather.wind_degrees = item.deg;
            weather.save();
          }
          ActiveAndroid.setTransactionSuccessful();
        } finally {
          ActiveAndroid.endTransaction();
        }
      });
  }
}