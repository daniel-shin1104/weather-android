package com.daniel.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.daniel.sunshine.etc.Pref_;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  @Pref Pref_ pref;
  private final String LOG_TAG = "sunshine:" + MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // This makes action bars to be on same elevation (i.e. no casting shadow)
    getSupportActionBar().setElevation(0f);
  }

//  public void openPreferredLocationInMap() {
//    String location = pref.location().get();
//
//    Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
//      .appendQueryParameter("q", location)
//      .build();
//
//    Intent intent = new Intent(Intent.ACTION_VIEW);
//    intent.setData(geoLocation);
//
//    if (intent.resolveActivity(getPackageManager()) != null) {
//      startActivity(intent);
//    } else {
//      Log.d(LOG_TAG, "Couldn't call " + location);
//    }
//  }


}
