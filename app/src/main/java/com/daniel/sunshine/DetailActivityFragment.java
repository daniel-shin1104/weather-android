package com.daniel.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

@EFragment(R.layout.fragment_detail)
public class DetailActivityFragment extends Fragment {
  @Bean Utility utility;

  private String location;
  private String forecast;

  @ViewById(R.id.detail_icon) ImageView iconView;
  @ViewById(R.id.detail_date_textview) TextView dateView;
  @ViewById(R.id.detail_forecast_textview) TextView descriptionView;
  @ViewById(R.id.detail_high_textview) TextView highTempView;
  @ViewById(R.id.detail_low_textview) TextView lowTempView;
  @ViewById(R.id.detail_humidity_textview) TextView humidityView;
  @ViewById(R.id.detail_wind_textview) TextView windView;
  @ViewById(R.id.detail_pressure_textview) TextView pressureView;

  @AfterViews
  void onViewCreated() {
    DetailActivity detailActivity = (DetailActivity) getActivity();

    /* Get Extra */
    int weatherId = detailActivity.weather_id;
    Date date = detailActivity.date;
    String description = detailActivity.description;
    double high = detailActivity.high;
    double low = detailActivity.low;
    int humidity = detailActivity.humidity;
    double wind_speed = detailActivity.wind_speed;
    double wind_degrees = detailActivity.wind_degrees;
    double pressure = detailActivity.pressure;

    iconView.setImageResource(utility.getArtResourceForWeatherCondition(weatherId));

    // Read date from cursor and update views for day of week and date
    String dateText = utility.getFormattedMonthDay(date);
    dateView.setText(dateText);

    descriptionView.setText(description);

    boolean isCelsius = utility.isCelsius();

    // Read high temperature from cursor and update view
    String highString = utility.formatTemperature(getActivity(), high, isCelsius);
    highTempView.setText(highString);

    // Read low temperature from cursor and update view
    String lowString = utility.formatTemperature(getActivity(), low, isCelsius);
    lowTempView.setText(lowString);

    // Read humidity from cursor and update view
    humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

    // Read wind speed and direction from cursor and update view
    windView.setText(utility.getFormattedWind(wind_speed, wind_degrees));

    // Read pressure from cursor and update view
    pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

    // We still need this for the share intent
    forecast = String.format("%s - %s - %s/%s", dateText, description, high, low);
  }

  private Intent createShareForecastIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + "#SunshineApp");
    return shareIntent;
  }
}







