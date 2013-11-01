package model;

import java.util.ArrayList;
import java.util.List;

import logs.LogTag;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.hti.LoginActivity;
import com.hti.MainActivity;
import com.mongodb.BasicDBObject;

/**
 * This class describe a route which will be displayed to the user
 * 
 * @author hti
 * 
 */
public class Route implements Parcelable {

	/** The route Id */
	@SerializedName("routeId")
	private int mRouteId;

	/**
	 * A list of BasicDBObject which will store all the waypoints to insert/get
	 * to the database
	 */
	@SerializedName("routePoints")
	private List<BasicDBObject> mRoutePoints;

	/** Number of kilometer of the route */
	@SerializedName("routeKm")
	private float mRouteKm;

	/**
	 * Boolean in order to know if the route is temporary (not used for this
	 * version)
	 */
	@SerializedName("routeIsTemp")
	private boolean mRouteIsTemp;

	/**
	 * A list of WayPointMap which will allow me to display this point on a
	 * google map
	 */
	private List<WaypointMAP> mRoutePointsTemp;

	/**
	 * Route constructor
	 * 
	 * @param pRouteId
	 * @param pRoutePoints
	 * @param pRouteKm
	 * @param pRouteIsTemp
	 */
	public Route(int pRouteId, List<BasicDBObject> pRoutePoints,
			float pRouteKm, boolean pRouteIsTemp) {
		super();
		this.mRouteId = pRouteId;
		this.mRoutePoints = pRoutePoints;
		this.mRouteKm = pRouteKm;
		this.mRouteIsTemp = pRouteIsTemp;
	}

	/**
	 * Save a route in the database if you have Internet, otherwise in a Json
	 * File
	 */
	public void saveRoute() {
		/** if internet then store this in the database */
		if (Internet.isNetworkAvailable(LoginActivity.getAppContext())) {
			HTIDatabaseConnection lHtiDbConnection = HTIDatabaseConnection
					.getInstance();
			if (lHtiDbConnection.getRoute(this.mRouteId) == null) {
				String errors = lHtiDbConnection.addRoute(this);
				if (errors != null) {
					Log.w(LogTag.WRITEDB,
							"Errors during add route in the database : \n"
									+ errors);
				}
			} else {
				Log.w(LogTag.WRITEDB, "The route with the id (" + this.mRouteId
						+ ") already exist in the database.");
			}
		}
		/** else store this in a JSON file (FILENAMEROUTE) */
		else {
			JsonManager.addRoutesInJson(MainActivity.FILENAMEROUTE, this,
					LoginActivity.getAppContext());
		}
	}

	/**
	 * Method Override in order to implement Parcelable This allows to send
	 * arguments from an Activity to another
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pDest, int pFlags) {
		/** Transform List<BasicDBObject> in List<Waypoint> */
		mRoutePointsTemp = new ArrayList<WaypointMAP>();
		for (BasicDBObject lDbObject : mRoutePoints) {
			mRoutePointsTemp.add(new WaypointMAP(Double.parseDouble(lDbObject
					.get("waypointLat").toString()), Double
					.parseDouble(lDbObject.get("waypointLng").toString())));
		}
		/** WriteToParcel */
		pDest.writeInt(this.mRouteId);
		// dest.writeFloat(this.routeKm);
		pDest.writeList(this.mRoutePointsTemp);
	}

	public static final Parcelable.Creator<Route> CREATOR = new Creator<Route>() {
		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}

		@Override
		public Route createFromParcel(Parcel source) {
			return new Route(source);
		}
	};

	public Route(Parcel pIn) {
		this.mRouteId = pIn.readInt();
		// this.routeKm = in.readFloat();
		this.mRoutePointsTemp = new ArrayList<WaypointMAP>();
		pIn.readList(this.mRoutePointsTemp, Route.class.getClassLoader());
	}

	/**
	 * Get the routeId
	 * 
	 * @return routeId
	 */
	public int getRouteId() {
		return this.mRouteId;
	}

	/**
	 * Get the list of route points to insert/get in the database
	 * 
	 * @return list of waypoints
	 */
	public List<BasicDBObject> getRoutePoints() {
		return this.mRoutePoints;
	}

	/**
	 * Set the routePoints to insert/get to the database
	 * 
	 * @param pRoutePoints
	 */
	public void setRoutePoints(List<BasicDBObject> pRoutePoints) {
		this.mRoutePoints = pRoutePoints;
	}

	/**
	 * Get the routeKm
	 * 
	 * @return routeKm
	 */
	public float getRouteKm() {
		return this.mRouteKm;
	}

	/**
	 * In order to know if the route is a temporary route
	 * 
	 * @return temp
	 */
	public boolean isRouteIsTemp() {
		return mRouteIsTemp;
	}

	/**
	 * To change the temporary field of a route
	 * 
	 * @param pRouteIsTemp
	 */
	public void setRouteIsTemp(boolean pRouteIsTemp) {
		this.mRouteIsTemp = pRouteIsTemp;
	}

	/**
	 * Return the list of waypoints to insert in the googleMap
	 * 
	 * @return list of waypoints
	 */
	public List<WaypointMAP> getRoutePointsTemp() {
		return mRoutePointsTemp;
	}

}
