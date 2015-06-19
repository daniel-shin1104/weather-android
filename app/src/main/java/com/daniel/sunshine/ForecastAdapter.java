package com.daniel.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.daniel.sunshine.persistence.Weather;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.Date;

@EBean
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
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
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView iconView;
    TextView dateView;
    TextView descriptionView;
    TextView highTempView;
    TextView lowTempView;

    ViewHolder(View view) {
      super(view);

      iconView = (ImageView) view.findViewById(R.id.list_item_icon);
      dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
      descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
      highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
      lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewTypeIndex) {
    Optional<ViewType> viewType = ViewType.fromViewTypeIndex(viewTypeIndex);

    int layoutId = viewType.isPresent() ? viewType.get().getLayoutId() : -1;

    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
    view.setFocusable(true);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    cursor.moveToPosition(position);

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

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? ViewType.TODAY.viewTypeIndex : ViewType.FUTURE_DAY.viewTypeIndex;
  }

  @Override
  public int getItemCount() {
    return ViewType.getLength();
  }

  public void swapCursor(Cursor cursor) {
    this.cursor = cursor;
    notifyDataSetChanged();
  }

  public Cursor getCursor() {
    return cursor;
  }
}















