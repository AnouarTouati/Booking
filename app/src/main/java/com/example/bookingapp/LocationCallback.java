package com.example.bookingapp;

import android.location.Location;

abstract class LocationCallback {
      abstract void locationResultsCallback(Location location);
      boolean comingBackFromLocationSettings = false  ;
}
