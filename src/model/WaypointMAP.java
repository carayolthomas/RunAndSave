package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * This class allows me to put a waypoint on a googleMap
 * 
 * @author hti
 * 
 */
public class WaypointMAP implements Parcelable {

	/** Waypoint latitude */
	@SerializedName("waypointLat")
	private Double mWaypointLat;

	/** Waypoint longitude */
	@SerializedName("waypointLng")
	private Double mWaypointLng;

	/**
	 * Default constructor
	 */
	public WaypointMAP() {
		super();
	}

	/**
	 * Constructor of a Waypoint with Longitude & Latitude
	 * 
	 * @param pWaypointLat
	 * @param pWaypointLng
	 */
	public WaypointMAP(Double pWaypointLat, Double pWaypointLng) {
		super();
		this.mWaypointLat = pWaypointLat;
		this.mWaypointLng = pWaypointLng;
	}

	/**
	 * Return waypoint latitude
	 * 
	 * @return latitude
	 */
	public Double getWaypointLat() {
		return mWaypointLat;
	}

	/**
	 * Return waypoint longitude
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
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(this.mWaypointLat);
		dest.writeDouble(this.mWaypointLng);
	}

	public static final Parcelable.Creator<WaypointMAP> CREATOR = new Creator<WaypointMAP>() {

		@Override
		public WaypointMAP[] newArray(int size) {
			return new WaypointMAP[size];
		}

		@Override
		public WaypointMAP createFromParcel(Parcel source) {
			return new WaypointMAP(source);
		}
	};

	public WaypointMAP(Parcel in) {
		this.mWaypointLat = in.readDouble();
		this.mWaypointLng = in.readDouble();
	}

}
