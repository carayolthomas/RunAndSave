package com.hti;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.hti.MainActivity.GetCurrentIdsTask;

import model.Ride;

import utils.HTIDatabaseConnection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RideResultFragment extends Fragment {
	
	public ListView listRidesView;
	private List<Ride> listRidesInfos;
	private List<String> listRidesInfosToString;
	
	/**
	 * Async Tasks
	 */
	private GetAllRidesTask taskRides;
	
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
			listRidesView.setAdapter(new ArrayAdapter<String>(LoginActivity.getAppContext(), android.R.layout.simple_list_item_1, listRidesInfosToString));
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
	
	
}
