package com.hti;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;

import logs.LogTag;
import model.Ride;
import model.Route;
import model.Waypoint;
import utils.HTIDatabaseConnection;
import utils.JsonManager;
import utils.MyLocationListener;
import utils.SearchJsonResultRoute;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class RunFragment extends Fragment {
	
	public Button buttonRun;
	public Button buttonDisplay;
	public Button buttonClean;
	public static String startText = "Start running !";
	public static String stopText = "Stop running !";
	private StartStopRunningTask ssrTask;
	private boolean isStart = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_main_run, container, false);
		
		
		buttonRun = (Button) view.findViewById(R.id.buttonRun);
		buttonRun.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						if(buttonRun.getText().equals(startText)) {
							ssrTask = new StartStopRunningTask();
							ssrTask.execute(startText);
							buttonRun.setText(stopText);
							isStart = true ;
						} else {
							ssrTask = new StartStopRunningTask();
							ssrTask.execute(stopText);
							buttonRun.setText(startText);
							isStart = false ;
						}
					}
				});
		
		buttonDisplay = (Button) view.findViewById(R.id.buttonDisplayCacheDebug);
		buttonDisplay.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						if (!isStart) {
							try {
								String content = "";
								Reader reader = JsonManager.openReader(MainActivity.FILENAMEWIFI, getActivity().getApplicationContext());
								SearchJsonResultRoute routeSearchResult = JsonManager.getRoute(reader);
								Route route = routeSearchResult.route;

								// get the content
								if (route != null) {
									content += "Route : " + route.getRouteId() + "\n";

									for (int j = 0; j < route.getRoutePoints().size() ; j++) {
										content += "\t" + route.getRoutePoints().get(j).getWaypointLat() + ";"
												+ route.getRoutePoints().get(j).getWaypointLng()
												+ "\n";
									}
									content += "----\n";
								}
								 else {
									content = "No route.";
								}
								reader.close();
								// Display the content
								AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
								alert.setTitle("Cache");
								alert.setMessage(content);
								alert.setPositiveButton("OK", null);
								alert.show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),
										Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(getActivity().getApplicationContext(), "Stop recording first!",
									Toast.LENGTH_LONG).show();
						}
					}
				});
		
		buttonClean = (Button) view.findViewById(R.id.buttonCleanCacheDebug);
		buttonClean.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							getActivity().deleteFile(MainActivity.FILENAMEGPS);
						}
				});
		
		return view;
	}


	/**
	 * Represents an asynchronous task used to track you 
	 */
	public class StartStopRunningTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			
			/*Looper.prepare();
			if(params[0].equals(startText)) {
				MainActivity.startWritingPositionInCache(getActivity().getApplicationContext());
			} else {
				MainActivity.stopWritingPositionInCache(getActivity().getApplicationContext());
			}*/
			return null;
		}
	}

}
