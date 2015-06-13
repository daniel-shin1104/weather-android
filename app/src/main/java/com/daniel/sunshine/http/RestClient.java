package com.daniel.sunshine.http;

import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestClient {
  private static OpenWeatherMap REST_CLIENT;
  private static String ROOT = "http://api.openweathermap.org/data/2.5";

  static {
    setup();
  }

  public static OpenWeatherMap get() {
    return REST_CLIENT;
  }

  private static void setup() {
    RestAdapter.Builder builder = new RestAdapter.Builder()
      .setEndpoint(ROOT)
      .setClient(new OkClient(new OkHttpClient()))
      .setLogLevel(RestAdapter.LogLevel.FULL);

    RestAdapter restAdapter = builder.build();

    REST_CLIENT = restAdapter.create(OpenWeatherMap.class);
  }
}
