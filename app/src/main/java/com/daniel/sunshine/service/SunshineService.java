package com.daniel.sunshine.service;

import com.activeandroid.ActiveAndroid;
import com.daniel.sunshine.http.RestClient;
import com.daniel.sunshine.http.WeatherResponse;
import com.daniel.sunshine.persistence.Location;
import com.daniel.sunshine.persistence.Weather;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EIntentService
public class SunshineService extends AbstractIntentService {
  private final String LOG_TAG = SunshineService.class.getSimpleName();

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

        ActiveAndroid.beginTransaction();
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
            weather.date = item.dt;
            weather.short_description = item.weather.get(0).main;
            weather.temperature_min = item.temp.min;
            weather.temperature_max = item.temp.max;
            weather.humidity = item.humidity;
            weather.pressure = item.pressure;
            weather.wind = item.speed;
            weather.degrees = item.deg;
            weather.save();
          }
          ActiveAndroid.setTransactionSuccessful();
        }
        finally {
          ActiveAndroid.endTransaction();
        }
      }

      @Override
      public void failure(RetrofitError error) {
        error.printStackTrace();
      }
    });
  }
}
