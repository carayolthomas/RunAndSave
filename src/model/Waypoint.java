package model;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;

public class Waypoint extends BasicDBObject implements Parcelable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("waypointLat")
	private Double waypointLat;
	
	@SerializedName("waypointLng")
	private Double waypointLng;
	
	public Waypoint() {
		super();
	}
	
	public Waypoint(Double waypointLat, Double waypointLng) {
		super();
		this.waypointLat = waypointLat;
		this.waypointLng = waypointLng;
	}

	public Double getWaypointLat() {
		return waypointLat;
	}

	public Double getWaypointLng() {
		return waypointLng;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(this.waypointLat);
		dest.writeDouble(this.waypointLng);
	}
	
	public static final Parcelable.Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
			
		@Override
		public Waypoint[] newArray(int size) {
			return new Waypoint[size];
		}
		
		@Override
		public Waypoint createFromParcel(Parcel source) {
			return new Waypoint(source);
		}
	};
	
	public Waypoint (Parcel in) {
		this.waypointLat = in.readDouble();
		this.waypointLng = in.readDouble();
	}

}
