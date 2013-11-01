package utils;

import java.util.List;
import model.*;

import com.google.gson.annotations.SerializedName;

/**
 * This class allows me to get a list of rides store in a JSON file
 * 
 * @author hti
 * 
 */
public class SearchJsonResultRides {
	@SerializedName("rides")
	public List<Ride> mRides;

	/**
	 * Default constructor
	 * 
	 * @param pRides
	 */
	public SearchJsonResultRides(List<Ride> pRides) {
		this.mRides = pRides;
	}
}
