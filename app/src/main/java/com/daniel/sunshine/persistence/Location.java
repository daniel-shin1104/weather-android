package com.daniel.sunshine.persistence;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Location", id = BaseColumns._ID)
public class Location extends Model {
  @Column public String location_setting;
  @Column public String city_name;
  @Column public double coord_lat;
  @Column public double coord_lon;

  // This method is optional. Doesn't affect the foreign key creation.
  public List<Weather> getManyWeathers() {
    return getMany(Weather.class, "location");
  }
}
