package com.daniel.sunshine;

import android.content.Context;
import com.daniel.sunshine.etc.Pref_;
import com.daniel.sunshine.service.SunshineService;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;

@EBean
public class JSONParser {
  @RootContext SunshineService sunshineService;
  @Pref Pref_ pref;

  private final String LOG_TAG = "sunshine:" + JSONParser.class.getSimpleName();

  private boolean DEBUG = true;

  /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
  private String getReadableDateString(long time){
    // Because the API returns a unix timestamp (measured in seconds),
    // it must be converted to milliseconds in order to be converted to valid date.
    Date date = new Date(time * 1000);
    SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
    return format.format(date);
  }

  /**
   * Prepare the weather high/lows for presentation.
   */
  private String formatHighLows(Context context, double high, double low) {
    String temperatureUnit = pref.temperatureUnit().get();

    if (temperatureUnit.equals(context.getString(R.string.pref_units_farenheit))) {
      high = (high * 1.8) + 32;
      low = (low * 1.8) + 32;
    }

    // For presentation, assume the user doesn't care about tenth of a degree
    long roundedHigh = Math.round(high);
    long roundedLow = Math.round(low);

    return roundedHigh + "/" + roundedLow;
  }
}












