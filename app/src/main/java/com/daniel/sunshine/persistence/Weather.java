package com.daniel.sunshine.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "OpenWeatherMap")
public class Weather extends Model {
  @Column(index = true) public int weather_id;
  @Column public Location location;
  @Column public long date;
  @Column public String short_description;
  @Column public double temperature_min;
  @Column public double temperature_max;
  @Column public int humidity;
  @Column public double pressure;
  @Column public double wind;
  @Column public double degrees;
}
