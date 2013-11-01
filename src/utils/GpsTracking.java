package utils;

import java.util.Vector;

import logs.LogTag;
import model.WaypointDB;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.hti.LoginActivity;
import com.hti.MainActivity;
import com.mongodb.BasicDBObject;

/**
 * This class provides some methods to track the user when he is running
 * @author hti
 *
 */
public class GpsTracking {
	
	/** To know if the tracking is started */
	public static boolean isStart = false;

	/** Frequency of request */
	private int mInterval = 3000;
	
	/** Handler */
	private Handler mHandler;
	
	/** Vector to store GPS waypoints */
	private Vector<BasicDBObject> mGpsWayPoints;
	
	/** Vector to store Internet waypoints */
	private Vector<BasicDBObject> mWifiWayPoints;
	
	/** The location manager for GPS */
	private static LocationManager mlocManagerGPS;
	
	/** The location listener for GPS */
	private static LocationListener mlocListenerGPS;
	
	/** The location manager for internet */
	private static LocationManager mlocManagerWifi;
	
	/** The locaton listener for internet */
	private static LocationListener mlocListenerWifi;
	
	/**
	 * Default Constructor
	 */
	public GpsTracking() {
		this.mGpsWayPoints = new Vector<BasicDBObject>();
		this.mWifiWayPoints = new Vector<BasicDBObject>();
		this.mHandler = new Handler();
	}
	
	/**
	 * Track the location of the phone through the GPS
	 */
	private void trackGPSLocation() {

		mlocManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				mInterval, 0, mlocListenerGPS);

		if (mlocManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (MyLocationListener.latitudeGPS > 0) {
				if (isStart) {
					WaypointDB lWp = new WaypointDB();
					lWp.append("waypointLat", MyLocationListener.latitudeGPS);
					lWp.append("waypointLng", MyLocationListener.longitudeGPS);
					mGpsWayPoints.add(lWp);
				}
			} else {
				Log.w(LogTag.GPS, "Position not found");
			}
		} else {
			Log.e(LogTag.GPS, "GPS OFF");
		}
		/** flush the buffer every MainActivity.BUFFERSIZE points */
		if (mGpsWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager
					.addRouteInJson(MainActivity.FILENAMEGPS, mGpsWayPoints,
							LoginActivity.getAppContext(), MainActivity.nbRoutes);
			Log.i(LogTag.WRITEFILEGPS, "Informations written in file");
		}
	}

	/**
	 * Track the location of the phone through the Network (wifi)
	 */
	private void trackWifiLocation() {

		mlocManagerWifi.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				mInterval, 0, mlocListenerWifi);

		if (mlocManagerWifi.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (MyLocationListener.latitudeWifi > 0) {
				if (isStart) {
					WaypointDB lWp = new WaypointDB();
					lWp.append("waypointLat", MyLocationListener.latitudeWifi);
					lWp.append("waypointLng", MyLocationListener.longitudeWifi);
					mWifiWayPoints.add(lWp);
				}
			} else {
				Log.w(LogTag.WIFI, "Position not found");
			}
		} else {
			Log.e(LogTag.WIFI, "NETWORK OFF");
		}
		/** flush the buffer every MainActivity.BUFFERSIZE points */
		if (mWifiWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager.addRouteInJson(MainActivity.FILENAMEWIFI,
					mWifiWayPoints, LoginActivity.getAppContext().getApplicationContext(),
					MainActivity.nbRoutes);
			Log.i(LogTag.WRITEFILEWIFI, "Informations written in file");
		}

	}
	
	/**
	 * Start the tracking
	 */
	public void startTracking() {
		isStart = true;
		mlocManagerGPS = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListenerGPS = new MyLocationListener(LocationManager.GPS_PROVIDER);
		mlocManagerWifi = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListenerWifi = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
	}
	
	/**
	 * Stop the tracking
	 */
	public void stopTracking() {
		isStart = false;
	}

	/** Periodic task */
	Runnable m_statusChecker;

	/**
	 * Start the periodic task
	 */
	public void startRepeatingTask() {
		/** initialize the handler of the timeout for the periodic tasks */
		m_statusChecker = new Runnable() {
			@Override
			public void run() {
				if(isStart) {
					trackGPSLocation();
					trackWifiLocation();
				}
					mHandler.postDelayed(m_statusChecker, mInterval);
			}
		};
		m_statusChecker.run();
	}
	
	/**
	 * Stop the periodic task
	 */
	public void stopRepeatingTask() {
		mHandler.removeCallbacks(m_statusChecker);
	}

}
