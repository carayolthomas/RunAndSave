package utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	public static double latitudeGPS;
	public static double longitudeGPS;
	public static long timestampGPS;

	public static double latitudeWifi;
	public static double longitudeWifi;
	public static long timestampWifi;
		
	private String provider;

	public MyLocationListener(String prov) {
		this.provider = prov;
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (this.provider.equals(LocationManager.GPS_PROVIDER)) {
			MyLocationListener.latitudeGPS = loc.getLatitude();
			MyLocationListener.longitudeGPS = loc.getLongitude();
			MyLocationListener.timestampGPS = loc.getTime();
		} else {
			MyLocationListener.latitudeWifi = loc.getLatitude();
			MyLocationListener.longitudeWifi = loc.getLongitude();
			MyLocationListener.timestampWifi = loc.getTime();
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