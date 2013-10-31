package model;

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

public class Route implements Parcelable{
	
	@SerializedName("routeId")
	private int routeId;
	
	@SerializedName("routePoints")
	private List<BasicDBObject> routePoints;
	
	@SerializedName("routeKm")
	private float routeKm;
	
	@SerializedName("routeIsTemp")
	private boolean routeIsTemp;

	public Route(int routeId) {
		super();
		this.routeId = routeId;
	}

	public Route(int routeId, List<BasicDBObject> routePoints, float routeKm, boolean pRouteIsTemp) {
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

	public List<BasicDBObject> getRoutePoints() {
		return this.routePoints;
	}

	public void setRoutePoints(List<BasicDBObject> routePoints) {
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.routeId);
		dest.writeFloat(this.routeKm);
		//dest.writeTypedList(this.routePoints);
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
	
	public Route(Parcel in) {
		this.routeId = Integer.parseInt(in.readString());
		this.routeKm = Float.parseFloat(in.readString());
		//in.readTypedList(this.routePoints, Waypoint.CREATOR);
	}

}
