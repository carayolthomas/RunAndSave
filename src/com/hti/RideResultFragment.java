package com.hti;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.hti.MainActivity.GetCurrentIdsTask;

import model.Ride;
import model.Route;

import utils.HTIDatabaseConnection;
import utils.ItemRide;
import utils.ItemRideAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class RideResultFragment extends Fragment {
	
	public static String EXTRA_ROUTE = "route_to_display";
	
	public ListView listRidesView;
	private List<Ride> listRidesInfos;
	private List<String> listRidesInfosToString;
	public static Route routeInfos;
	
	/**
	 * Async Tasks
	 */
	private GetAllRidesTask taskRides;
	private GetSelectedRouteRideTask taskRouteRide;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_main_result_ride, container, false);
		
	    Button btn_refresh = (Button) view.findViewById(R.id.refreshRidesList);
	    btn_refresh.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
				 refresh();
	    }});
	    
		//Display all of these rides in the ListView
		listRidesView = (ListView) view.findViewById(R.id.ridesListView);
		
	    listRidesView.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    		//Get the ride selected by the user
				String selectedFromList = (listRidesView.getItemAtPosition(position).toString());
				int numRideSelected = Character.getNumericValue(selectedFromList.split("°")[1].charAt(0));
				taskRouteRide = new GetSelectedRouteRideTask();
				taskRouteRide.execute(numRideSelected);
				//Wait for the getter
				try {
					while(taskRouteRide.get().booleanValue() != true) {
							Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	    	    //Launch the map corresponding to the route of the ride selected : routeInfos
				Intent myIntent = new Intent(getActivity(), DisplayMapActivity.class);
				Log.i("Debug Map", "Intent created");
				myIntent.putExtra(MainActivity.ROUTE_TO_DISPLAY, routeInfos);
				Log.i("Debug Map", "Intent fullfilled");
				startActivity(myIntent);
				Log.i("Debug Map", "Activity Map started");
				
	    	}});
	    
		return view;
	}

	public void refresh() {
		//Get all the rides of the user connected
		listRidesInfosToString = new ArrayList<String>();
		taskRides = new GetAllRidesTask();
		taskRides.execute();
		try {
			while(taskRides.get().booleanValue() != true) {
				Thread.sleep(10);
			}
			
			ItemRideAdapter adapter = new ItemRideAdapter(this.getActivity().getApplicationContext(),
														  R.layout.item_ride_row,
														  listRidesInfosToString.toArray(new String[listRidesInfosToString.size()]));
			listRidesView.setAdapter(adapter);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * AsyncTask get all rides for the user connected
	 */
	public class GetAllRidesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			listRidesInfos = HTIDatabaseConnection.getInstance().getAllUserRides();
			listRidesInfosToString = new ArrayList<String>();
			for(Ride r : listRidesInfos) {
				listRidesInfosToString.add(r.toString());
			}
			return true;
		}
	}
	
	/**
	 * AsyncTask get the ride selected
	 */
	public class GetSelectedRouteRideTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Ride rideInfos = HTIDatabaseConnection.getInstance().getRide(params[0]);
			routeInfos = HTIDatabaseConnection.getInstance().getRoute(rideInfos.getRideRouteId());
			return true;
		}
	}

}
