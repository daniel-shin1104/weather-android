package com.daniel.sunshine;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.daniel.sunshine.etc.Pref_;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity
@PreferenceScreen(R.xml.pref_general)
public class SettingsActivity extends PreferenceActivity {
  @PreferenceByKey(R.string.pref_location_key) Preference locationPreference;
  @PreferenceByKey(R.string.pref_units_key) Preference temperatureUnitPreference;

  @Pref Pref_ pref;

  @AfterPreferences
  void initPrefs() {
    locationPreference.setSummary(pref.location().get());
    temperatureUnitPreference.setSummary(pref.temperatureUnit().get());
  }

  @PreferenceChange({R.string.pref_location_key, R.string.pref_units_key})
  void onLocationPrefChange(Preference preference, String newValue) {
    preference.setSummary(newValue);
  }
}