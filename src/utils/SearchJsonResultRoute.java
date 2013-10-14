package utils;

import model.*;

import com.google.gson.annotations.SerializedName;

public class SearchJsonResultRoute {	
	@SerializedName("route")
	public Route route;
	
	public SearchJsonResultRoute(Route pRoute) {
		this.route = pRoute;
	}
}
