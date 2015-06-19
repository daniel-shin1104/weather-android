package com.daniel.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.activeandroid.content.ContentProvider;
import com.daniel.sunshine.persistence.Weather;
import com.daniel.sunshine.service.SunshineService_;
import org.androidannotations.annotations.*;

@EFragment(R.layout.fragment_main)
@OptionsMenu(R.menu.main)
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {
  @ViewById(R.id.recyclerview_forecast) RecyclerView recyclerView;
  @ViewById(R.id.recyclerview_forecast_empty) TextView emptyView;

  // Portrait only
  @ViewById(R.id.appbar) AppBarLayout appBarLayout;

  // Landscape only
  @ViewById(R.id.parallax_bar) AppBarLayout parallaxBarLayout;

  @Bean ForecastAdapter forecastAdapter;
  @Bean Utility utility;

  @AfterViews
  void onViewCreated() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(forecastAdapter);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Implement parallax scrolling.
        if (parallaxBarLayout != null) {
          int max = parallaxBarLayout.getHeight();

          if (dy > 0) {
            parallaxBarLayout.setTranslationY(Math.max(-max, parallaxBarLayout.getTranslationY() - dy / 2));
          } else {
            parallaxBarLayout.setTranslationY(Math.min(0, parallaxBarLayout.getTranslationY() - dy / 2));
          }
        }

        // Implement AppBar elevation
        if (appBarLayout != null) {
          // FIXME: doens't work with pre-v21
          if (recyclerView.computeVerticalScrollOffset() == 0) {
            ViewCompat.setElevation(appBarLayout, 0);
          } else {
            ViewCompat.setElevation(appBarLayout, appBarLayout.getTargetElevation());
          }
        }
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (recyclerView != null) {
      recyclerView.clearOnScrollListeners();
    }
  }

  @OptionsItem(R.id.action_refresh)
  void onOptionsActionRefreshSelected() {
    updateWeather();
  }

  @OptionsItem(R.id.action_settings)
  void onOptionsActionSettingsSelected() {
    startActivity(new Intent(getActivity(), SettingsActivity_.class));
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(0, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  public void updateWeather() {
    SunshineService_.intent(getActivity().getApplication())
      .requestWeatherInformation()
      .start();
  }

  @Override
  public void onStart() {
    super.onStart();
    updateWeather();
  }

  ////////////////////////////////////////////
  // CURSOR_LOADER CALLBACKS
  ////////////////////////////////////////////
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
    return new CursorLoader(
      getActivity(),
      ContentProvider.createUri(Weather.class, null),
      null,
      null,
      null,
      Weather.Columns.DATE.columnName + " ASC"
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    forecastAdapter.swapCursor(data);

    if (forecastAdapter.getItemCount() == 0) {
      updateEmptyView();
    } else {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    forecastAdapter.swapCursor(null);
  }

  private void updateEmptyView() {
    int emptyViewMessageId = R.string.empty_forecast_list;

    if (!utility.isNetworkAvailable()) {
      emptyViewMessageId = R.string.empty_forecast_list_no_network;
    }

    emptyView.setText(emptyViewMessageId);

    emptyView.setVisibility(View.VISIBLE);
  }
}






