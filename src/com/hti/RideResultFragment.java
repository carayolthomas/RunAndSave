package com.hti;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import model.Ride;
import model.Route;

import utils.HTIDatabaseConnection;
import utils.ItemRideAdapter;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Fragment from the MainActivity which allow the user to display all the ride
 * he has done in a list
 * 
 * @author hti
 * 
 */
public class RideResultFragment extends Fragment {

	/** Constant in order to store the route to display in an Intent */
	public static String EXTRA_ROUTE = "route_to_display";

	/** The list view for all the rides */
	public ListView mListRidesView;

	/** The rides list */
	private List<Ride> mListRidesInfos;

	/** The list of description of all rides */
	private List<String> mListRidesInfosToString;

	/** The route selected by the user */
	public static Route mRouteInfos;

	/** The asynchronous task to get all the rides from the database */
	private GetAllRidesTask mTaskRides;

	/** The asynchronous task to get the ride selected by the user */
	private GetSelectedRouteRideTask mTaskRouteRide;

	public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
			Bundle pSavedInstanceState) {
		View lView = pInflater.inflate(R.layout.fragment_main_result_ride,
				pContainer, false);

		/** Display all of these rides in the ListView */
		mListRidesView = (ListView) lView.findViewById(R.id.ridesListView);
		mListRidesView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/** Get the ride selected by the user */
				String lSelectedFromList = (mListRidesView
						.getItemAtPosition(position).toString());
				int lNumRideSelected = Character
						.getNumericValue(lSelectedFromList.split("°")[1]
								.charAt(0));
				mTaskRouteRide = new GetSelectedRouteRideTask();
				mTaskRouteRide.execute(lNumRideSelected);
				/** Wait for the getter (to do in a better way) */
				try {
					while (mTaskRouteRide.get().booleanValue() != true) {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				/**
				 * Launch the map corresponding to the route of the ride
				 * selected : routeInfos
				 */
				Intent lIntent = new Intent(getActivity(),
						DisplayMapActivity.class);
				lIntent.putExtra(MainActivity.ROUTE_TO_DISPLAY, mRouteInfos);
				startActivity(lIntent);
			}
		});

		return lView;
	}

	/**
	 * This method allows the user to refresh the rides (TODO automatically)
	 */
	public void refresh() {
		/** Get all the rides of the user connected */
		mListRidesInfosToString = new ArrayList<String>();
		mTaskRides = new GetAllRidesTask();
		mTaskRides.execute();
		try {
			while (mTaskRides.get().booleanValue() != true) {
				Thread.sleep(10);
			}

			ItemRideAdapter lAdapter = new ItemRideAdapter(
					this.getActivity().getApplicationContext(),
					R.layout.item_ride_row,
					mListRidesInfosToString
							.toArray(new String[mListRidesInfosToString.size()]));
			mListRidesView.setAdapter(lAdapter);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is called each time the user slide on the RideResultFragment
	 * in order to refresh the list of rides
	 */
	@Override
	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible) {
			refresh();
		}
	}

	/**
	 * AsyncTask get all rides for the user connected
	 */
	public class GetAllRidesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			mListRidesInfos = HTIDatabaseConnection.getInstance()
					.getAllUserRides();
			mListRidesInfosToString = new ArrayList<String>();
			for (Ride lR : mListRidesInfos) {
				mListRidesInfosToString.add(lR.toString());
			}
			return true;
		}
	}

	/**
	 * AsyncTask get the ride selected
	 */
	public class GetSelectedRouteRideTask extends
			AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Ride lRideInfos = HTIDatabaseConnection.getInstance().getRide(
					params[0]);
			mRouteInfos = HTIDatabaseConnection.getInstance().getRoute(
					lRideInfos.getRideRouteId());
			return true;
		}
	}

}
