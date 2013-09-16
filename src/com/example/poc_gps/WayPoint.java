package com.example.poc_gps;
import com.google.gson.annotations.SerializedName;

public class WayPoint {
	@SerializedName("lat")
	public Double lat;
	
	@SerializedName("lng")
	public Double lng;
	
	@SerializedName("timestamp")
	public Long timestamp;
	
	public WayPoint (Double lat, Double lng, Long timestamp) {
		this.lat = lat;
		this.lng = lng;
		this.timestamp = timestamp;
	}
}
