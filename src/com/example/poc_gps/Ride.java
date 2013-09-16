package com.example.poc_gps;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Ride {
	@SerializedName("rideNumber")
	public int rideNumber;
	
	@SerializedName("total_wayPoints")
	public int nbWayPoints;
	
	@SerializedName("wayPoints")
	public List<WayPoint> wayPoints;
	
	public Ride (int ride, int totalWayPoints, List<WayPoint> rideWayPoints) {
		this.rideNumber = ride;
		this.nbWayPoints = totalWayPoints;
		this.wayPoints = rideWayPoints;
	}
}
