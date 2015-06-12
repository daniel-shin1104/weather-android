package com.daniel.sunshine.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Weather")
public class Weather extends Model {
  @Column(index = true) public String weather_id;
  @Column public Location location;
  @Column public String date;
  @Column public String short_description;
  @Column public String temperature_min;
  @Column public String temperature_max;
  @Column public String humidity;
  @Column public String pressure;
  @Column public String wind;
  @Column public String degrees;
}
