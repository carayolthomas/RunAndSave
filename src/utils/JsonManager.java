package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;
import java.util.List;

import model.*;
import android.content.Context;

import com.hti.*;
import com.google.gson.Gson;

public class JsonManager {


	/**
	 * Open the file in cache (read)
	 * 
	 * @param pFilename
	 *            the name of the file
	 * @param pContext
	 *            the context of the application to access the cache
	 * @return the reader to read the file
	 */
	public static Reader openReader(String pFilename, Context pContext) {
		Reader reader = null;

		try {
			reader = new InputStreamReader(pContext.openFileInput(pFilename));
		} catch (FileNotFoundException e1) {
			FileOutputStream cacheFile;
			try {
				cacheFile = pContext.openFileOutput(pFilename,
						Context.MODE_PRIVATE);
				cacheFile.close();
				reader = JsonManager.openReader(pFilename, pContext);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return reader;
	}


	
	/**
	 * Get the number of route in cache
	 * 
	 * @param context
	 *            the context to access the cache
	 * @return the number of routes in cache
	 */
	public static int getNumberOfRides(Context context) {
		Gson gson = new Gson();
		// Subjective choice in the filenale, we could open the wifi file or
		// create only 1 file
		Reader reader = JsonManager.openReader(MainActivity.FILENAMEGPS,
				context);
		int nb;
		try {
			nb = gson.fromJson(reader, SearchJsonResultRoutes.class).routes.size();
		} catch (Exception e) {
			nb = 0;
			e.printStackTrace();
		}
		return nb;
	}
	
	/*
	 * 
	 * Handler for each Ride which will be store in Json files (3G off)
	 * 
	 */
	
	private static SearchJsonResultRides createRideJson(String pFilename, Reader pReader) {

		Gson gson = new Gson();
		SearchJsonResultRides ridesSearchResult;

		// if the file does not exist yet
		if (pReader == null) {
			List<Ride> rides = new Vector<Ride>();
			ridesSearchResult = new SearchJsonResultRides(rides);
		} else {
			ridesSearchResult = gson.fromJson(pReader, SearchJsonResultRides.class);
		}
		return ridesSearchResult;
	}
	
	public static void addRideInJson(String pFileName, Ride pRide, Context pContext) {
		Gson gson = new Gson();
		FileOutputStream cacheFile;
		Reader reader = JsonManager.openReader(pFileName, pContext);
		SearchJsonResultRides ridesSearchResult = JsonManager.createRideJson(pFileName, reader);

		List<Ride> rides = ridesSearchResult.rides;
		rides.add(pRide);

		// update the file in cache
		try {
			reader.close();
			// transform json to string
			String jsonstr = gson.toJson(ridesSearchResult);
			// clear old data
			pContext.deleteFile(pFileName);
			// recreate the file with the new data
			cacheFile = pContext.openFileOutput(pFileName, Context.MODE_PRIVATE);
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
	
	/*
	 * 
	 * Handlers of the file containing all the routes which are not in the database
	 * 
	 */
	
	private static SearchJsonResultRoutes createRoutesJson(String pFilename, Reader pReader) {

		Gson gson = new Gson();
		SearchJsonResultRoutes routesSearchResult;

		// if the file does not exist yet
		if (pReader == null) {
			List<Route> routes = new Vector<Route>();
			routesSearchResult = new SearchJsonResultRoutes(routes);
		} else {
			routesSearchResult = gson.fromJson(pReader, SearchJsonResultRoutes.class);
		}
		return routesSearchResult;
	}
	
	public static void addRoutesInJson(String pFileName, Route pRoute, Context pContext) {
		Gson gson = new Gson();
		FileOutputStream cacheFile;
		Reader reader = JsonManager.openReader(pFileName, pContext);
		SearchJsonResultRoutes routesSearchResult = JsonManager.createRoutesJson(pFileName, reader);

		List<Route> routes = routesSearchResult.routes;
		routes.add(pRoute);

		// update the file in cache
		try {
			reader.close();
			// transform json to string
			String jsonstr = gson.toJson(routesSearchResult);
			// clear old data
			pContext.deleteFile(pFileName);
			// recreate the file with the new data
			cacheFile = pContext.openFileOutput(pFileName, Context.MODE_PRIVATE);
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

	
	/*
	 * 
	 * Handlers of JSON files while someone is running
	 * 
	 */
	
	/**
	 * Create the json for a temporary route
	 * 
	 * @param pFilename
	 *            the cache filename
	 * @param pReader
	 *            the reader to read the file
	 * @return
	 */
	private static SearchJsonResultRoute createRouteJson(String pFilename, Reader pReader) {

		Gson gson = new Gson();
		SearchJsonResultRoute routeSearchResult;

		// if the file does not exist yet
		if (pReader == null) {
			Route route = new Route(0, null, 0, true);
			routeSearchResult = new SearchJsonResultRoute(route);
		} else {
			routeSearchResult = gson.fromJson(pReader, SearchJsonResultRoute.class);
		}
		return routeSearchResult;
	}
	
	/**
	 * Add the waypoints to a route in the file in cache while the run is started
	 * This file will contain only the temporary route
	 * 
	 * @param pFileName
	 *            the file in cache
	 * @param pWayPoints
	 *            the waypoints to add
	 * @param pContext
	 *            the context of the application to access the cache
	 * @param pRouteId
	 *            the route in which add the waypoints
	 */
	public static void addRouteInJson(String pFileName, Vector<Waypoint> pWayPoints, Context pContext, int pRouteId) {
		Gson gson = new Gson();
		FileOutputStream cacheFile;
		Reader reader = JsonManager.openReader(pFileName, pContext);
		SearchJsonResultRoute routeSearchResult = JsonManager.createRouteJson(pFileName, reader);

		Route route = routeSearchResult.route;
		// if the route already exists, just add the next waypoints
		if(route.getRouteId() == pRouteId) {
			route.getRoutePoints().addAll(pWayPoints);
		} 
		// else create the route
		else {
			route = new Route(routeSearchResult.route.getRouteId(), pWayPoints, 0, true);
		}

		// update the file in cache
		try {
			reader.close();
			// transform json to string
			String jsonstr = gson.toJson(routeSearchResult);
			// clear old data
			pContext.deleteFile(pFileName);
			pWayPoints.clear();
			// recreate the file with the new data
			cacheFile = pContext.openFileOutput(pFileName, Context.MODE_PRIVATE);
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
	 * Get a route in cache
	 * 
	 * @param pReader
	 *            the reader to read the file
	 * @return the route in cache
	 */
	public static SearchJsonResultRoute getRoute(Reader pReader) {
		Gson gson = new Gson();
		return gson.fromJson(pReader, SearchJsonResultRoute.class);
	}

	/**
	 * Get the number of route in cache
	 * 
	 * @param context
	 *            the context to access the cache
	 * @return the number of routes in cache
	 */
	public static int getNumberOfRoutes(Context context) {
		Gson gson = new Gson();
		// Subjective choice in the filenale, we could open the wifi file or
		// create only 1 file
		Reader reader = JsonManager.openReader(MainActivity.FILENAMEGPS,
				context);
		int nb;
		try {
			nb = gson.fromJson(reader, SearchJsonResultRoutes.class).routes.size();
		} catch (Exception e) {
			nb = 0;
			e.printStackTrace();
		}
		return nb;
	}
	
	
}