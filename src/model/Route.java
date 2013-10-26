package model;

import java.util.List;

import logs.LogTag;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.hti.LoginActivity;
import com.hti.MainActivity;

public class Route {
	
	@SerializedName("routeId")
	private int routeId;
	
	@SerializedName("routePoints")
	private List<Waypoint> routePoints;
	
	@SerializedName("routeKm")
	private float routeKm;
	
	@SerializedName("routeIsTemp")
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

	public void saveRoute() {
		// if internet then store this in the database
		if(Internet.isNetworkAvailable(LoginActivity.getAppContext())) {
			HTIDatabaseConnection htiDbConnection = HTIDatabaseConnection.getInstance();
			if(htiDbConnection.getRoute(this.routeId) == null) {
				String errors = htiDbConnection.addRoute(this);
				Log.i(LogTag.WRITEDB, "The route with the id (" + this.routeId + ") has been saved in the database.");
				if(errors != null) {
					Log.w(LogTag.WRITEDB, "Errors during add route in the database : \n" + errors);
				}
			} else {
				Log.i(LogTag.WRITEDB, "The route with the id (" + this.routeId + ") already exist in the database.");
			}
		}
		// else store this in a JSON file (FILENAMEROUTE)
		else {
			JsonManager.addRoutesInJson(MainActivity.FILENAMEROUTE, this, LoginActivity.getAppContext());
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

	public void setRoutePoints(List<Waypoint> routePoints) {
		this.routePoints = routePoints;
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
