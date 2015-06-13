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
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class ForecastAdapter extends CursorAdapter {
  private static final int VIEW_TYPE_COUNT = 2;
  private static final int VIEW_TYPE_TODAY = 0;
  private static final int VIEW_TYPE_FUTURE_DAY = 1;
  
  @Bean Utility utility;

  // TODO: Use annotations
  public static class ViewHolder {
    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;

    public ViewHolder(View view) {
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
    // Choose the layout type
    int viewType = getItemViewType(cursor.getPosition());

    int layoutId;
    switch (viewType) {
      case VIEW_TYPE_TODAY:
        layoutId = R.layout.list_item_forecast_today;
        break;

      case VIEW_TYPE_FUTURE_DAY:
        layoutId = R.layout.list_item_forecast;
        break;

      default:
        layoutId = -1;
    }

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

    int viewType = getItemViewType(cursor.getPosition());
    switch (viewType) {
      case VIEW_TYPE_TODAY:
        viewHolder.iconView.setImageResource(utility.getArtResourceForWeatherCondition(
          cursor.getInt(weatherId)
        ));
        break;

      case VIEW_TYPE_FUTURE_DAY:
        viewHolder.iconView.setImageResource(utility.getIconResourceFromWeatherCondition(
          cursor.getInt(weatherId)
        ));
        break;

      default: break;
    }

    // FIXME: this method needs to accept long not String
    viewHolder.dateView.setText(utility.getFriendlyDayString(String.valueOf(date)));
    viewHolder.descriptionView.setText(description);

    boolean isCelsius = utility.isCelsius();
    viewHolder.highTempView.setText(utility.formatTemperature(context, high, isCelsius));
    viewHolder.lowTempView.setText(utility.formatTemperature(context, low, isCelsius));
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }
}
