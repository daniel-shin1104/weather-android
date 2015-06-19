package com.daniel.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Date;


@EActivity(R.layout.activity_detail)
public class DetailActivity extends AppCompatActivity {
  @ViewById Toolbar toolbar;

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
    getSupportActionBar().setDisplayShowTitleEnabled(false);
  }
}
