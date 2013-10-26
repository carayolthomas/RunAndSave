package model;


import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;

public class Waypoint extends BasicDBObject{
	
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

}
