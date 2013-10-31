package utils;

import java.util.Vector;

import model.Waypoint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.hti.LoginActivity;
import com.hti.MainActivity;
import com.mongodb.BasicDBObject;

public class GpsTracking {
	
	public static boolean isStart = false;

	private int m_interval = 3000; // 5 seconds by default, can be changed later
	private Handler m_handler;
	private Vector<BasicDBObject> gpsWayPoints;
	private Vector<BasicDBObject> wifiWayPoints;
	private static LocationManager mlocManagerGPS;
	private static LocationListener mlocListenerGPS;
	private static LocationManager mlocManagerWifi;
	private static LocationListener mlocListenerWifi;
	
	public GpsTracking() {
		this.gpsWayPoints = new Vector<BasicDBObject>();
		this.wifiWayPoints = new Vector<BasicDBObject>();
		this.m_handler = new Handler();
	}
	
	/**
	 * Track the location of the phone through the GPS
	 */
	private void trackGPSLocation() {

		/*
		 * put that before otherwise the first click is initialising the
		 * location
		 */

		// every 5 seconds
		mlocManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				m_interval, 0, mlocListenerGPS);
		/* */
		if (mlocManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (MyLocationListener.latitudeGPS > 0) {
				if (isStart) {
					Waypoint wp = new Waypoint();
					wp.append("waypointLat", MyLocationListener.latitudeGPS);
					wp.append("waypointLng", MyLocationListener.longitudeGPS);
					gpsWayPoints.add(wp);
				}
			} else {
				Log.w("GPS", "Position non trouvée");
			}
		} else {
			Log.e("GPS", "GPS OFF");
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (gpsWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager
					.addRouteInJson(MainActivity.FILENAMEGPS, gpsWayPoints,
							LoginActivity.getAppContext(), MainActivity.nbRoutes);
			Log.i("WRITEFILEGPS", "Informations written in file");
		}
	}

	/**
	 * Track the location of the phone through the Network (wifi)
	 */
	private void trackWifiLocation() {

		/*
		 * put that before otherwise the first click is initialising the
		 * location
		 */
		
		mlocManagerWifi.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				m_interval, 0, mlocListenerWifi);
		/* */
		if (mlocManagerWifi.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (MyLocationListener.latitudeWifi > 0) {
				if (isStart) {
					Waypoint wp = new Waypoint();
					wp.append("waypointLat", MyLocationListener.latitudeWifi);
					wp.append("waypointLng", MyLocationListener.longitudeWifi);
					wifiWayPoints.add(wp);
				}

			} else {
				Log.w("WIFI", "Position non trouvée");
			}
		} else {
			Log.e("WIFI", "NETWORK OFF");
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (wifiWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager.addRouteInJson(MainActivity.FILENAMEWIFI,
					wifiWayPoints, LoginActivity.getAppContext().getApplicationContext(),
					MainActivity.nbRoutes);
			Log.i("WRITEFILEWIFI", "Informations written in file");
		}

	}
	
	public void startTracking() {
		this.isStart = true;
		mlocManagerGPS = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListenerGPS = new MyLocationListener(LocationManager.GPS_PROVIDER);
		mlocManagerWifi = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListenerWifi = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
	}
	
	public void stopTracking() {
		this.isStart = false;
	}

	/*
	 * Periodic task
	 */
	Runnable m_statusChecker;

	public void startRepeatingTask() {
		// initialize the handler of the timeout for the periodic tasks
		m_statusChecker = new Runnable() {
			@Override
			public void run() {
					trackGPSLocation();
					trackWifiLocation();
					m_handler.postDelayed(m_statusChecker, m_interval);
			}
			
		};
		m_statusChecker.run();
	}
	
	public void stopRepeatingTask() {
		m_handler.removeCallbacks(m_statusChecker);
	}

}
