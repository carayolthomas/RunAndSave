package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;

/**
 * This class allows me to store all the waypoints into the database
 * 
 * @author hti
 * 
 */
public class WaypointDB extends BasicDBObject implements Parcelable {

	private static final long serialVersionUID = 1L;

	/** Latitude of a waypoint */
	@SerializedName("waypointLat")
	private Double mWaypointLat;

	/** Longitude of a waypoint */
	@SerializedName("waypointLng")
	private Double mWaypointLng;

	/** Default constructor */
	public WaypointDB() {
		super();
	}

	/**
	 * Constructor of a Waypoint with Longitude & Latitude
	 * 
	 * @param pWaypointLat
	 * @param pWaypointLng
	 */
	public WaypointDB(Double pWaypointLat, Double pWaypointLng) {
		super();
		this.mWaypointLat = pWaypointLat;
		this.mWaypointLng = pWaypointLng;
	}

	/**
	 * Get the latitude of the waypoint
	 * 
	 * @return latitude
	 */
	public Double getWaypointLat() {
		return mWaypointLat;
	}

	/**
	 * Get the longitude of a waypoint
	 * 
	 * @return longitude
	 */
	public Double getWaypointLng() {
		return mWaypointLng;
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
		pDest.writeDouble(this.mWaypointLat);
		pDest.writeDouble(this.mWaypointLng);
	}

	public static final Parcelable.Creator<WaypointDB> CREATOR = new Creator<WaypointDB>() {

		@Override
		public WaypointDB[] newArray(int size) {
			return new WaypointDB[size];
		}

		@Override
		public WaypointDB createFromParcel(Parcel source) {
			return new WaypointDB(source);
		}
	};

	public WaypointDB(Parcel pIn) {
		this.mWaypointLat = pIn.readDouble();
		this.mWaypointLng = pIn.readDouble();
	}

}
