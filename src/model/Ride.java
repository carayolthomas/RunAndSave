package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utils.HTIDatabaseConnection;
import utils.Internet;
import utils.JsonManager;

import com.hti.LoginActivity;
import com.hti.MainActivity;

/**
 * Ride class for describing what is a Ride and store all the information
 * concerning a Ride like Calories & TimeStamp
 * 
 * @author hti
 */
public class Ride {

	/** The id of a ride */
	private int mRideId;

	/** The id of the route associated to the ride */
	private int mRideRouteId;

	/** The id of the user associated to the ride */
	private int mRideUserId;

	/** Number of calories burnt during this ride */
	private double mRideCalories;

	/** The duration of the ride */
	private double mRideDuration;

	/** Ride start time */
	private Date mRideStartTimestamp;

	/** Ride stop time */
	private Date mRideStopTimestamp;

	/** Ride date */
	private String mRideDate;

	/**
	 * Constructor used when the user end the ride
	 * 
	 * @param pRideId
	 * @param pRideRouteId
	 * @param pRideCalories
	 * @param pRideStartTimestamp
	 * @param pRideStopTimestamp
	 */
	public Ride(int pRideId, int pRideRouteId, double pRideCalories,
			Date pRideStartTimestamp, Date pRideStopTimestamp) {
		super();
		this.mRideId = pRideId;
		this.mRideRouteId = pRideRouteId;
		this.mRideCalories = pRideCalories;
		this.mRideStartTimestamp = pRideStartTimestamp;
		this.mRideStopTimestamp = pRideStopTimestamp;
	}

	/**
	 * Constructor used for the communication with the database
	 * 
	 * @param pRideId
	 * @param pRideUserId
	 * @param pRideRouteId
	 * @param pRideCalories
	 * @param pRideDuration
	 * @param pRideDate
	 */
	public Ride(int pRideId, int pRideUserId, int pRideRouteId,
			double pRideCalories, double pRideDuration, String pRideDate) {
		super();
		this.mRideId = pRideId;
		this.mRideRouteId = pRideRouteId;
		this.mRideUserId = pRideUserId;
		this.mRideCalories = pRideCalories;
		this.mRideDuration = pRideDuration;
		this.mRideDate = pRideDate;
	}

	/**
	 * Compute the Duration, the calories burnt and the date for a ride
	 */
	public void computeRide() {
		if (mRideStartTimestamp != null && mRideStopTimestamp != null) {
			long lDuration = (mRideStopTimestamp.getTime() - mRideStartTimestamp
					.getTime()) / 60000;
			float lWeight = MainActivity.userConnected.getUserWeight();
			mRideCalories = (double) (lWeight * 2.204) * lDuration * 0.1;
			mRideDuration = lDuration;
			Calendar lCalendar = Calendar.getInstance();
			mRideDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("FR",
					"fr")).format(lCalendar.getTime());
		}
	}

	/**
	 * Save a ride in the database if you have Internet, otherwise in a Json
	 * File
	 */
	public void saveRide() {
		/** if internet then store this in the database */
		if (Internet.isNetworkAvailable(LoginActivity.getAppContext())) {
			HTIDatabaseConnection lHtiDbConnection = HTIDatabaseConnection
					.getInstance();
			lHtiDbConnection.addRide(this);
		}
		/** else store this in a JSON file (FILENAMERIDE) */
		else {
			JsonManager.addRideInJson(MainActivity.FILENAMERIDE, this,
					LoginActivity.getAppContext());
		}
	}

	/**
	 * Return the routeId associated to the Ride
	 * 
	 * @return routeId
	 */
	public int getRideRouteId() {
		return this.mRideRouteId;
	}

	/**
	 * Return the rideId
	 * 
	 * @return rideId
	 */
	public int getRideId() {
		return this.mRideId;
	}

	/**
	 * Get the user associated with this ride
	 * 
	 * @return userId
	 */
	public int getRideUserId() {
		return mRideUserId;
	}

	/**
	 * Get the ride duration
	 * 
	 * @return rideDuration
	 */
	public double getRideDuration() {
		return mRideDuration;
	}

	/**
	 * Get calories burnt for this ride
	 * 
	 * @return calories burnt
	 */
	public double getRideCalories() {
		return this.mRideCalories;
	}

	/**
	 * Get the start date of the ride
	 * 
	 * @return startDate
	 */
	public Date getRideStartTimestamp() {
		return mRideStartTimestamp;
	}

	/**
	 * Get the stop date of the ride
	 * 
	 * @return stopDate
	 */
	public Date getRideStopTimestamp() {
		return mRideStopTimestamp;
	}

	/**
	 * Get the ride date
	 * 
	 * @return rideDate
	 */
	public String getRideDate() {
		return mRideDate;
	}

	/**
	 * Return the String that will be displayed in the rides list
	 * 
	 * @return displayMessage
	 */
	public String toString() {
		return "Ride n°" + this.mRideId + " : " + (int) this.mRideDuration
				+ "min / " + (int) this.mRideCalories + " cal. ("
				+ this.mRideDate + ")";
	}

}
