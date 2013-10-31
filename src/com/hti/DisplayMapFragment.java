package com.hti;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import model.Waypoint;
import model.Route;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.BasicDBObject;

public class DisplayMapFragment extends Fragment {
	
	private GoogleMap mapGoogle;
	private MapView mapView;
	private ArrayList<BasicDBObject> gpsWaypoints;
	private ArrayList<BasicDBObject> wifiWaypoints;
	private Route routeToDisplay;
	
	private View mView;
	private Bundle mSavedInstanceState;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			MapsInitializer.initialize(LoginActivity.getAppContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_map_display, container,
				false);

		mapView = (MapView) view.findViewById(R.id.map_view);
		mapView.onCreate(savedInstanceState);
		this.mView = view;
		this.mSavedInstanceState = savedInstanceState;

		/*
		 * //Get the route to display Bundle bundle = getArguments();
		 * routeToDisplay =
		 * bundle.getParcelable(RideResultFragment.EXTRA_ROUTE);
		 */

		return view;
	}
    
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
        	switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoginActivity.getAppContext())) {
        		case ConnectionResult.SERVICE_MISSING :
        			Log.i("GooglePlayService", "ConnectionResult.SERVICE_MISSING");
        			break;
        		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED :
        			Log.i("GooglePlayService", "ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED");
        			break;
        		case ConnectionResult.SERVICE_DISABLED :
        			Log.i("GooglePlayService", "ConnectionResult.SERVICE_DISABLED");
        			break;
        		default :
        			Log.i("CA", "VA");
        	}
            //zeFonction();
        }
    }
    
    public void zeFonction() {
		try {
			MapsInitializer.initialize(this.getActivity()
					.getApplicationContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		routeToDisplay = RideResultFragment.routeInfos;
		Log.i("CHECK ARGS", "" + routeToDisplay.getRouteId());

		mapView = (MapView) mView.findViewById(R.id.map_view);
		mapView.onCreate(mSavedInstanceState);
		mapGoogle = ((MapView) mapView.findViewById(R.id.map_view)).getMap();
		/*// The line composed by the waypoints
		PolylineOptions lineOptions = new PolylineOptions();
		// to check if we already set the 1st waypoints
		boolean startPointIsSet = false;
		int i, size;

		// Getting Map for the SupportMapFragment

		// Enable MyLocation Button in the Map
		mapGoogle.setMyLocationEnabled(true);

		// Get a the waypoints of the ride to display
		try {
			gpsWaypoints = (ArrayList<BasicDBObject>) routeToDisplay
					.getRoutePoints();
		} catch (Exception e) {
		}
		try {
			wifiWaypoints = (ArrayList<BasicDBObject>) routeToDisplay
					.getRoutePoints();
		} catch (Exception e) {
		}

		size = gpsWaypoints == null ? (wifiWaypoints == null ? 0
				: wifiWaypoints.size()) : gpsWaypoints.size();
		// create the route line
		Iterator<BasicDBObject> iteWaypoints = gpsWaypoints.iterator();
		if (iteWaypoints.hasNext()) {
			System.out.println(iteWaypoints.next().get("waypointLat")
					.toString());
		}
		for (i = 0; i < size; i++) {
			Waypoint wpt = null;
			if (gpsWaypoints != null
					&& Double.parseDouble(gpsWaypoints.get(i)
							.get("waypointLat").toString()) != 0) {
				wpt = new Waypoint(Double.parseDouble(gpsWaypoints.get(i)
						.get("waypointLat").toString()),
						Double.parseDouble(gpsWaypoints.get(i)
								.get("waypointLng").toString()));
			} else {
				if (wifiWaypoints != null
						&& Double.parseDouble(wifiWaypoints.get(i)
								.get("waypointLat").toString()) != 0) {
					wpt = new Waypoint(Double.parseDouble(wifiWaypoints.get(i)
							.get("waypointLat").toString()),
							Double.parseDouble(wifiWaypoints.get(i)
									.get("waypointLng").toString()));
				}
			}
			if (wpt != null) {
				// add the start marker
				if (!startPointIsSet) {
					startPointIsSet = true;
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(wpt.getWaypointLat(), wpt
									.getWaypointLng())) // Sets the
							.zoom(17) // Sets the zoom
							.tilt(30) // Sets the tilt of the camera to 30
										// degrees
							.build(); // Creates a CameraPosition from the
										// builder
					mapGoogle.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
					mapGoogle
							.addMarker(new MarkerOptions()
									.position(
											new LatLng(wpt.getWaypointLat(),
													wpt.getWaypointLng()))
									.title("Start")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				}
				lineOptions.add(new LatLng(wpt.getWaypointLat(), wpt
						.getWaypointLng()));
			}
		}

		// add the end marker
		Waypoint wpt = null;
		while (wpt == null && i >= 0) {
			i--;
			if (gpsWaypoints != null
					&& Double.parseDouble(gpsWaypoints.get(i)
							.get("waypointLat").toString()) != 0) {
				wpt = new Waypoint(Double.parseDouble(gpsWaypoints.get(i)
						.get("waypointLat").toString()),
						Double.parseDouble(gpsWaypoints.get(i)
								.get("waypointLng").toString()));
			} else {
				if (wifiWaypoints != null
						&& Double.parseDouble(wifiWaypoints.get(i)
								.get("waypointLat").toString()) != 0) {
					wpt = new Waypoint(Double.parseDouble(wifiWaypoints.get(i)
							.get("waypointLat").toString()),
							Double.parseDouble(wifiWaypoints.get(i)
									.get("waypointLng").toString()));
				}
			}
		}
		if (wpt != null) {
			mapGoogle.addMarker(new MarkerOptions()
					.position(
							new LatLng(wpt.getWaypointLat(), wpt
									.getWaypointLng()))
					.title("Finish")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		// Trace the route
		lineOptions.width(2).color(Color.RED);
		mapGoogle.addPolyline(lineOptions);*/
	}

}
