package utils;

import model.*;

import com.google.gson.annotations.SerializedName;

/**
 * This class allows me to get a route from a JSON file
 * 
 * @author hti
 * 
 */
public class SearchJsonResultRoute {
	@SerializedName("route")
	public Route mRoute;

	/**
	 * Default constructor
	 * 
	 * @param pRoute
	 */
	public SearchJsonResultRoute(Route pRoute) {
		this.mRoute = pRoute;
	}
}
