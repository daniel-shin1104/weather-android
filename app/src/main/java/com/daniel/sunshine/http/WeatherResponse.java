package com.daniel.sunshine.http;

import android.util.Log;

import java.util.List;

public class WeatherResponse {
  public int cod;
  public int cnt;
  public _City city;
  public List<_List> list;

  public static class _City {
    public String country;
    public int id;
    public String name;
    public Coord coord;

    public static class Coord {
      public double lat;
      public double lon;
    }
  }

  public static class _List {
    public long dt;
    public double pressure;
    public int humidity;
    public double speed;
    public double deg;
    public Temp temp;
    public List<Weather> weather;

    public static class Temp {
      public double max;
      public double min;
    }

    public static class Weather {
      public String main;
      public int id;
    }
  }

  public void printAll() {
    Log.i("HELLO", "cnt: " + String.valueOf(cnt));
    Log.i("HELLO", "cod: " + String.valueOf(cod));
    Log.i("HELLO", "city_id: " + String.valueOf(city.id));
    Log.i("HELLO", "city_country: " + String.valueOf(city.country));
    Log.i("HELLO", "city_name: " + String.valueOf(city.name));
    Log.i("HELLO", "city_coord_lat: " + String.valueOf(city.coord.lat));
    Log.i("HELLO", "city_coord_lon: " + String.valueOf(city.coord.lon));

    for (WeatherResponse._List item : list) {
      Log.i("HELLO", "list_item_dt: " + String.valueOf(item.dt));
      Log.i("HELLO", "list_item_pressure: " + String.valueOf(item.pressure));
      Log.i("HELLO", "list_item_humidity: " + String.valueOf(item.humidity));
      Log.i("HELLO", "list_item_speed: " + String.valueOf(item.speed));
      Log.i("HELLO", "list_item_deg: " + String.valueOf(item.deg));
      Log.i("HELLO", "list_item_temp_max: " + String.valueOf(item.temp.max));
      Log.i("HELLO", "list_item_temp_min: " + String.valueOf(item.temp.min));
      Log.i("HELLO", "list_item_weather_id: " + String.valueOf(item.weather.get(0).id));
      Log.i("HELLO", "list_item_weather_main: " + String.valueOf(item.weather.get(0).main));
    }
  }
}
