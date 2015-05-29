package com.daniel.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.*;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
  private static final String LOG_TAG = "sunshine:" + DetailActivityFragment.class.getSimpleName();
  private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
  private String forecastStr;
  
  public DetailActivityFragment() {
    setHasOptionsMenu(true);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    // The detail Activity called via intent. Inspect this intent
    Intent intent = getActivity().getIntent();

    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
      forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
      ((TextView) rootView.findViewById(R.id.detail_text))
        .setText(forecastStr);
    }

    return rootView;
  }

  private Intent createShareForecastIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);

    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, forecastStr + FORECAST_SHARE_HASHTAG);

    return shareIntent;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present
    inflater.inflate(R.menu.menu_detail, menu);

    // Retrieve the share menu item
    MenuItem menuItem = menu.findItem(R.id.action_share);

    // Get the provider and hold onto it to set/change the share intent
    android.support.v7.widget.ShareActionProvider shareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    // Attach an intent to this ShareActionProvider. you can update this at any time.
    // like when the user selects a new piece of data they might like to share
    if (shareActionProvider != null) {
      shareActionProvider.setShareIntent(createShareForecastIntent());
    } else {
      Log.d(LOG_TAG, "Share Action Provider is null?");
    }

  }
}
