package model;

import java.util.Date;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;
import android.content.Context;

import com.hti.MainActivity;

public class Ride {
	private int rideId;
	private int rideRouteId;
	private int rideUserId;
	private double rideCalories;
	private double rideDuration;
	private Date rideStartTimestamp;
	private Date rideStopTimestamp;

	public Ride(int rideId, int rideRouteId, double rideCalories,
			Date rideStartTimestamp, Date rideStopTimestamp) {
		super();
		this.rideId = rideId;
		this.rideRouteId = rideRouteId;
		this.rideCalories = rideCalories;
		this.rideStartTimestamp = rideStartTimestamp;
		this.rideStopTimestamp = rideStopTimestamp;
	}

	public int addRideToQueue() {
		return 0;
	}

	public void computeRide(User userId) {
		// calculate the duration & calories
		if (rideStartTimestamp != null && rideStopTimestamp != null) {
			long duration = (rideStopTimestamp.getTime() - rideStartTimestamp
					.getTime()) / 60000;
			int weight = userId.getUserWeight();
			rideCalories = (double) (weight * 2.204) * duration * 0.1;
			rideDuration = duration;
		}
	}

	public void saveRide(int userId, Context pApplicationContext) {
		// if internet then store this in the database
		if (Internet.isNetworkAvailable(pApplicationContext)) {
			HTIDatabaseConnection htiDbConnection = HTIDatabaseConnection
					.getInstance();
			this.rideUserId = userId;
			htiDbConnection.addRide(this);
		}
		// else store this in a JSON file (FILENAMERIDE)
		else {
			JsonManager.addRideInJson(MainActivity.FILENAMERIDE, this,
					pApplicationContext);
		}
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

}
