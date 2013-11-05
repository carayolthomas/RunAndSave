package com.hti;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import logs.LogTag;
import model.Ride;
import model.Route;

import utils.HTIDatabaseConnection;
import utils.ItemRideAdapter;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
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
	
	/** View for loading screen */
	private ProgressDialog mDialogLoadingMap;
	
	/** The list view for all the rides */
	private ListView mListRidesView;
	
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
				/**  Test loading screen */
				createLoadingWaitDialog("The map is loading. Please wait...");
				/** Get the ride selected by the user */
				String lSelectedFromList = (mListRidesView
						.getItemAtPosition(position).toString());
				int lNumRideSelected = Character
						.getNumericValue(lSelectedFromList.split("°")[1]
								.charAt(0));
				mTaskRouteRide = new GetSelectedRouteRideTask();
				mTaskRouteRide.execute(lNumRideSelected);
			}
		});
		
		return lView;
	}

	/**
	 * This method refresh the rides in the list
	 */
	public void refresh() {
		/** Loading page */
		createLoadingWaitDialog("Your rides are loading. Please wait...");
		/** Get all the rides of the user connected */
		mListRidesInfosToString = new ArrayList<String>();
		mTaskRides = new GetAllRidesTask();
		mTaskRides.execute();
	}
	
	/**
	 * Create a dialog view for waiting
	 * @param pMessage
	 */
	public void createLoadingWaitDialog(String pMessage) {
		mDialogLoadingMap = ProgressDialog.show(getActivity(), "", 
				pMessage, true);
		mDialogLoadingMap.setCancelable(false);
	}
	
	/**
	 * Dismiss the loading dialog view
	 */
	public void dismissLoadingWaitDialog() {
		mDialogLoadingMap.dismiss();
	}

	/**
	 * This method is called each time the user slide on the RideResultFragment
	 * in order to refresh the list of rides
	 */
	@Override
	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible && MainActivity.mIsNewRide) {
			MainActivity.mIsNewRide = false;
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
		
		@Override
		protected void onPostExecute(final Boolean pSuccess) {
			ItemRideAdapter lAdapter = new ItemRideAdapter(
					getActivity().getApplicationContext(),
					R.layout.item_ride_row,
					mListRidesInfosToString
							.toArray(new String[mListRidesInfosToString.size()]));
			mListRidesView.setAdapter(lAdapter);
			/** Cancel the loading dialog */
			dismissLoadingWaitDialog();
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
			if(mRouteInfos != null) {
				return true;
			} else {
				return false;
			}
			
		}
		
		@Override
		protected void onPostExecute(final Boolean pSuccess) {
			if (!pSuccess) {
				Log.e(LogTag.READDB, "The route returned is null");
				try {
					throw new Exception("The route returned is null");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				/**
				 * Launch the map corresponding to the route of the ride
				 * selected : routeInfos
				 */
				Intent lIntent = new Intent(getActivity(),
						DisplayMapActivity.class);
				lIntent.putExtra(MainActivity.ROUTE_TO_DISPLAY, mRouteInfos);
				/** Cancel the loading page */
				dismissLoadingWaitDialog();
				/** Start the new activity */
				startActivity(lIntent);
			}
		}
	}
}
