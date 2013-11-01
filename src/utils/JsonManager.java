package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;

import model.*;
import android.content.Context;

import com.hti.*;
import com.mongodb.BasicDBObject;
import com.google.gson.Gson;

/**
 * Class to manage JSON files used in this application
 * 
 * @author thomas
 * 
 */
public class JsonManager {

	/**
	 * Open the file in cache (read)
	 * 
	 * @param pFilename
	 *            the name of the file
	 * @return the reader to read the file
	 */
	public static Reader openReader(String pFilename) {
		Reader lReader = null;

		try {
			lReader = new InputStreamReader(LoginActivity.getAppContext()
					.openFileInput(pFilename));
		} catch (FileNotFoundException e1) {
			FileOutputStream lCacheFile;
			try {
				lCacheFile = LoginActivity.getAppContext().openFileOutput(
						pFilename, Context.MODE_PRIVATE);
				lCacheFile.close();
				lReader = JsonManager.openReader(pFilename);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return lReader;
	}

	/**
	 * Get the number of route in cache
	 * 
	 * @param context
	 *            the context to access the cache
	 * @return the number of routes in cache
	 */
	public static int getNumberOfRides() {
		Gson lGson = new Gson();
		// Subjective choice in the filenale, we could open the wifi file or
		// create only 1 file
		Reader lReader = JsonManager.openReader(MainActivity.FILENAMEGPS);
		int lNb;
		try {
			lNb = lGson.fromJson(lReader, SearchJsonResultRoutes.class).pRoutes
					.size();
		} catch (Exception e) {
			lNb = 0;
			e.printStackTrace();
		}
		return lNb;
	}

	/**
	 * Handler for each Ride which will be store in Json files (3G off)
	 * 
	 * @param pFilename
	 * @param pReader
	 * @return rides
	 */
	private static SearchJsonResultRides createRideJson(String pFilename,
			Reader pReader) {

		Gson lGson = new Gson();
		SearchJsonResultRides lRidesSearchResult;

		/** if the file does not exist yet */
		if (pReader == null) {
			List<Ride> lRides = new Vector<Ride>();
			lRidesSearchResult = new SearchJsonResultRides(lRides);
		} else {
			lRidesSearchResult = lGson.fromJson(pReader,
					SearchJsonResultRides.class);
		}
		return lRidesSearchResult;
	}

	/**
	 * Add the ride in a JSON file
	 * 
	 * @param pFileName
	 * @param pRide
	 * @param pContext
	 */
	public static void addRideInJson(String pFileName, Ride pRide,
			Context pContext) {
		Gson lGson = new Gson();
		FileOutputStream lCacheFile;
		Reader lReader = JsonManager.openReader(pFileName);
		SearchJsonResultRides lRidesSearchResult = JsonManager.createRideJson(
				pFileName, lReader);

		List<Ride> lRides = lRidesSearchResult.mRides;
		lRides.add(pRide);

		/** update the file in cache */
		try {
			lReader.close();
			/** transform json to string */
			String jsonstr = lGson.toJson(lRidesSearchResult);
			/** clear old data */
			pContext.deleteFile(pFileName);
			/** recreate the file with the new data */
			lCacheFile = pContext.openFileOutput(pFileName,
					Context.MODE_PRIVATE);
			lCacheFile.write(jsonstr.getBytes());
			lCacheFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Handlers of the file containing all the routes which are not in the
	 * database
	 * 
	 * @param pFilename
	 * @param pReader
	 * @return routes
	 */
	private static SearchJsonResultRoutes createRoutesJson(String pFilename,
			Reader pReader) {
		Gson lGson = new Gson();
		SearchJsonResultRoutes lRoutesSearchResult;

		/** if the file does not exist yet */
		if (pReader == null) {
			List<Route> lRoutes = new Vector<Route>();
			lRoutesSearchResult = new SearchJsonResultRoutes(lRoutes);
		} else {
			lRoutesSearchResult = lGson.fromJson(pReader,
					SearchJsonResultRoutes.class);
		}
		return lRoutesSearchResult;
	}

	/**
	 * Add a route in JSON file
	 * 
	 * @param pFileName
	 * @param pRoute
	 * @param pContext
	 */
	public static void addRoutesInJson(String pFileName, Route pRoute,
			Context pContext) {
		Gson lGson = new Gson();
		FileOutputStream lCacheFile;
		Reader lReader = JsonManager.openReader(pFileName);
		SearchJsonResultRoutes lRoutesSearchResult = JsonManager
				.createRoutesJson(pFileName, lReader);

		List<Route> lRoutes = lRoutesSearchResult.pRoutes;
		lRoutes.add(pRoute);

		/** update the file in cache */
		try {
			lReader.close();
			/** transform json to string */
			String lJsonstr = lGson.toJson(lRoutesSearchResult);
			/** clear old data */
			pContext.deleteFile(pFileName);
			/** recreate the file with the new data */
			lCacheFile = pContext.openFileOutput(pFileName,
					Context.MODE_PRIVATE);
			lCacheFile.write(lJsonstr.getBytes());
			lCacheFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the json for a temporary route
	 * 
	 * @param pFilename
	 *            the cache filename
	 * @param pReader
	 *            the reader to read the file
	 * @return
	 */
	private static SearchJsonResultRoute createRouteJson(String pFilename,
			Reader pReader) {

		Gson lGson = new Gson();
		SearchJsonResultRoute lRouteSearchResult = lGson.fromJson(pReader,
				SearchJsonResultRoute.class);
		if (lRouteSearchResult == null) {
			Route lRoute = new Route(MainActivity.nbRoutes, null, 0, true);
			lRouteSearchResult = new SearchJsonResultRoute(lRoute);
		}
		return lRouteSearchResult;
	}

	/**
	 * Add the waypoints to a route in the file in cache while the run is
	 * started This file will contain only the temporary route
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
	public static void addRouteInJson(String pFileName,
			Vector<BasicDBObject> pWayPoints, Context pContext, int pRouteId) {
		Gson lGson = new Gson();
		FileOutputStream lCacheFile;
		Reader lReader = JsonManager.openReader(pFileName);
		SearchJsonResultRoute lRouteSearchResult = JsonManager.createRouteJson(
				pFileName, lReader);

		Route lRoute = lRouteSearchResult.mRoute;
		/** if the route already exists, just add the next waypoints */
		if (lRoute.getRouteId() == pRouteId) {
			List<BasicDBObject> listToAdd = new ArrayList<BasicDBObject>();
			if (lRoute.getRoutePoints() != null) {
				listToAdd.addAll(lRoute.getRoutePoints());
			}
			listToAdd.addAll(pWayPoints);
			lRoute.setRoutePoints(listToAdd);
		}
		/** else create the route */
		else {
			lRoute = new Route(MainActivity.nbRoutes, pWayPoints, 0, true);
		}

		/** update the file in cache */
		try {
			lReader.close();
			/** transform json to string */
			String lJsonstr = lGson.toJson(lRouteSearchResult);
			/** clear old data */
			pContext.deleteFile(pFileName);
			pWayPoints.clear();
			/** recreate the file with the new data */
			lCacheFile = pContext.openFileOutput(pFileName,
					Context.MODE_PRIVATE);
			lCacheFile.write(lJsonstr.getBytes());
			lCacheFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		Gson lGson = new Gson();
		return lGson.fromJson(pReader, SearchJsonResultRoute.class);
	}

	/**
	 * Get the number of route in cache
	 * 
	 * @param context
	 *            the context to access the cache
	 * @return the number of routes in cache
	 */
	public static int getNumberOfRoutes() {
		Gson lGson = new Gson();
		/**
		 * Subjective choice in the filenale, we could open the wifi file or
		 * create only 1 file
		 */
		Reader lReader = JsonManager.openReader(MainActivity.FILENAMEGPS);
		int lNb;
		try {
			lNb = lGson.fromJson(lReader, SearchJsonResultRoutes.class).pRoutes
					.size();
		} catch (Exception e) {
			lNb = 0;
			e.printStackTrace();
		}
		return lNb;
	}
}