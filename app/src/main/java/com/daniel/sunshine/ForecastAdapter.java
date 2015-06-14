package com.daniel.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.daniel.sunshine.persistence.Weather;
import com.google.common.base.Optional;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class ForecastAdapter extends CursorAdapter {
  @Bean Utility utility;

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
  
  private static class ViewHolder {
    ImageView iconView;
    TextView dateView;
    TextView descriptionView;
    TextView highTempView;
    TextView lowTempView;

    ViewHolder(View view) {
      iconView = (ImageView) view.findViewById(R.id.list_item_icon);
      dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
      descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
      highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
      lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
    }
  }

  public ForecastAdapter(Context context) {
    super(context, null, 0);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    int viewTypeIndex = getItemViewType(cursor.getPosition());

    Optional<ViewType> viewType = ViewType.fromViewTypeIndex(viewTypeIndex);
    int layoutId = viewType.isPresent() ? viewType.get().getLayoutId() : -1;

    View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

    ViewHolder viewHolder = new ViewHolder(view);
    view.setTag(viewHolder);

    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {

    ViewHolder viewHolder = (ViewHolder) view.getTag();

    // Read data from cursor
    int weatherId = cursor.getColumnIndex(Weather.Columns.WEATHER_ID.columnName);
    long date = cursor.getLong(cursor.getColumnIndex(Weather.Columns.DATE.columnName));
    String description = cursor.getString(cursor.getColumnIndex(Weather.Columns.SHORT_DESCRIPTION.columnName));
    double high = cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MAX.columnName));
    double low = cursor.getDouble(cursor.getColumnIndex(Weather.Columns.TEMPERATURE_MIN.columnName));

    int viewTypeIndex = getItemViewType(cursor.getPosition());
    Optional<ViewType> viewType = ViewType.fromViewTypeIndex(viewTypeIndex);
    if (!viewType.isPresent()) { throw new NullPointerException(); }

    switch (viewType.get()) {
      case TODAY:
        viewHolder.iconView.setImageResource(utility.getArtResourceForWeatherCondition(
          cursor.getInt(weatherId)
        ));
        break;

      case FUTURE_DAY:
        viewHolder.iconView.setImageResource(utility.getIconResourceFromWeatherCondition(
          cursor.getInt(weatherId)
        ));
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
  public int getViewTypeCount() {
    return ViewType.getLength();
  }
}
