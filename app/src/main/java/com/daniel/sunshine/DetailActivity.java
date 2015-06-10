package com.daniel.sunshine;

import android.support.v7.app.ActionBarActivity;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;


@EActivity(R.layout.activity_detail)
public class DetailActivity extends ActionBarActivity {
  // Passed Extra arguments from Intent
  @Extra String forecast_date;
}
