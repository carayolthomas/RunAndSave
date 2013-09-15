package com.example.poc_gps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static boolean isStart = false;
	private static FileOutputStream fos;
	private static String FILENAME = "testCaching";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.addGPSLocationListener();
		this.addWifiLocationListener();
		this.addDisplayCacheListener();
		this.addRecordLocationListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	private void addDisplayCacheListener() {
		findViewById(R.id.display).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						if (!isStart) {
							FileInputStream fis;
							try {

								fis = openFileInput(FILENAME);

								String str = "";
								byte[] buffer = new byte[256];
								int nbOctet = fis.read(buffer);

								while (nbOctet != -1) {
									str += new String(buffer);
									buffer = new byte[256];
									nbOctet = fis.read(buffer);
								}
								AlertDialog.Builder alert = new AlertDialog.Builder(
										MainActivity.this);
								alert.setTitle("Cache");
								alert.setMessage(str);
								alert.setPositiveButton("OK", null);
								alert.show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Toast.makeText(getApplicationContext(),
									"Stop recording first!", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
	}

	private void addRecordLocationListener() {
		findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (isStart) {
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Start recording position when click");
					isStart = false;
				} else {
					try {
						fos = openFileOutput(FILENAME, Context.MODE_APPEND);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isStart = true;
					Button mButton = (Button) findViewById(R.id.cache);
					mButton.setText("Stop recording position when click");
				}
			}
		});
	}

	private void addGPSLocationListener() {
		findViewById(R.id.gpsPosition).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						LocationManager mlocManager = null;
						LocationListener mlocListener;
						mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						mlocListener = new MyLocationListener(
								LocationManager.GPS_PROVIDER);
						// every 5 seconds
						mlocManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 5000, 0,
								mlocListener);

						if (mlocManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							if (MyLocationListener.latitudeGPS > 0) {
								if (isStart) {
									try {
										fos.write(("GPS: "
												+ MyLocationListener.latitudeGPS
												+ ","
												+ MyLocationListener.longitudeGPS
												+ " - "
												+ MyLocationListener.location
														.getTime() + "\n")
												.getBytes());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								Toast.makeText(
										getApplicationContext(),
										"Latitude GPS:- "
												+ MyLocationListener.latitudeGPS
												+ "\nLongitude GPS:- "
												+ MyLocationListener.longitudeGPS,
										Toast.LENGTH_LONG).show();
							} else {
								AlertDialog.Builder alert = new AlertDialog.Builder(
										MainActivity.this);
								alert.setTitle("Wait");
								alert.setMessage("GPS in progress, please wait.");
								alert.setPositiveButton("OK", null);
								alert.show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"GPS is not turned on...",
									Toast.LENGTH_LONG).show();
						}

					}
				});
	}

	private void addWifiLocationListener() {
		findViewById(R.id.wifiPosition).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						LocationManager mlocManager = null;
						LocationListener mlocListener;
						mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						mlocListener = new MyLocationListener(
								LocationManager.NETWORK_PROVIDER);
						mlocManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 5000, 0,
								mlocListener);

						if (mlocManager
								.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
							if (MyLocationListener.latitudeWifi > 0) {
								if (isStart) {
									try {
										fos.write(("Wifi: "
												+ MyLocationListener.latitudeWifi
												+ ","
												+ MyLocationListener.longitudeWifi
												+ " - "
												+ MyLocationListener.location
														.getTime() + "\n")
												.getBytes());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								Toast.makeText(
										getApplicationContext(),
										"Latitude Wifi:- "
												+ MyLocationListener.latitudeWifi
												+ "\nLongitude Wifi:- "
												+ MyLocationListener.longitudeWifi,
										Toast.LENGTH_LONG).show();
							} else {
								AlertDialog.Builder alert = new AlertDialog.Builder(
										MainActivity.this);
								alert.setTitle("Wait");
								alert.setMessage("Wifi in progress, please wait.");
								alert.setPositiveButton("OK", null);
								alert.show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"Wifi is not turned on...",
									Toast.LENGTH_LONG).show();
						}

					}
				});
	}

}
