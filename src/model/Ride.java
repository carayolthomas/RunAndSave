package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;
import android.content.Context;

import com.hti.LoginActivity;
import com.hti.MainActivity;

public class Ride {
	private int rideId;
	private int rideRouteId;
	private int rideUserId;
	private double rideCalories;
	private double rideDuration;
	private Date rideStartTimestamp;
	private Date rideStopTimestamp;
	private String rideDate;

	
	public Ride(int rideId, int rideRouteId, int rideUserId,
			double rideCalories, double rideDuration, Date rideStartTimestamp,
			Date rideStopTimestamp) {
		super();
		this.rideId = rideId;
		this.rideRouteId = rideRouteId;
		this.rideUserId = rideUserId;
		this.rideCalories = rideCalories;
		this.rideDuration = rideDuration;
		this.rideStartTimestamp = rideStartTimestamp;
		this.rideStopTimestamp = rideStopTimestamp;
		
	}

	public Ride(int rideId, int rideRouteId, double rideCalories,
			Date rideStartTimestamp, Date rideStopTimestamp) {
		super();
		this.rideId = rideId;
		this.rideRouteId = rideRouteId;
		this.rideCalories = rideCalories;
		this.rideStartTimestamp = rideStartTimestamp;
		this.rideStopTimestamp = rideStopTimestamp;
	}
	
	

	public Ride(int rideId, int rideRouteId, int rideUserId,
			double rideCalories, double rideDuration, String rideDate) {
		super();
		this.rideId = rideId;
		this.rideRouteId = rideRouteId;
		this.rideUserId = rideUserId;
		this.rideCalories = rideCalories;
		this.rideDuration = rideDuration;
		this.rideDate = rideDate;
	}

	public int addRideToQueue() {
		return 0;
	}

	public void computeRide() {
		// calculate the duration & calories
		if (rideStartTimestamp != null && rideStopTimestamp != null) {
			long duration = (rideStopTimestamp.getTime() - rideStartTimestamp
					.getTime()) / 60000;
			float weight = MainActivity.userConnected.getUserWeight();
			rideCalories = (double) (weight * 2.204) * duration * 0.1;
			rideDuration = duration;
			Calendar calendar = Calendar.getInstance();
			rideDate = new SimpleDateFormat("yyyy-MM-dd",new Locale("FR","fr")).format(calendar.getTime());
		}
	}

	public void saveRide() {
		// if internet then store this in the database
		if (Internet.isNetworkAvailable(LoginActivity.getAppContext())) {
			HTIDatabaseConnection htiDbConnection = HTIDatabaseConnection
					.getInstance();
			htiDbConnection.addRide(this);
		}
		// else store this in a JSON file (FILENAMERIDE)
		else {
			JsonManager.addRideInJson(MainActivity.FILENAMERIDE, this,
					LoginActivity.getAppContext());
		}
	}

	public int getRideRouteId() {
		return rideRouteId;
	}

	public int getRideId() {
		return this.rideId;
	}

	public void setRideId(int rideId) {
		this.rideId = rideId;
	}

	public int getRouteId() {
		return this.rideRouteId;
	}

	public int getRideUserId() {
		return rideUserId;
	}

	public double getRideDuration() {
		return rideDuration;
	}

	public void setRouteId(int rideRouteId) {
		this.rideRouteId = rideRouteId;
	}

	public double getRideCalories() {
		return this.rideCalories;
	}

	public Date getRideStartTimestamp() {
		return rideStartTimestamp;
	}

	public void setRideStartTimestamp(Date rideStartTimestamp) {
		this.rideStartTimestamp = rideStartTimestamp;
	}

	public Date getRideStopTimestamp() {
		return rideStopTimestamp;
	}

	public void setRideStopTimestamp(Date rideStopTimestamp) {
		this.rideStopTimestamp = rideStopTimestamp;
	}
	
	public String getRideDate() {
		return rideDate;
	}

	public void setRideDate(String rideDate) {
		this.rideDate = rideDate;
	}
	
	public String toString() {
		return "Ride n°" + this.rideId + " : " + (int)this.rideDuration + "min / " + 
				(int)this.rideCalories + " cal. (" + this.rideDate + ")";   
	}

}
