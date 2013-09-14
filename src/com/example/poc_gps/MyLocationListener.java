package com.example.poc_gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	public static double latitudeGPS;
	public static double longitudeGPS;

	public static double latitudeWifi;
	public static double longitudeWifi;

	private String provider;

	public MyLocationListener(String prov) {
		this.provider = prov;
	}

	@Override
	public void onLocationChanged(Location loc) {
		loc.getLatitude();
		loc.getLongitude();
		if (this.provider.equals(LocationManager.GPS_PROVIDER)) {
			latitudeGPS = loc.getLatitude();
			longitudeGPS = loc.getLongitude();
		} else {
			latitudeWifi = loc.getLatitude();
			longitudeWifi = loc.getLongitude();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// print "Currently GPS is Disabled";
	}

	@Override
	public void onProviderEnabled(String provider) {
		// print "GPS got Enabled";
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}