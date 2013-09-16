package com.example.poc_gps;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SearchJSONResult {
	@SerializedName("rides")
	public List<Ride> rides;
	
	@SerializedName("nbRides")
	public int nbRides;
	
	public SearchJSONResult(List<Ride> rides, int nbRides) {
		this.rides = rides;
		this.nbRides = nbRides;
	}
}
