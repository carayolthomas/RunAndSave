package utils;

import java.util.List;
import model.*;

import com.google.gson.annotations.SerializedName;

public class SearchJsonResultRides {	
	@SerializedName("rides")
	public List<Ride> rides;
	
	public SearchJsonResultRides(List<Ride> pRides) {
		this.rides = pRides;
	}
}
