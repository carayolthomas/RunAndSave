package com.hti;

import java.util.Date;
import java.util.Locale;

import model.Ride;
import model.Route;
import model.User;
import utils.GpsTracking;
import utils.JsonManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	/**
	 * TODO
	 *
	 *  - Association Route / Ride : HashMap
	 *  - View pour tester
	 *
	 */
	public static final int BUFFERSIZE = 10;
	public final static String RIDE_NUMBER_MAP = "rideNumberToMap";
	/*
	 * Ces deux fichiers contiennent toutes mes routes
	 */
	public static final String FILENAMEGPS = "cacheGPS.json";
	public static final String FILENAMEWIFI = "cacheWifi.json";
	public static final String FILENAMERIDE = "rides.json";
	public static final String FILENAMEROUTE = "routes.json";
	public static int nbRides = 0;
	public static int nbRoutes = 0;
	private Date dateStartRunning ;
	private Date dateStopRunning ;
	public static boolean isConnectedToInternet;
	//User fictif en attendant le login
	public User userConnected = new User(1, "toto", "toto", 65);

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nbRides = JsonManager.getNumberOfRides(this);
		nbRides = nbRides == 0 ? 0 : nbRides + 1;
		nbRoutes = JsonManager.getNumberOfRoutes(this);
		nbRoutes = nbRoutes == 0 ? 0 : nbRoutes + 1;

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
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
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
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/*
	 * Gestion du tracking GPS & WIFI
	 */
    public void startLogPosition() {
    	startWritingPositionInCache(getApplicationContext());
    	// start chrono
    	dateStartRunning = new Date();
    }

    public void stopLogPosition() {
    	stopWritingPositionInCache(getApplicationContext());
    	// stop chrono
    	dateStopRunning = new Date();
    	// handle the new route
    	Route routeToSave = JsonManager.getRoute(JsonManager.openReader(FILENAMEGPS, getApplicationContext())).route;
    	routeToSave.saveRoute(userConnected, getApplicationContext());
    	// handle the new ride
    	Ride rideToSave = new Ride(nbRides, nbRoutes, 0, dateStartRunning, dateStopRunning);
    	rideToSave.computeRide(userConnected);
    	rideToSave.saveRide(userConnected.getUserId(), getApplicationContext());
    	// modify temp id's
    	nbRides++;
    	nbRoutes++;
    }

    public static void startWritingPositionInCache(Context pApplicationContext) {
		GpsTracking gpst = new GpsTracking(pApplicationContext);
		gpst.startRepeatingTask();
	}

    public static void stopWritingPositionInCache(Context pApplicationContext) {
		GpsTracking gpst = new GpsTracking(pApplicationContext);
		gpst.stopRepeatingTask();
	}

    /*
     * Gestion des Rides et Routes
     */
    public static void associateRideToRoute(Route pRouteId, Ride pRide) {

	}

	public static void associateOldRouteIdWithNewId() {

	}

	/*
	 * Cache tasks
	 */
	/**
	 * Add the listener on the buttons 'displayGPSCache' and 'displayWifiCache'
	 */
	/*private void addDisplayCacheListener() {
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
	}*/

	/**
	 * Add the listener on the button 'cleanGPSCache'
	 */
	/*private void cleanGPSCacheListener() {
		findViewById(R.id.cleanGPSCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEGPS);
					}
				});

	}*/

	/**
	 * Add the listener on the button 'cleanWifiCache'
	 */
	/*private void cleanWifiCacheListener() {
		findViewById(R.id.cleanWifiCache).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deleteFile(FILENAMEWIFI);
					}
				});
	}*/

	/**
	 * Display the content of a file in cache
	 *
	 * @param cacheFilename
	 *            the file in cache to display
	 */
	/*private void displayCache(String cacheFilename) {
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
	}*/

	/*
	 * Location tasks
	 */
	/**
	 * Add a listener on the button 'cache' which enable the tracking
	 */
	/*private void addRecordLocationListener() {
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
	}*/

}
