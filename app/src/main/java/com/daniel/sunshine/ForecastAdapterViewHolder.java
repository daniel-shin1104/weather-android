package com.daniel.sunshine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  ImageView iconView;
  TextView dateView;
  TextView descriptionView;
  TextView highTempView;
  TextView lowTempView;

  ForecastAdapterOnClickListner forecastAdatperOnClickListener;

  public ForecastAdapterViewHolder(View view, ForecastAdapterOnClickListner forecastAdapterOnClickListner) {
    super(view);

    this.forecastAdatperOnClickListener = forecastAdapterOnClickListner;

    iconView = (ImageView) view.findViewById(R.id.list_item_icon);
    dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
    descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
    highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
    lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);

    view.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    forecastAdatperOnClickListener.onClick(getAdapterPosition());
//    cursor.moveToPosition(adapterPosition);
//
//    long date = cursor.getLong(cursor.getColumnIndex(Weather.Columns.DATE.columnName));
//    forecastAdatperOnClickHandler.onClick(date, this);
  }
}
