package com.daniel.sunshine;

import android.support.v7.app.ActionBarActivity;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.util.Date;


@EActivity(R.layout.activity_detail)
public class DetailActivity extends ActionBarActivity {
  @Extra int weather_id;
  @Extra Date date;
  @Extra String description;
  @Extra double high;
  @Extra double low;
  @Extra int humidity;
  @Extra double wind_speed;
  @Extra double wind_degrees;
  @Extra double pressure;
}
