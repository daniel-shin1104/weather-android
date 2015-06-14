package com.daniel.sunshine.http;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface OpenWeatherMap {
  @GET("/forecast/daily?units=metric")
  void getForecast(
    @Query("q") String location,
    Callback<WeatherResponse> cb
    );
}
