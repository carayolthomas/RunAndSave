package utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Internet {
	public static boolean isNetworkAvailable(Context pApplicationContext) {
		ConnectivityManager cm = (ConnectivityManager) pApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
}
