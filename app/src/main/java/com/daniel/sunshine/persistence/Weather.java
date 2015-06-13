package com.daniel.sunshine.persistence;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Weather", id = BaseColumns._ID)
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

  public static List<Weather> getAll() {
    return new Select()
      .from(Weather.class)
      .orderBy("date ASC")
      .execute();
  }

  @Override
  public String toString() {
    return "Weather{" +
      "weather_id=" + weather_id +
      ", location=" + location +
      ", date=" + date +
      ", short_description='" + short_description + '\'' +
      ", temperature_min=" + temperature_min +
      ", temperature_max=" + temperature_max +
      ", humidity=" + humidity +
      ", pressure=" + pressure +
      ", wind=" + wind +
      ", degrees=" + degrees +
      '}';
  }

  public static enum Columns {
    WEATHER_ID,
    LOCATION,
    DATE,
    SHORT_DESCRIPTION,
    TEMPERATURE_MIN,
    TEMPERATURE_MAX,
    HUMIDITY,
    PRESSURE,
    WIND,
    DEGREES;

    public String columnName;

    static {
      WEATHER_ID.columnName = "weather_id";
      LOCATION.columnName = "location";
      DATE.columnName = "date";
      SHORT_DESCRIPTION.columnName = "short_description";
      TEMPERATURE_MIN.columnName = "temperature_min";
      TEMPERATURE_MAX.columnName = "temperature_max";
      HUMIDITY.columnName = "humidity";
      PRESSURE.columnName = "pressure";
      WIND.columnName = "wind";
      DEGREES.columnName = "degrees";
    }
  }
}
