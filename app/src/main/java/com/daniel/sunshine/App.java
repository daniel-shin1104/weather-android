package com.daniel.sunshine;

import android.content.Context;
import org.androidannotations.annotations.EApplication;

@EApplication
public class App extends com.activeandroid.app.Application {
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = this;
  }

  public static Context getContext() {
    return context;
  }
}
