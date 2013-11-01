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

/**
 * MainActivity which provide the three fragment of this application
 * 
 * @author hti
 * 
 */
public class MainActivity extends FragmentActivity {

	/** The size of the buffer which allow me to store the position of the user */
	public static final int BUFFERSIZE = 10;

	/** Constant key for the route to display */
	public final static String ROUTE_TO_DISPLAY = "routeToDisplay";

	/** File name to store the GPS values */
	public static final String FILENAMEGPS = "cacheGPS.json";

	/** File name to store the position values from the Internet */
	public static final String FILENAMEWIFI = "cacheWifi.json";

	/** File to store rides */
	public static final String FILENAMERIDE = "rides.json";

	/** File to store routes */
	public static final String FILENAMEROUTE = "routes.json";

	/** Number of rides for the user connected */
	public static int nbRides = 0;

	/** Number of routes for the user connected */
	public static int nbRoutes = 0;

	/** Start date of the current ride */
	public static Date dateStartRunning;

	/** Stop date of the current ride */
	public static Date dateStopRunning;

	/** Connected to Internet ? */
	public static boolean isConnectedToInternet;

	/** The user connected */
	public static User userConnected;

	/** Asynchronous task to save the route in DB */
	private static SaveRouteInDBTask taskRoute;

	/** Asynchronous task to save the ride in DB */
	private static SaveRideInDBTask taskRide;

	/** Asynchronous task to get the number of routes & rides in the DB */
	private static GetCurrentIdsTask taskIds;

	/** RideResultFragment */
	public static RideResultFragment rideResultFragment;

	/** RunFragment */
	public static RunFragment runFragment;

	/** DisplayMapActivity */
	public static DisplayMapActivity displayMapActivity;

	/** SectionsPagerAdapter */
	public static SectionsPagerAdapter mSectionsPagerAdapter;

	/** The ViewPager that will host the section contents. */
	public static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** Get the User thanks to the Intent */
		Intent userIntent = getIntent();
		if (userIntent != null) {
			userConnected = new User(
					userIntent.getStringExtra(LoginActivity.EXTRA_EMAIL),
					userIntent.getStringExtra(LoginActivity.EXTRA_PASSWORD),
					Float.parseFloat(userIntent
							.getStringExtra(LoginActivity.EXTRA_WEIGHT)));
		}

		/** Update nbRoutes & nbRides */
		taskIds = new GetCurrentIdsTask();
		taskIds.execute();

		/**
		 * Create the adapter that will return a fragment for each of the three
		 * primary sections of the app.
		 */
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		/** Set up the ViewPager with the sections adapter. */
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 * Inflate the menu; this adds items to the action bar if it is present.
		 */
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
			if (position == 0) {
				runFragment = new RunFragment();
				Bundle args = new Bundle();
				runFragment.setArguments(args);
				return runFragment;
			}
			if (position == 1) {
				rideResultFragment = new RideResultFragment();
				Bundle args = new Bundle();
				// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position
				// + 1);
				rideResultFragment.setArguments(args);
				return rideResultFragment;
			}
			/*
			 * if(position == 2) { displayMapFragment = new
			 * DisplayMapFragment(); Bundle args = new Bundle();
			 * //args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
			 * 1); displayMapFragment.setArguments(args); return
			 * displayMapFragment; }
			 */
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
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

	/**
	 * Tracking handle ON
	 */
	public static void startLogPosition() {
		/** Clean cache files */
		cleanCacheFiles();
		/** StartWritingPositionInCache */
		startWritingPositionInCache();
		/** Start chrono */
		dateStartRunning = new Date();
	}

	/**
	 * Clean the files in the cache
	 */
	private static void cleanCacheFiles() {
		if (LoginActivity.getAppContext().deleteFile(MainActivity.FILENAMEGPS)) {
			Log.i("Delete file", "FILENAMEGPS file has been deleted");
		} else {
			Log.w("Delete file", "Problem deleting FILENAMEGPS file");
		}
		if (LoginActivity.getAppContext().deleteFile(MainActivity.FILENAMEWIFI)) {
			Log.i("Delete file", "FILENAMEWIFI file has been deleted");
		} else {
			Log.w("Delete file", "Problem deleting FILENAMEWIFI file");
		}
	}

	/**
	 * Tracking handle OFF
	 */
	public static void stopLogPosition() {
		stopWritingPositionInCache();
		/** Stop chrono */
		dateStopRunning = new Date();
		/** handle the new route */
		Route lRouteToSave = JsonManager.getRoute(JsonManager
				.openReader(FILENAMEWIFI)).route;
		taskRoute = new SaveRouteInDBTask();
		taskRoute.execute(lRouteToSave);
		/** handle the new ride */
		Ride lRideToSave = new Ride(nbRides, nbRoutes, 0, dateStartRunning,
				dateStopRunning);
		lRideToSave.computeRide();
		taskRide = new SaveRideInDBTask();
		taskRide.execute(lRideToSave);
		/** modify id's */
		nbRides++;
		nbRoutes++;
	}

	/**
	 * Enable the GPS tracking
	 */
	public static void startWritingPositionInCache() {
		GpsTracking gpst = new GpsTracking();
		gpst.startTracking();
		gpst.startRepeatingTask();
	}

	/**
	 * Disable the GPS tracking
	 */
	public static void stopWritingPositionInCache() {
		GpsTracking gpst = new GpsTracking();
		gpst.stopTracking();
		gpst.stopRepeatingTask();
	}

	/**
	 * AsyncTask save Route in Database
	 */
	public static class SaveRouteInDBTask extends AsyncTask<Route, Void, Void> {
		@Override
		protected Void doInBackground(Route... params) {
			if (params[0] != null) {
				params[0].saveRoute();
			} else {
				Log.e(LogTag.WRITEDB,
						"Problem while saving the route in the database, maybe the route is null");
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
			if (params[0] != null) {
				params[0].saveRide();
			} else {
				Log.e(LogTag.WRITEDB,
						"Problem while saving the ride in the database, maybe the ride is null");
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
			nbRoutes = HTIDatabaseConnection.getInstance().getNumberOfRoutes();
			return null;
		}
	}
}
