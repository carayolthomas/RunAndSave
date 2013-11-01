package utils;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import logs.LogTag;
import model.Ride;
import model.Route;
import model.User;
import android.util.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * This class provides all the methods to access to the database
 * @author hti
 *
 */
public class HTIDatabaseConnection {

	/** Constant for the Route collection */
	public static final String ROUTECOLL = "Routes";
	
	/** Constant for the Ride collection */
	public static final String RIDECOLL = "Rides";
	
	/** Constant for the User collection */
	public static final String USERCOLL = "Users";

	/** Constant for the User password protection */
	public static final String CRYPTALGO = "SHA-1";

	/** Database username */
	private String mDatabaseUsername;
	
	/** Database password */
	private String mDatabasePassword;
	
	/** Database host */
	private String mDatabaseHost;
	
	/** Database name */
	private String mDatabaseName;
	
	/** Database instance */
	private static DB mDatabaseInst;

	/** For the singleton pattern */
	private static HTIDatabaseConnection me = null;

	/**
	 * GesInstance for the singleton pattern
	 * @return HTIDatabaseConnection
	 */
	public static HTIDatabaseConnection getInstance() {
		if (HTIDatabaseConnection.me == null) {
			HTIDatabaseConnection.me = new HTIDatabaseConnection();
		}
		return HTIDatabaseConnection.me;
	}

	/**
	 * Default constructor
	 */
	private HTIDatabaseConnection() {
		super();
		this.mDatabaseUsername = "thomascarayol";
		this.mDatabasePassword = "azerty";
		this.mDatabaseHost = "mongodb1.alwaysdata.com";
		this.mDatabaseName = "thomascarayol_hti";
	}

	/**
	 * Establish the connection to the database
	 * @return boolean
	 */
	public boolean doConnect() {
		String lUri = "mongodb://" + this.mDatabaseUsername + ":"
				+ this.mDatabasePassword + "@" + this.mDatabaseHost + "/"
				+ this.mDatabaseName;
		MongoClientURI lMongoClientURI = new MongoClientURI(lUri);
		MongoClient lMongoClient;
		DB lDb = null;
		try {
			lMongoClient = new MongoClient(lMongoClientURI);
			lDb = lMongoClient.getDB(this.mDatabaseName);
			if (lDb.authenticate(this.mDatabaseUsername,
					this.mDatabasePassword.toCharArray())) {
				Log.i(LogTag.AUTHENTIFICATION, "OK");
				mDatabaseInst = lDb;
				return true;
			} else {
				Log.i(LogTag.AUTHENTIFICATION, "NOK");
				return false;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Insert a user in the database
	 * @param pUser
	 * @return errors
	 */
	public String insertUser(User pUser) {
		DBCollection lUserCollection = mDatabaseInst.getCollection(USERCOLL);
		BasicDBObject lNewUser;
		try {
			lNewUser = new BasicDBObject("userEmail",
					pUser.getUserEmail()).append(
					"userPassword",
					Encode.encode(pUser.getUserPassword(),
							HTIDatabaseConnection.CRYPTALGO)).append("userWeight",
					pUser.getUserWeight());
			return lUserCollection.insert(lNewUser).getError();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the routeId of a ride
	 * @param pRide
	 * @param newRouteId
	 * @return errors
	 */
	public String updateRideRouteId(Ride pRide, int newRouteId) {
		DBCollection lRideCollection = mDatabaseInst.getCollection(RIDECOLL);
		return lRideCollection.update(
				new BasicDBObject("rideId", pRide.getRideId()),
				new BasicDBObject("rideRouteId", newRouteId)).getError();
	}

	/**
	 * Return the route corresponding to the given id
	 * 
	 * @param pRouteId
	 * @return route matched
	 */
	@SuppressWarnings("unchecked")
	public Route getRoute(int pRouteId) {
		Route lRoute = null;
		DBCollection lRouteCollection = mDatabaseInst.getCollection(ROUTECOLL);
		DBObject lRouteObject = lRouteCollection.findOne(new BasicDBObject(
				"routeId", pRouteId));
		if(lRouteObject != null) {
			lRoute = new Route(
					Integer.parseInt(String.valueOf(lRouteObject.get("routeId"))),
					(ArrayList<BasicDBObject>) lRouteObject.get("routePoints"),
					Float.parseFloat(String.valueOf(lRouteObject.get("routeKm"))),
					Boolean.parseBoolean(String.valueOf(lRouteObject.get(("routeIsTemp")))));
		}
		return lRoute;
	}

	/**
	 * Return the ride corresponding to the given id
	 * 
	 * @param pRideId
	 * @return ride matched
	 */
	public Ride getRide(int pRideId) {
		DBCollection lRouteCollection = mDatabaseInst.getCollection(RIDECOLL);
		DBObject lRideObject = lRouteCollection.findOne(new BasicDBObject(
				"rideId", pRideId));
		Ride lRide = new Ride(
				Integer.parseInt(String.valueOf(lRideObject.get("rideId"))),
				Integer.parseInt(String.valueOf(lRideObject.get("rideUserId"))),
				Integer.parseInt(String.valueOf(lRideObject.get("rideRouteId"))),
				Double.parseDouble(String.valueOf(lRideObject.get("rideCalories"))),
				Double.parseDouble(String.valueOf(lRideObject.get("rideDuration"))),
				String.valueOf(lRideObject.get("rideDate")));

		return lRide;
	}

	/**
	 * Add a route in the DB
	 * 
	 * @param pRoute
	 */
	public String addRoute(Route pRoute) {
		DBCollection lRouteCollection = mDatabaseInst.getCollection(ROUTECOLL);
		BasicDBObject lNewRoute = new BasicDBObject("routeId", pRoute.getRouteId()).
											append("routePoints", pRoute.getRoutePoints()).
											append("routeKm", pRoute.getRouteKm());

		return lRouteCollection.insert(lNewRoute).getError();
	}

	/**
	 * Add a ride in the DB
	 * 
	 * @param pRide
	 * @param pUserId
	 */
	public String addRide(Ride pRide) {
		DBCollection lRideCollection = mDatabaseInst.getCollection(RIDECOLL);
		BasicDBObject lNewRide = new BasicDBObject("rideId", pRide.getRideId())
				.append("rideRouteId", pRide.getRideRouteId())
				.append("rideCalories", pRide.getRideCalories())
				.append("rideDuration", pRide.getRideDuration())
				.append("rideStartTimestamp", pRide.getRideStartTimestamp())
				.append("rideStopTimestamp", pRide.getRideStopTimestamp())
				.append("rideUserId", pRide.getRideUserId())
				.append("rideDate", pRide.getRideDate().toString());
		return lRideCollection.insert(lNewRide).getError();
	}

	/**
	 * Get all the rides of an user
	 * 
	 * @param pUserId
	 * @return list of rides
	 */
	public List<Ride> getAllUserRides() {
		DBCollection lUserCollection = mDatabaseInst.getCollection(RIDECOLL);
		DBCursor lCursor = lUserCollection.find();
		ArrayList<Ride> lUserRides = new ArrayList<Ride>();
		try {
			while (lCursor.hasNext()) {
				DBObject next = lCursor.next();
				String rideId = String.valueOf(next.get("rideId"));
				String rideRouteId = String.valueOf(next.get("rideRouteId"));
				String rideCalories = String.valueOf(next.get("rideCalories"));
				String rideDuration = String.valueOf(next.get("rideDuration"));
				String rideDate = String.valueOf(next.get("rideDate"));
				lUserRides.add(new Ride(Integer.parseInt(rideId), Integer.parseInt(rideRouteId), 0, Double.parseDouble(rideCalories), Double.parseDouble(rideDuration), rideDate));
			}
		} finally {
			lCursor.close();
		}

		return lUserRides;
	}

	/**
	 * Get a user
	 * 
	 * @param username
	 * @param clearPassword
	 * @return user
	 */
	public User getUser(String username, String clearPassword) {
		DBCollection lUserCollection = mDatabaseInst.getCollection(USERCOLL);
		DBObject lUserObject = lUserCollection.findOne(new BasicDBObject("userEmail",
				username));
		try {
			/** The user doesn't exist */
			if (lUserObject == null) {
				return new User(username, "", 0);
			} else {
				/** The email already exists, we suppose that he put a wrong password */
				if (!Encode.encode(clearPassword, CRYPTALGO).equalsIgnoreCase(
						lUserObject.get("userPassword").toString())) {
					return null;
				}
				/** The user has been found in the database */
				else {
					return new User(lUserObject.get("userEmail").toString(),
							lUserObject.get("userPassword").toString(),
							Float.parseFloat(lUserObject.get("userWeight")
									.toString()));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the number of routes
	 * @return nbRoutes
	 */
	public int getNumberOfRoutes() {
		DBCollection lRouteCollection = mDatabaseInst.getCollection(ROUTECOLL);
		return lRouteCollection.find().count();
	}
	
	/**
	 * Get the number of rides
	 * @return nbRides
	 */
	public int getNumberOfRides() {
		DBCollection lRideCollection = mDatabaseInst.getCollection(RIDECOLL);
		return lRideCollection.find().count();
	}
}
