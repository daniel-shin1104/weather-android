package com.daniel.sunshine.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Location")
public class Location extends Model {
  @Column public String location_setting;
  @Column public String city_name;
  @Column public String coord_lat;
  @Column public String coord_long;

  // This method is optional. Doesn't affect the foreign key creation.
  public List<Weather> getManyWeathers() {
    return getMany(Weather.class, "location");
  }
}
