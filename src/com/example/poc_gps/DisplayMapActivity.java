package com.example.poc_gps;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DisplayMapActivity extends FragmentActivity {

	GoogleMap map;
	ArrayList<LatLng> markerPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_layout);

		Intent intent = getIntent();
		int rideNumber = Integer.parseInt(intent
				.getStringExtra(MainActivity.RIDE_NUMBER_MAP));
		// Open cache files
		Reader readerGPS = JsonManager.openReader(MainActivity.FILENAMEGPS,
				this);
		Reader readerWifi = JsonManager.openReader(MainActivity.FILENAMEWIFI,
				this);
		// Initializing
		markerPoints = new ArrayList<LatLng>();

		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting Map for the SupportMapFragment
		map = fm.getMap();

		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);

		SearchJSONResult gpsResults = JsonManager.getAllRides(
				MainActivity.FILENAMEGPS, readerGPS);
		SearchJSONResult wifiResults = JsonManager.getAllRides(
				MainActivity.FILENAMEWIFI, readerWifi);

		List<WayPoint> gpsWaypoints = gpsResults.rides.get(rideNumber).wayPoints;
		List<WayPoint> wifiWaypoints = wifiResults.rides.get(rideNumber).wayPoints;

		PolylineOptions lineOptions = new PolylineOptions();
		boolean startPointIsSet = false;
		int i;
		// create the route line
		for (i = 0; i < gpsWaypoints.size(); i++) {
			WayPoint wpt = null;
			if (gpsWaypoints.get(i).lat != null) {
				wpt = gpsWaypoints.get(i);

			} else {
				if (wifiWaypoints.get(i).lat != null) {
					wpt = wifiWaypoints.get(i);
				}
			}
			if (wpt != null) {
				// add the start marker
				if (!startPointIsSet) {
					startPointIsSet = true;
					map.addMarker(new MarkerOptions()
							.position(new LatLng(wpt.lat, wpt.lng))
							.title("Start: " + new Date(wpt.timestamp))
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				}
				lineOptions.add(new LatLng(wpt.lat, wpt.lng));
			}
		}

		// add the end marker
		WayPoint wpt = null;
		while (wpt == null) {
			i--;
			if (gpsWaypoints.get(i).lat != null) {
				wpt = gpsWaypoints.get(i);

			} else {
				if (wifiWaypoints.get(i).lat != null) {
					wpt = wifiWaypoints.get(i);
				}
			}
		}
		map.addMarker(new MarkerOptions()
				.position(new LatLng(wpt.lat, wpt.lng))
				.title("Finish: " + new Date(wpt.timestamp))
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		// Trace the route
		lineOptions.width(2).color(Color.RED);
		map.addPolyline(lineOptions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
