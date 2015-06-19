package com.daniel.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.daniel.sunshine.persistence.Weather;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.Date;

@EBean
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapterViewHolder> implements ForecastAdapterOnClickListner {
  @Bean Utility utility;
  @RootContext Context context;

  private Cursor cursor;

  private enum ViewType {
    TODAY(0),
    FUTURE_DAY(1);

    int viewTypeIndex;
    ViewType(int viewTypeIndex) {
      this.viewTypeIndex = viewTypeIndex;
    }

    int getLayoutId() {
      switch (this) {
        case TODAY: return R.layout.list_item_forecast_today;
        case FUTURE_DAY: return R.layout.list_item_forecast;
        default: return -1;
      }
    }

    static int getLength() { return ViewType.values().length; }

    static Optional<ViewType> fromViewTypeIndex(int viewTypeIndex) {
      for (ViewType viewType : ViewType.values()) {
        if (viewType.viewTypeIndex == viewTypeIndex) {

          return Optional.of(viewType);
        }
      }
      return Optional.absent();
    }
  }

  @Override
  public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewTypeIndex) {
    Optional<ViewType> viewType = ViewType.fromViewTypeIndex(viewTypeIndex);

    int layoutId = viewType.isPresent() ? viewType.get().getLayoutId() : -1;

    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
    view.setFocusable(true);

    return new ForecastAdapterViewHolder(view, this);
  }

  @Override
  public void onBindViewHolder(ForecastAdapterViewHolder viewHolder, int position) {
    if (cursor != null && cursor.moveToPosition(position)) {
      // Read data from cursor
      int weatherId = cursor.getInt(cursor.getColumnIndex(Weather.Columns.WEATHER_ID.columnName));
      Date date = new Date(cursor.getLong(cursor.getColumnIndex(Weather.Columns.DATE.columnName)));
      String description = cursor.getString(cursor.getColumnIndex(Weather.Columns.SHORT_DESCRIPTION.columnName));
      double high = cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MAX.columnName));
      double low = cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MIN.columnName));

      int viewTypeIndex = getItemViewType(cursor.getPosition());
      Optional<ViewType> viewType = ViewType.fromViewTypeIndex(viewTypeIndex);
      if (!viewType.isPresent()) { throw new NullPointerException(); }

      // Enable debugging
      Picasso.with(context).setIndicatorsEnabled(true);
      switch (viewType.get()) {
        case TODAY:
          Picasso.with(context)
            .load(utility.getArtResourceURLForWeatherCondition(weatherId))
            .error(utility.getArtResourceForWeatherCondition(weatherId))
            .into(viewHolder.iconView);
          break;

        case FUTURE_DAY:
          Picasso.with(context)
            .load(utility.getArtResourceURLForWeatherCondition(weatherId))
            .error(utility.getIconResourceFromWeatherCondition(weatherId))
            .into(viewHolder.iconView);
          break;

        default: break;
      }

      viewHolder.dateView.setText(utility.getFriendlyDayString(date));
      viewHolder.descriptionView.setText(description);

      boolean isCelsius = utility.isCelsius();
      viewHolder.highTempView.setText(utility.formatTemperature(context, high, isCelsius));
      viewHolder.lowTempView.setText(utility.formatTemperature(context, low, isCelsius));
    }
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? ViewType.TODAY.viewTypeIndex : ViewType.FUTURE_DAY.viewTypeIndex;
  }

  @Override
  public int getItemCount() {
    // cursor may be null when accessed prematurely while syncing with data model hasn't been fully established
    return cursor != null ? cursor.getCount() : 0;
  }

  public void swapCursor(Cursor cursor) {
    this.cursor = cursor;
    notifyDataSetChanged();
  }

  @Override
  public void onClick(int adapterPosition) {
    if (cursor != null && cursor.moveToPosition(adapterPosition)) {
      DetailActivity_.intent(context)
        .weather_id(cursor.getInt(cursor.getColumnIndex(Weather.Columns.WEATHER_ID.columnName)))
        .date(new Date(cursor.getLong(cursor.getColumnIndex(Weather.Columns.DATE.columnName))))
        .description(cursor.getString(cursor.getColumnIndex(Weather.Columns.SHORT_DESCRIPTION.columnName)))
        .high(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MAX.columnName)))
        .low(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MIN.columnName)))
        .humidity(cursor.getInt(cursor.getColumnIndex(Weather.Columns.HUMIDITY.columnName)))
        .wind_speed(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.WIND_SPEED.columnName)))
        .wind_degrees(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.WIND_DEGREES.columnName)))
        .pressure(cursor.getDouble(cursor.getColumnIndex(Weather.Columns.PRESSURE.columnName)))
        .start();
    }
  }
}















