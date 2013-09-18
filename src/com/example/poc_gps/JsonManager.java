package com.example.poc_gps;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;

public class JsonManager {
	public static void JSONToCache(String filename, ArrayList<WayPoint> wayPoints, Context context) {
		Gson gson = new Gson();
		Reader reader = JsonManager.openReader(filename, context);
		SearchJSONResult ridesSearchResult = JsonManager.getJSON(filename, reader, wayPoints);

		List<Ride> rides = ridesSearchResult.rides;
		if (rides != null && rides.size() > 0) {
			// get the current ride
			Ride currRide = null;

			for (int i = 0; i < rides.size() && currRide == null; i++) {
				if (rides.get(i).rideNumber == MainActivity.nbRides) {
					currRide = rides.get(i);
				}
			}
			// add the new waypoints
			if (currRide == null) {
				rides.add(new Ride(MainActivity.nbRides, wayPoints.size(),
						wayPoints));

			} else {
				// update the number of waypoints
				currRide.nbWayPoints += wayPoints.size();
			}
		} else {
			// create the Ride
			rides = new ArrayList<Ride>();
			rides.add(new Ride(MainActivity.nbRides, wayPoints.size(),
					wayPoints));

		}

		FileOutputStream cacheFile;
		try {
			reader.close();
			// transform json to string
			String jsonstr = gson.toJson(ridesSearchResult);
			// clear old data
			context.deleteFile(filename);
			wayPoints.clear();
			// recreate the file with the new data
			cacheFile = context.openFileOutput(filename, Context.MODE_PRIVATE);
			cacheFile.write(jsonstr.getBytes());
			cacheFile.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Reader openReader(String filename, Context context) {
		Reader reader = null;
		;
		try {

			reader = new InputStreamReader(context.openFileInput(filename));

		} catch (FileNotFoundException e1) {
			FileOutputStream cacheFile;
			try {
				cacheFile = context.openFileOutput(filename, Context.MODE_PRIVATE);
				cacheFile.close();
				reader = JsonManager.openReader(filename, context);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}

		}
		return reader;
	}

	public static SearchJSONResult getJSON(String filename, Reader reader, ArrayList<WayPoint> wayPoints) {

		Gson gson = new Gson();
		SearchJSONResult ridesSearchResult;
		if (reader == null) {

			ArrayList<Ride> rides = new ArrayList<Ride>();
			rides.add(new Ride(MainActivity.nbRides, wayPoints.size(),
					wayPoints));
			ridesSearchResult = new SearchJSONResult(rides,
					MainActivity.nbRides);
		} else {
			ridesSearchResult = gson.fromJson(reader, SearchJSONResult.class);

			if (ridesSearchResult == null) {

				ArrayList<Ride> rides = new ArrayList<Ride>();
				rides.add(new Ride(MainActivity.nbRides, wayPoints.size(),
						wayPoints));
				ridesSearchResult = new SearchJSONResult(rides,
						MainActivity.nbRides);
			}
		}
		return ridesSearchResult;

	}
	
	public static SearchJSONResult getAllRides(String filename, Reader reader) {
		Gson gson = new Gson();
		return gson.fromJson(reader, SearchJSONResult.class);
	}
	
	public static int getNumberOfRides(Context context) {
		Gson gson = new Gson();
		//Subjective choice in the filenale, we could open the wifi file or create only 1 file
		Reader reader = JsonManager.openReader(MainActivity.FILENAMEGPS,
				context);
		return gson.fromJson(reader, SearchJSONResult.class).nbRides;
	}
}
