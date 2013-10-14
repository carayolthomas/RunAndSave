package model;

import java.util.List;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;
import android.content.Context;
import android.util.Log;

import com.hti.MainActivity;

public class Route {

	private int routeId;
	private List<Waypoint> routePoints;
	private float routeKm;
	private boolean routeIsTemp;

	public Route(int routeId) {
		super();
		this.routeId = routeId;
	}

	public Route(int routeId, List<Waypoint> routePoints, float routeKm, boolean pRouteIsTemp) {
		super();
		this.routeId = routeId;
		this.routePoints = routePoints;
		this.routeKm = routeKm;
		this.routeIsTemp = pRouteIsTemp;
	}

	public void saveRoute(User pUserId, Context pApplicationContext) {
		// if internet then store this in the database
		if(Internet.isNetworkAvailable(pApplicationContext)) {
			HTIDatabaseConnection htiDbConnection = HTIDatabaseConnection.getInstance();
			if(htiDbConnection.getRoute(this.routeId) == null) {
				htiDbConnection.addRoute(this);
				Log.v("Route.java", "The route with the id (" + this.routeId + ") has been saved in the database.");
			} else {
				Log.v("Route.java", "The route with the id (" + this.routeId + ") already exist in the database.");
			}
		}
		// else store this in a JSON file (FILENAMEROUTE)
		else {
			JsonManager.addRoutesInJson(MainActivity.FILENAMEROUTE, this, pApplicationContext);
		}
	}

	public void addRouteToQueue() {
	}

	/**
	 * Ajoute une liste de WayPoints � partir d'un fichier Json
	 * @param pUserId
	 */
	public void computeRoute(User pUserId) {
		// already done TO CHECK
	}

	public int getRouteId() {
		return this.routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public List<Waypoint> getRoutePoints() {
		return this.routePoints;
	}

	public void addRoutePoints(Waypoint wpt) {
		this.routePoints.add(wpt);
	}

	public float getRouteKm() {
		return this.routeKm;
	}

	public boolean isRouteIsTemp() {
		return routeIsTemp;
	}

	public void setRouteIsTemp(boolean routeIsTemp) {
		this.routeIsTemp = routeIsTemp;
	}

}
