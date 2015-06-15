package com.daniel.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  @ViewById Toolbar toolbar;

  @AfterViews
  void onViewCreated() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setElevation(0f);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
  }
}
