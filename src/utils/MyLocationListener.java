package utils;

import logs.LogTag;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * This is a location listener which return the location of your phone through
 * the GPS or Network
 * 
 * @author hti
 * 
 */
public class MyLocationListener implements LocationListener {

	/** GPS latitude */
	public static double latitudeGPS;

	/** GPS longitude */
	public static double longitudeGPS;

	/** Network latitude */
	public static double latitudeWifi;

	/** Network longitude */
	public static double longitudeWifi;

	/** The provider (GPS OR NETWORK) */
	private String provider;

	/**
	 * Default constructor
	 * 
	 * @param prov
	 */
	public MyLocationListener(String prov) {
		this.provider = prov;
	}

	@Override
	public void onLocationChanged(Location loc) {
		Log.i(LogTag.LOCATIONLISTENER,
				loc.getLatitude() + " ---- " + loc.getLongitude());
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
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * Get latitude given by GPS
	 * 
	 * @return double
	 */
	public static double getLatitudeGPS() {
		return latitudeGPS;
	}

	/**
	 * Get longitude given by GPS
	 * 
	 * @return double
	 */
	public static double getLongitudeGPS() {
		return longitudeGPS;
	}

	/**
	 * Get latitude given by Network
	 * 
	 * @return double
	 */
	public static double getLatitudeWifi() {
		return latitudeWifi;
	}

	/**
	 * Get longitude given by Network
	 * 
	 * @return double
	 */
	public static double getLongitudeWifi() {
		return longitudeWifi;
	}

}