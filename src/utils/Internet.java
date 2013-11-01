package utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Internet class
 * 
 * @author hti
 * 
 */
public class Internet {
	/**
	 * Return true if internet is available, else false
	 * 
	 * @param pApplicationContext
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context pApplicationContext) {
		ConnectivityManager cm = (ConnectivityManager) pApplicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
}
