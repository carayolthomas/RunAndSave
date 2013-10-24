package utils;

import java.util.Vector;

import model.Waypoint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import com.hti.LoginActivity;
import com.hti.MainActivity;

public class GpsTracking {
	
	private static boolean isStart = false;

	private int m_interval = 5000; // 5 seconds by default, can be changed later
	private Handler m_handler;
	private Vector<Waypoint> gpsWayPoints;
	private Vector<Waypoint> wifiWayPoints;
	private Context gpsTrackingContext;
	
	public GpsTracking(Context pGpsTrackingContext) {
		this.gpsTrackingContext = pGpsTrackingContext;
		this.gpsWayPoints = new Vector<Waypoint>();
		this.wifiWayPoints = new Vector<Waypoint>();
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
		LocationManager mlocManager = null;
		LocationListener mlocListener;
		mlocManager = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.GPS_PROVIDER);
		// every 5 seconds
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				m_interval, 0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (MyLocationListener.latitudeGPS > 0) {
				if (isStart) {
					gpsWayPoints.add(new Waypoint(
							MyLocationListener.latitudeGPS,
							MyLocationListener.longitudeGPS));
				}
			} else {
				gpsWayPoints.add(new Waypoint(0., 0.));
			}
		} else {
			gpsWayPoints.add(new Waypoint(0., 0.));
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (gpsWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager
					.addRouteInJson(MainActivity.FILENAMEGPS, gpsWayPoints,
							this.gpsTrackingContext.getApplicationContext(), MainActivity.nbRoutes);
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
		LocationManager mlocManager = null;
		LocationListener mlocListener;
		mlocManager = (LocationManager) LoginActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				m_interval, 0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (MyLocationListener.latitudeWifi > 0) {
				if (isStart) {
					wifiWayPoints.add(new Waypoint(
							MyLocationListener.latitudeWifi,
							MyLocationListener.longitudeWifi));
				}

			} else {
				wifiWayPoints
						.add(new Waypoint(0., 0.));
			}
		} else {
			wifiWayPoints.add(new Waypoint(0., 0.));
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (wifiWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager.addRouteInJson(MainActivity.FILENAMEWIFI,
					wifiWayPoints, this.gpsTrackingContext.getApplicationContext(),
					MainActivity.nbRoutes);
		}

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
