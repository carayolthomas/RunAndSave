package com.example.poc_gps;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;

public class JsonManager {

	/**
	 * Add the waypoints to a ride in the file in cache
	 * 
	 * @param filename
	 *            the file in cache
	 * @param wayPoints
	 *            the waypoints to add
	 * @param context
	 *            the context of the application to access the cache
	 * @param nbRides
	 *            the ride in which add the waypoints
	 */
	public static void addWaypointsToRide(String filename,
			Vector<WayPoint> wayPoints, Context context, int nbRides) {
		Gson gson = new Gson();
		FileOutputStream cacheFile;
		Reader reader = JsonManager.openReader(filename, context);
		SearchJSONResult ridesSearchResult = JsonManager.createRideJson(
				filename, reader, nbRides);

		List<Ride> rides = ridesSearchResult.rides;
		//if there is no ride
		if (rides != null && rides.size() > 0) {
			Ride currRide = null;
			// get the current ride
			for (int i = 0; i < rides.size() && currRide == null; i++) {
				if (rides.get(i).rideNumber == nbRides) {
					currRide = rides.get(i);
				}
			}
			// if the ride does not exist yet
			if (currRide == null) {
				rides.add(new Ride(nbRides, wayPoints.size(), wayPoints));
				ridesSearchResult.nbRides++;

			} else {
				// add the new waypoints
				currRide.wayPoints.addAll(wayPoints);
				// update the number of waypoints
				currRide.nbWayPoints += wayPoints.size();
			}
		} else {
			// create the Ride
			rides = new Vector<Ride>();
			rides.add(new Ride(nbRides, wayPoints.size(), wayPoints));
			ridesSearchResult.nbRides = 1;
		}

		// update the file in cache
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

	/**
	 * Open the file in cache (read)
	 * 
	 * @param filename
	 *            the name of the file
	 * @param context
	 *            the context of the application to access the cache
	 * @return the reader to read the file
	 */
	public static Reader openReader(String filename, Context context) {
		Reader reader = null;

		try {
			reader = new InputStreamReader(context.openFileInput(filename));
		} catch (FileNotFoundException e1) {
			FileOutputStream cacheFile;
			try {
				cacheFile = context.openFileOutput(filename,
						Context.MODE_PRIVATE);
				cacheFile.close();
				reader = JsonManager.openReader(filename, context);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return reader;
	}

	/**
	 * Create the json for a ride
	 * 
	 * @param filename
	 *            the cache filename
	 * @param reader
	 *            the reader to read the file
	 * @param wayPoints
	 *            the wayponts to
	 * @param nbRides
	 * @return
	 */
	private static SearchJSONResult createRideJson(String filename,
			Reader reader, int nbRides) {

		Gson gson = new Gson();
		SearchJSONResult ridesSearchResult;

		// if the file does not exist yet
		if (reader == null) {
			Vector<Ride> rides = new Vector<Ride>();
			rides.add(new Ride(nbRides, 0, new Vector<WayPoint>()));
			ridesSearchResult = new SearchJSONResult(rides, nbRides);
		} else {
			ridesSearchResult = gson.fromJson(reader, SearchJSONResult.class);
			// if the ride is a new one
			if (ridesSearchResult == null) {
				Vector<Ride> rides = new Vector<Ride>();
				rides.add(new Ride(nbRides, 0, new Vector<WayPoint>()));
				ridesSearchResult = new SearchJSONResult(rides, nbRides);
			}
		}
		return ridesSearchResult;
	}

	/**
	 * Get all the rides in cache
	 * 
	 * @param reader
	 *            the reader to read the file
	 * @return the rides in cache
	 */
	public static SearchJSONResult getAllRides(Reader reader) {
		Gson gson = new Gson();
		return gson.fromJson(reader, SearchJSONResult.class);
	}

	/**
	 * Get the number of ride in cache
	 * 
	 * @param context
	 *            the context to access the cache
	 * @return the number of rides in cache
	 */
	public static int getNumberOfRides(Context context) {
		Gson gson = new Gson();
		// Subjective choice in the filenale, we could open the wifi file or
		// create only 1 file
		Reader reader = JsonManager.openReader(MainActivity.FILENAMEGPS,
				context);
		int nb;
		try {
			nb = gson.fromJson(reader, SearchJSONResult.class).nbRides;
		} catch (Exception e) {
			nb = 0;
			e.printStackTrace();
		}
		return nb;
	}
}
