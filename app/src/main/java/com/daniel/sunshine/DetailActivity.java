package com.daniel.sunshine;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DetailActivity extends ActionBarActivity {
  public static final String DATE_KEY = "forecast_date";
  private static final String LOCATION_KEY = "location";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
  }
}
