package com.daniel.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.androidannotations.annotations.*;

import java.util.Date;


@EActivity(R.layout.activity_detail)
public class DetailActivity extends AppCompatActivity {
  @ViewById Toolbar toolbar;

  @Bean Utility utility;

  @Extra int weather_id;
  @Extra Date date;
  @Extra String description;
  @Extra double high;
  @Extra double low;
  @Extra int humidity;
  @Extra double wind_speed;
  @Extra double wind_degrees;
  @Extra double pressure;

  @AfterViews
  void onViewCreated() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setElevation(0f);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(true);

    getSupportActionBar().setTitle(utility.getFormattedMonthDay(date));
  }
}
