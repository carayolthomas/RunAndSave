package utils;

import java.util.List;
import model.*;

import com.google.gson.annotations.SerializedName;

public class SearchJsonResultRoutes {	
	@SerializedName("routes")
	public List<Route> routes;
	
	public SearchJsonResultRoutes(List<Route> pRoutes) {
		this.routes = pRoutes;
	}
}
