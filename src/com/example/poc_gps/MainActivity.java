package com.example.poc_gps;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.gpsPosition).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						LocationManager mlocManager = null;
						LocationListener mlocListener;
						mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						mlocListener = new MyLocationListener(LocationManager.GPS_PROVIDER);
						//every 5 seconds
						mlocManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 5000, 0,
								mlocListener);

						if (mlocManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							if (MyLocationListener.latitudeGPS > 0) {
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
		
		findViewById(R.id.wifiPosition).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						LocationManager mlocManager = null;
						LocationListener mlocListener;
						mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						mlocListener = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
						mlocManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 5000, 0,
								mlocListener);

						if (mlocManager
								.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
							if (MyLocationListener.latitudeWifi > 0) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

}
