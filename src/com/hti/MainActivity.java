package com.hti;

import java.util.Date;
import java.util.Locale;

import logs.LogTag;
import model.Ride;
import model.Route;
import model.User;
import utils.GpsTracking;
import utils.HTIDatabaseConnection;
import utils.JsonManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	public static final int BUFFERSIZE = 10;
	public final static String RIDE_NUMBER_MAP = "rideNumberToMap";
	public final static String ROUTE_TO_DISPLAY = "routeToDisplay";
	/*
	 * Ces deux fichiers contiennent toutes mes routes
	 */
	public static final String FILENAMEGPS = "cacheGPS.json";
	public static final String FILENAMEWIFI = "cacheWifi.json";
	public static final String FILENAMERIDE = "rides.json";
	public static final String FILENAMEROUTE = "routes.json";
	public static int nbRides = 0;
	public static int nbRoutes = 0;
	public static Date dateStartRunning ;
	public static Date dateStopRunning ;
	public static boolean isConnectedToInternet;
	//User fictif en attendant le login
	public static User userConnected ;
	
	//AsyncTasks to call
	private static SaveRouteInDBTask taskRoute;
	private static SaveRideInDBTask taskRide;
	private static GetCurrentIdsTask taskIds;
	
	//My Fragments
	public static RideResultFragment rideResultFragment;
	public static RunFragment runFragment;
	public static DisplayMapActivity displayMapFragment;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public static SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get the User thanks to the Intent
		Intent userIntent = getIntent();
		if(userIntent != null) {
			userConnected = new User(userIntent.getStringExtra(LoginActivity.EXTRA_EMAIL),
									 userIntent.getStringExtra(LoginActivity.EXTRA_PASSWORD),
									 Float.parseFloat(userIntent.getStringExtra(LoginActivity.EXTRA_WEIGHT)));
			Log.i("TEST", userConnected.toString());
		}
		
		//Manage the fragment
		//displayMapFragment = new DisplayMapFragment();
		
		//Update ids
		taskIds = new GetCurrentIdsTask();
		taskIds.execute();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if(position == 0) {
				runFragment = new RunFragment();
				Bundle args = new Bundle();
				runFragment.setArguments(args);
				return runFragment;
			}
			if(position == 1) {
				rideResultFragment = new RideResultFragment();
				Bundle args = new Bundle();
				//args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				rideResultFragment.setArguments(args);
				return rideResultFragment;
			}
			/*if(position == 2) {
				displayMapFragment = new DisplayMapFragment();
				Bundle args = new Bundle();
				//args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				displayMapFragment.setArguments(args);
				return displayMapFragment;
			}*/
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/*
	 * Gestion du tracking GPS & WIFI
	 */
    public static void startLogPosition() {
    	//Nettoyage des fichiers cache
    	cleanCacheFiles();
		//StartWritingPositionInCache
    	startWritingPositionInCache();
    	// start chrono
    	dateStartRunning = new Date();
    }

	private static void cleanCacheFiles() {
		if(LoginActivity.getAppContext().deleteFile(MainActivity.FILENAMEGPS)) {
			Log.i("Delete file", "FILENAMEGPS file has been deleted");
		} else {
			Log.w("Delete file", "Problem deleting FILENAMEGPS file");
		}
		if(LoginActivity.getAppContext().deleteFile(MainActivity.FILENAMEWIFI)) {
			Log.i("Delete file", "FILENAMEWIFI file has been deleted");
		} else {
			Log.w("Delete file", "Problem deleting FILENAMEWIFI file");
		}
	}

    public static void stopLogPosition() {
    	stopWritingPositionInCache();
    	// stop chrono
    	dateStopRunning = new Date();
    	// handle the new route
    	Route routeToSave = JsonManager.getRoute(JsonManager.openReader(FILENAMEWIFI)).route;
    	taskRoute = new SaveRouteInDBTask();
    	taskRoute.execute(routeToSave);
    	// handle the new ride
    	Ride rideToSave = new Ride(nbRides, nbRoutes, 0, dateStartRunning, dateStopRunning);
    	rideToSave.computeRide();
    	taskRide = new SaveRideInDBTask();
    	taskRide.execute(rideToSave);
    	// modify temp id's
    	nbRides++;
    	nbRoutes++;
    }

    public static void startWritingPositionInCache() {
		GpsTracking gpst = new GpsTracking();
		gpst.startTracking();
		gpst.startRepeatingTask();
	}

    public static void stopWritingPositionInCache() {
		GpsTracking gpst = new GpsTracking();
		gpst.stopTracking();
		gpst.stopRepeatingTask();
	}

    /*
     * Gestion des Rides et Routes
     */
    public static void associateRideToRoute(Route pRouteId, Ride pRide) {

	}

	public static void associateOldRouteIdWithNewId() {

	}
	
	/**
	 * AsyncTask save Route in Database
	 */
	
	public static class SaveRouteInDBTask extends AsyncTask<Route, Void, Void> {
		@Override
		protected Void doInBackground(Route... params) {
			if(params[0] != null) {
				params[0].saveRoute();
			} else {
				Log.e(LogTag.WRITEDB, "Problem while saving the route in the database, maybe the route is null");
			}
			return null;
		}
	}
	
	
	/**
	 * AsyncTask save Ride on Database
	 */
	public static class SaveRideInDBTask extends AsyncTask<Ride, Void, Void> {
		@Override
		protected Void doInBackground(Ride... params) {
			if(params[0] != null) {
				params[0].saveRide();
			} else {
				Log.e(LogTag.WRITEDB, "Problem while saving the ride in the database, maybe the ride is null");
			}
			return null;
		}
	}
	
	
	/**
	 * AsyncTask get current routeId & rideId
	 */
	public static class GetCurrentIdsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			nbRides = HTIDatabaseConnection.getInstance().getNumberOfRides();
			//nbRides = nbRides == 0 ? 0 : nbRides + 1;
			nbRoutes = HTIDatabaseConnection.getInstance().getNumberOfRoutes();
			//nbRoutes = nbRoutes == 0 ? 0 : nbRoutes + 1;
			return null;
		}
	}
}
