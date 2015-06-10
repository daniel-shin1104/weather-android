package com.daniel.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  private final String LOG_TAG = "sunshine:" + MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // This makes action bars to be on same elevation (i.e. no casting shadow)
    getSupportActionBar().setElevation(0f);
  }

  public void openPreferredLocationInMap() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

    String location = sharedPrefs.getString(
      getString(R.string.pref_location_key),
      getString(R.string.pref_location_default)
    );

    Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
      .appendQueryParameter("q", location)
      .build();

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(geoLocation);

    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivity(intent);
    } else {
      Log.d(LOG_TAG, "Couldn't call " + location);
    }
  }

}
