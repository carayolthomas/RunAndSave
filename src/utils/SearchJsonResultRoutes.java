package utils;

import java.util.List;
import model.*;

import com.google.gson.annotations.SerializedName;

/**
 * This class allows me to get a list of rutes from a JSON file
 * 
 * @author hti
 * 
 */
public class SearchJsonResultRoutes {
	@SerializedName("routes")
	public List<Route> pRoutes;

	public SearchJsonResultRoutes(List<Route> pRoutes) {
		this.pRoutes = pRoutes;
	}
}
