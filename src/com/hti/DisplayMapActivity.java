package com.hti;

import java.util.ArrayList;
import java.util.Formatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import model.Route;
import model.WaypointMAP;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * This is the activity in which the map will be displayed
 * 
 * @author hti
 */
public class DisplayMapActivity extends Activity {

	/** The google map in order to add some points to draw the route */
	private GoogleMap mMapGoogle;

	/** Waypoints for GPS listener */
	private ArrayList<WaypointMAP> mGpsWaypoints;

	/** Waypoints for Internet listener */
	private ArrayList<WaypointMAP> mWifiWaypoints;

	/** The route to display on the map */
	private Route mRouteToDisplay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displaymap);

		/** Initialize the map */
		try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		/** Get the route from the RideResultFragment */
		Intent lIntent = getIntent();
		mRouteToDisplay = lIntent
				.getParcelableExtra(MainActivity.ROUTE_TO_DISPLAY);
		mRouteToDisplay = RideResultFragment.mRouteInfos;

		/** Get the map */
		mMapGoogle = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapzebest)).getMap();

		/** The line composed by the waypoints */
		PolylineOptions lLineOptions = new PolylineOptions();

		/** boolean to check if we already set the 1st waypoints */
		boolean lStartPointIsSet = false;

		/** index & size to go through the list of waypoints */
		int lIndex, lSize;

		/** Enable MyLocation Button in the Map */
		mMapGoogle.setMyLocationEnabled(true);

		/** Get the waypoints of the route to display */
		try {
			mGpsWaypoints = (ArrayList<WaypointMAP>) mRouteToDisplay
					.getRoutePointsTemp();
		} catch (Exception e) {
		}
		try {
			mWifiWaypoints = (ArrayList<WaypointMAP>) mRouteToDisplay
					.getRoutePointsTemp();
		} catch (Exception e) {
		}

		/** Get the size of the list of waypoints */
		lSize = mGpsWaypoints == null ? (mWifiWaypoints == null ? 0
				: mWifiWaypoints.size()) : mGpsWaypoints.size();

		/** Create the route line */
		float lCumulatedDistance = 0;
		for (lIndex = 0; lIndex < lSize; lIndex++) {
			WaypointMAP lWpt = null;
			WaypointMAP lWptPrevious = null;
			/** Get the previous one to compute the distance between both */
			if(lIndex != 0) {
				if (mGpsWaypoints != null
						&& mGpsWaypoints.get(lIndex).getWaypointLat() != 0) {
					lWptPrevious = mGpsWaypoints.get(lIndex-1);
				} else {
					if (mWifiWaypoints != null
							&& mWifiWaypoints.get(lIndex).getWaypointLat() != 0) {
						lWptPrevious = mWifiWaypoints.get(lIndex-1);
					}
				} 
			}
			/** Get the current one */
			if (mGpsWaypoints != null
					&& mGpsWaypoints.get(lIndex).getWaypointLat() != 0) {
				lWpt = mGpsWaypoints.get(lIndex);
			} else {
				if (mWifiWaypoints != null
						&& mWifiWaypoints.get(lIndex).getWaypointLat() != 0) {
					lWpt = mWifiWaypoints.get(lIndex);
				}
			}
			/** Compute distance between both */
			if(lWpt != null && lWptPrevious != null) {
				float[] lDistance = new float[10];
				Location.distanceBetween(lWptPrevious.getWaypointLat(),
						lWptPrevious.getWaypointLng(),
						lWpt.getWaypointLat(),
						lWpt.getWaypointLng(),
						lDistance);
				lCumulatedDistance += lDistance[0];
			}
			if (lWpt != null) {
				/** Add the start marker & zoom on it */
				if (!lStartPointIsSet) {
					lStartPointIsSet = true;
					CameraPosition lCameraPosition = new CameraPosition.Builder()
							.target(new LatLng(lWpt.getWaypointLat(), lWpt
									.getWaypointLng())).zoom(17).tilt(30)
							.build();
					mMapGoogle.animateCamera(CameraUpdateFactory
							.newCameraPosition(lCameraPosition));
					mMapGoogle
							.addMarker(new MarkerOptions()
									.position(
											new LatLng(lWpt.getWaypointLat(),
													lWpt.getWaypointLng()))
									.title("Start")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				}
				lLineOptions.add(new LatLng(lWpt.getWaypointLat(), lWpt
						.getWaypointLng()));
			}
		}

		/** Add the end marker */
		WaypointMAP lWpt = null;
		while (lWpt == null && lIndex >= 0) {
			lIndex--;
			if (mGpsWaypoints != null
					&& mGpsWaypoints.get(lIndex).getWaypointLat() != 0) {
				lWpt = mGpsWaypoints.get(lIndex);
			} else {
				if (mWifiWaypoints != null
						&& mWifiWaypoints.get(lIndex).getWaypointLat() != 0) {
					lWpt = mWifiWaypoints.get(lIndex);
				}
			}
		}
		if (lWpt != null) {
			mMapGoogle.addMarker(new MarkerOptions()
					.position(
							new LatLng(lWpt.getWaypointLat(), lWpt
									.getWaypointLng()))
					.title("Finish: " + String.valueOf((new Formatter().format("%.2f", lCumulatedDistance/1000.))) + " km.")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		/** Trace the route */
		lLineOptions.width(2).color(Color.RED);
		mMapGoogle.addPolyline(lLineOptions);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mMapGoogle.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mMapGoogle.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 * Inflate the menu; this adds items to the action bar if it is present.
		 */
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /** Handle item selection */
	    switch (item.getItemId()) {
	        case R.id.action_help:
	            displayHelpView();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void displayHelpView() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.help_text_title);
		builder.setIcon(R.drawable.about_icon);
        builder.setMessage(R.string.help_text).create().show();	
	}

}
