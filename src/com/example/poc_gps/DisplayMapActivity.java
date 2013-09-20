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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
		// get the rides in cache
		SearchJSONResult gpsResults = JsonManager.getAllRides(readerGPS);
		SearchJSONResult wifiResults = JsonManager.getAllRides(readerWifi);
		List<WayPoint> gpsWaypoints = null;
		List<WayPoint> wifiWaypoints = null;
		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		//The line composed by the waypoints
		PolylineOptions lineOptions = new PolylineOptions();
		//to check if we already set the 1st waypoints
		boolean startPointIsSet = false;
		int i, size;
		
		// Initializing
		markerPoints = new ArrayList<LatLng>();

		// Getting Map for the SupportMapFragment
		map = fm.getMap();

		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);

		//Get a the waypoints of the ride to display
		try {
			gpsWaypoints = gpsResults.rides.get(rideNumber).wayPoints;
		} catch (Exception e) {
		}
		try {
			wifiWaypoints = wifiResults.rides.get(rideNumber).wayPoints;
		} catch (Exception e) {
		}

		size = gpsWaypoints == null ? (wifiWaypoints == null ? 0
				: wifiWaypoints.size()) : gpsWaypoints.size();
		// create the route line
		for (i = 0; i < size; i++) {
			WayPoint wpt = null;
			if (gpsWaypoints != null && gpsWaypoints.get(i).lat != null) {
				wpt = gpsWaypoints.get(i);

			} else {
				if (wifiWaypoints != null && wifiWaypoints.get(i).lat != null) {
					wpt = wifiWaypoints.get(i);
				}
			}
			if (wpt != null) {
				// add the start marker
				if (!startPointIsSet) {
					startPointIsSet = true;
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(wpt.lat, wpt.lng)) // Sets the
																	// center of
																	// the map
																	// to the
																	// start
																	// point
							.zoom(17) // Sets the zoom
							.tilt(30) // Sets the tilt of the camera to 30
										// degrees
							.build(); // Creates a CameraPosition from the
										// builder
					map.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
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
		while (wpt == null && i >= 0) {
			i--;
			if (gpsWaypoints != null && gpsWaypoints.get(i).lat != null) {
				wpt = gpsWaypoints.get(i);

			} else {
				if (wifiWaypoints != null && wifiWaypoints.get(i).lat != null) {
					wpt = wifiWaypoints.get(i);
				}
			}
		}
		if (wpt != null) {
			map.addMarker(new MarkerOptions()
					.position(new LatLng(wpt.lat, wpt.lng))
					.title("Finish: " + new Date(wpt.timestamp))
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
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
