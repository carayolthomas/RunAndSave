package com.example.poc_gps;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static int nbRides = 0;
	public final static String RIDE_NUMBER_MAP = "rideNumberToMap";
	
	private static boolean isStart = false;
	public static final String FILENAMEGPS = "cacheGPS.json";
	public static final String FILENAMEWIFI = "cacheWifi.json";
	private int m_interval = 5000; // 5 seconds by default, can be changed later
	private Handler m_handler;
	private ArrayList<WayPoint> gpsWayPoints;
	private ArrayList<WayPoint> wifiWayPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_handler = new Handler();
		this.cleanGPSCacheListener();
		this.cleanWifiCacheListener();
		this.addDisplayCacheListener();
		this.addRecordLocationListener();

		findViewById(R.id.displayMap).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						startMapActivity();
					}
				});

	}

	private void startMapActivity() {
		Intent intent = new Intent(this, DisplayMapActivity.class);
		intent.putExtra(MainActivity.RIDE_NUMBER_MAP, 0);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	/*
	 * Cache tasks
	 */
	private void addDisplayCacheListener() {
		findViewById(R.id.displayGPSCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						displayCache(FILENAMEGPS);
					}
				});

		findViewById(R.id.displayWifiCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						displayCache(FILENAMEWIFI);
					}
				});
	}
	
	private void cleanGPSCacheListener() {
		findViewById(R.id.cleanGPSCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEGPS);
					}
				});

	}

	private void cleanWifiCacheListener() {
		findViewById(R.id.cleanWifiCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEWIFI);
					}
				});
	}

	private void displayCache(String cacheFilename) {
		if (!isStart) {
			try {
				String str = "";
				Reader reader = JsonManager.openReader(cacheFilename, this);
				SearchJSONResult ridesSearchResult = JsonManager.getJSON(cacheFilename,
						reader, cacheFilename.equalsIgnoreCase(MainActivity.FILENAMEGPS) ? this.gpsWayPoints : this.wifiWayPoints);
				List<Ride> rides = ridesSearchResult.rides;
				if (rides != null && rides.size() > 0) {

					for (int i = 0; i < rides.size(); i++) {
						Ride ride = rides.get(i);
						str += "Ride : " + ride.rideNumber + "\n";

						for (int j = 0; j < ride.wayPoints.size(); j++) {
							str += "\t" + ride.wayPoints.get(j).lat + ";"
									+ ride.wayPoints.get(j).lng + " at "
									+ new Date(ride.wayPoints.get(j).timestamp)
									+ "\n";
						}
						str += "----\n";
					}
				}
				reader.close();
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);
				alert.setTitle("Cache");
				alert.setMessage(str);
				alert.setPositiveButton("OK", null);
				alert.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(getApplicationContext(), "Stop recording first!",
					Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * Location tasks
	 */
	private void addRecordLocationListener() {
		findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (isStart) {
					// flush the buffered waypoints
					JsonManager.JSONToCache(MainActivity.FILENAMEGPS, gpsWayPoints, getApplicationContext());
					JsonManager.JSONToCache(MainActivity.FILENAMEWIFI, wifiWayPoints, getApplicationContext());
					// change message
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Start recording position when click");
					MainActivity.isStart = false;
					stopRepeatingTask();
					MainActivity.nbRides++;
				} else {
					gpsWayPoints = new ArrayList<WayPoint>();
					wifiWayPoints = new ArrayList<WayPoint>();
					MainActivity.isStart = true;
					startRepeatingTask();
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Stop recording position when click");
				}
			}
		});
	}

	private void addGPSLocation() {

		/*
		 * put that before otherwise the first click is initialising the
		 * location
		 */
		LocationManager mlocManager = null;
		LocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.GPS_PROVIDER);
		// every 5 seconds
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
				0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (MyLocationListener.latitudeGPS > 0) {
				if (isStart) {
					gpsWayPoints.add(new WayPoint(
							MyLocationListener.latitudeGPS,
							MyLocationListener.longitudeGPS,
							MyLocationListener.location.getTime()));
				}

			} else {
				gpsWayPoints
						.add(new WayPoint(null, null, new Date().getTime()));
			}
		} else {
			gpsWayPoints.add(new WayPoint(null, null, new Date().getTime()));
		}
		if (gpsWayPoints.size() > 100) {
			JsonManager.JSONToCache(MainActivity.FILENAMEGPS, this.gpsWayPoints, this);
		}
	}

	private void addWifiLocation() {

		/*
		 * put that before otherwise the first click is initialising the
		 * location
		 */
		LocationManager mlocManager = null;
		LocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				5000, 0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (MyLocationListener.latitudeWifi > 0) {
				if (isStart) {
					wifiWayPoints.add(new WayPoint(
							MyLocationListener.latitudeWifi,
							MyLocationListener.longitudeWifi,
							MyLocationListener.location.getTime()));
				}

			} else {
				wifiWayPoints
						.add(new WayPoint(null, null, new Date().getTime()));
			}
		} else {
			wifiWayPoints.add(new WayPoint(null, null, new Date().getTime()));
		}
		if (wifiWayPoints.size() > 100) {
			JsonManager.JSONToCache(MainActivity.FILENAMEWIFI, this.wifiWayPoints, this);
		}

	}

	

	/*
	 * Periodic task
	 */
	Runnable m_statusChecker = new Runnable() {
		@Override
		public void run() {
			addGPSLocation();
			addWifiLocation();
			m_handler.postDelayed(m_statusChecker, m_interval);
		}
	};

	void startRepeatingTask() {
		m_statusChecker = new Runnable() {
			@Override
			public void run() {
				addGPSLocation();
				addWifiLocation();
				m_handler.postDelayed(m_statusChecker, m_interval);
			}
		};
		m_statusChecker.run();
	}

	void stopRepeatingTask() {
		m_handler.removeCallbacks(m_statusChecker);
	}

}
