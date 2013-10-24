package utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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
		Log.i("GPS", loc.getLatitude() + " ---- " + loc.getLongitude());
		if (this.provider.equals(LocationManager.GPS_PROVIDER)) {
			MyLocationListener.latitudeGPS = loc.getLatitude();
			MyLocationListener.longitudeGPS = loc.getLongitude();
		} else {
			MyLocationListener.latitudeWifi = loc.getLatitude();
			MyLocationListener.longitudeWifi = loc.getLongitude();
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

	public static double getLatitudeGPS() {
		return latitudeGPS;
	}

	public static double getLongitudeGPS() {
		return longitudeGPS;
	}

	public static double getLatitudeWifi() {
		return latitudeWifi;
	}

	public static double getLongitudeWifi() {
		return longitudeWifi;
	}
	
	
}