package com.daniel.sunshine.etc;

import com.daniel.sunshine.R;
import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Pref {
  @DefaultRes(R.string.pref_units_celsius)
  String temperatureUnit();
}
