package com.example.poc_gps;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int BUFFERSIZE = 10;
	public final static String RIDE_NUMBER_MAP = "rideNumberToMap";
	public static final String FILENAMEGPS = "cacheGPS.json";
	public static final String FILENAMEWIFI = "cacheWifi.json";

	public static Integer nbRides = null;
	private static boolean isStart = false;

	private int m_interval = 5000; // 5 seconds by default, can be changed later
	private Handler m_handler;
	private Vector<WayPoint> gpsWayPoints;
	private Vector<WayPoint> wifiWayPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nbRides = JsonManager.getNumberOfRides(this);
		nbRides = nbRides == 0 ? 0 : nbRides + 1;
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
		String rideToDisplay = ((EditText) findViewById(R.id.rideToDisplay))
				.getText().toString();
		intent.putExtra(MainActivity.RIDE_NUMBER_MAP, rideToDisplay);
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
	/**
	 * Add the listener on the buttons 'displayGPSCache' and 'displayWifiCache'
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

	/**
	 * Add the listener on the button 'cleanGPSCache'
	 */
	private void cleanGPSCacheListener() {
		findViewById(R.id.cleanGPSCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEGPS);
					}
				});

	}

	/**
	 * Add the listener on the button 'cleanWifiCache'
	 */
	private void cleanWifiCacheListener() {
		findViewById(R.id.cleanWifiCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEWIFI);
					}
				});
	}

	/**
	 * Display the content of a file in cache
	 * 
	 * @param cacheFilename
	 *            the file in cache to display
	 */
	private void displayCache(String cacheFilename) {
		// Dsiplay the cache only if the tracking is off
		if (!isStart) {
			try {
				String content = "";
				Reader reader = JsonManager.openReader(cacheFilename, this);
				SearchJSONResult ridesSearchResult = JsonManager
						.getAllRides(reader);
				List<Ride> rides = ridesSearchResult.rides;

				// get the content
				if (rides != null && rides.size() > 0) {

					for (int i = 0; i < rides.size(); i++) {
						Ride ride = rides.get(i);
						content += "Ride : " + ride.rideNumber + "\n";

						for (int j = 0; j < ride.wayPoints.size(); j++) {
							content += "\t" + ride.wayPoints.get(j).lat + ";"
									+ ride.wayPoints.get(j).lng + " at "
									+ new Date(ride.wayPoints.get(j).timestamp)
									+ "\n";
						}
						content += "----\n";
					}
				} else {
					content = "No ride.";
				}
				reader.close();
				// Display the content
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);
				alert.setTitle("Cache");
				alert.setMessage(content);
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
	/**
	 * Add a listener on the button 'cache' which enable the tracking
	 */
	private void addRecordLocationListener() {
		findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (isStart) {
					// flush the buffered waypoints
					JsonManager.addWaypointsToRide(MainActivity.FILENAMEGPS,
							gpsWayPoints, getApplicationContext(),
							MainActivity.nbRides);
					JsonManager.addWaypointsToRide(MainActivity.FILENAMEWIFI,
							wifiWayPoints, getApplicationContext(),
							MainActivity.nbRides);
					// change message
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Start recording position when click");
					MainActivity.isStart = false;
					MainActivity.nbRides++;
					stopRepeatingTask();
				} else {
					try {
						m_interval = Integer
								.parseInt(((EditText) findViewById(R.id.sampleTime))
										.getText().toString()) * 1000;
					} catch (Exception e) {
						m_interval = 5000;
					}

					gpsWayPoints = new Vector<WayPoint>();
					wifiWayPoints = new Vector<WayPoint>();
					MainActivity.isStart = true;
					startRepeatingTask();
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Stop recording position when click");
				}
			}
		});
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
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.GPS_PROVIDER);
		// every 5 seconds
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				m_interval, 0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (MyLocationListener.latitudeGPS > 0) {
				if (isStart) {
					gpsWayPoints.add(new WayPoint(
							MyLocationListener.latitudeGPS,
							MyLocationListener.longitudeGPS,
							MyLocationListener.timestampGPS));
				}

			} else {
				gpsWayPoints
						.add(new WayPoint(null, null, new Date().getTime()));
			}
		} else {
			gpsWayPoints.add(new WayPoint(null, null, new Date().getTime()));
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (gpsWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager
					.addWaypointsToRide(MainActivity.FILENAMEGPS, gpsWayPoints,
							getApplicationContext(), MainActivity.nbRides);
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
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				m_interval, 0, mlocListener);
		/* */
		if (mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			if (MyLocationListener.latitudeWifi > 0) {
				if (isStart) {
					wifiWayPoints.add(new WayPoint(
							MyLocationListener.latitudeWifi,
							MyLocationListener.longitudeWifi,
							MyLocationListener.timestampWifi));
				}

			} else {
				wifiWayPoints
						.add(new WayPoint(null, null, new Date().getTime()));
			}
		} else {
			wifiWayPoints.add(new WayPoint(null, null, new Date().getTime()));
		}
		// flush the buffer every MainActivity.BUFFERSIZE points
		if (wifiWayPoints.size() > MainActivity.BUFFERSIZE) {
			JsonManager.addWaypointsToRide(MainActivity.FILENAMEWIFI,
					wifiWayPoints, getApplicationContext(),
					MainActivity.nbRides);
		}

	}

	/*
	 * Periodic task
	 */
	Runnable m_statusChecker;

	void startRepeatingTask() {
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

	void stopRepeatingTask() {
		m_handler.removeCallbacks(m_statusChecker);
	}

}
