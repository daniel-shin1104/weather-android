package com.daniel.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by daniel on 5/29/15.
 */

public class WeatherProvider extends ContentProvider {
  // The URI Matcher used by this content provider
  private static final UriMatcher uriMatcher = buildUriMatcher();
  private WeatherDbHelper openHelper;

  private static final int WEATHER = 100;
  private static final int WEATHER_WITH_LOCATION = 101;
  private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
  private static final int LOCATION = 300;
  private static final int LOCATION_ID = 301;

  private static UriMatcher buildUriMatcher() {
    // All paths added to the UriMatcher have a corresponding code to return when a match is found.
    // The code passed into the constructor represents the code to return for the root URI.
    // It's common to use NO_MATCH as the code for this case.
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = WeatherContract.CONTENT_AUTHORITY;

    // For each type of URI you want to add, create a corresponding code.
    matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
    matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
    matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

    matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
    matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    openHelper = new WeatherDbHelper(getContext());
    return false;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    // Here's the switch statement that, given a URI, will determine what kind of request it is, and query the database accordingly.
    Cursor cursor;
    switch (uriMatcher.match(uri)) {

      // "weather/*/*"
      case WEATHER_WITH_LOCATION_AND_DATE:
        cursor = null;
        break;

      // "weather/*"
      case WEATHER_WITH_LOCATION:
        cursor = null;
        break;

      // "weather"
      case WEATHER:
        cursor = openHelper.getReadableDatabase().query(
          WeatherContract.WeatherEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs,
          null,
          null,
          sortOrder
        );
        break;

      // "location/*"
      case LOCATION_ID:
        cursor = openHelper.getReadableDatabase().query(
          WeatherContract.LocationEntry.TABLE_NAME,
          projection,
          WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
          null,
          null,
          null,
          sortOrder
        );
        break;

      // "location"
      case LOCATION:
        cursor = openHelper.getReadableDatabase().query(
          WeatherContract.LocationEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs,
          null,
          null,
          sortOrder
        );
        break;

      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    // Use the Uri Matcher to determine what kind of URI this is.
    final int match = uriMatcher.match(uri);

    switch(match) {
      case WEATHER_WITH_LOCATION_AND_DATE:
        return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
      case WEATHER_WITH_LOCATION:
        return WeatherContract.WeatherEntry.CONTENT_TYPE;
      case WEATHER:
        return WeatherContract.WeatherEntry.CONTENT_TYPE;
      case LOCATION:
        return WeatherContract.LocationEntry.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);

    Uri retURI;
    long _id;
    switch (match) {
      case WEATHER:
        _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
        if (_id > 0) {
          retURI = WeatherContract.WeatherEntry.buildWeatherUri(_id);
        } else {
          throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        break;

      case LOCATION:
        _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
        if (_id > 0) {
          retURI = WeatherContract.LocationEntry.buildLocationUri(_id);
        } else {
          throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return retURI;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);
    int rowsDeleted;

    // This makes delete all rows return the number of rows deleted.
    if (selection == null) { selection = "1"; }
    switch (match) {
      case WEATHER:
        rowsDeleted = db.delete(
          WeatherContract.LocationEntry.TABLE_NAME,
          selection,
          selectionArgs
        );
        break;

      case LOCATION:
        rowsDeleted = db.delete(
          WeatherContract.LocationEntry.TABLE_NAME,
          selection,
          selectionArgs
        );
        break;

      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    // Because a null deletes all rows
    if (rowsDeleted > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);
    int rowsUpdated;
    switch (match) {
      case WEATHER:
        rowsUpdated = db.update(
          WeatherContract.WeatherEntry.TABLE_NAME,
          values,
          selection,
          selectionArgs
        );
        break;

      case LOCATION:
        rowsUpdated = db.update(
          WeatherContract.LocationEntry.TABLE_NAME,
          values,
          selection,
          selectionArgs
        );
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    if (rowsUpdated > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsUpdated;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);

    switch (match) {
      case WEATHER:
        db.beginTransaction();
        int returnCount = 0;
        try {
          for (ContentValues value : values) {
            long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
            if (_id != -1) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
      default:
        return super.bulkInsert(uri, values);
    }
  }
}

















