package utils;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Ride;
import model.Route;
import model.User;
import model.Waypoint;
import android.util.Log;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class HTIDatabaseConnection {

	public static final String ROUTECOLL = "Routes";
	public static final String RIDECOLL = "Rides";
	public static final String USERCOLL = "Users";

	public static final String CRYPTALGO = "SHA-1";

	private String databaseUsername;
	private String databasePassword;
	private String databaseHote;
	private String databaseName;
	// private Jongo jongo;
	private static DB databaseInst;

	private static HTIDatabaseConnection me = null;

	public static HTIDatabaseConnection getInstance() {
		if (HTIDatabaseConnection.me == null) {
			HTIDatabaseConnection.me = new HTIDatabaseConnection();
		}
		return HTIDatabaseConnection.me;
	}

	private HTIDatabaseConnection() {
		super();
		this.databaseUsername = "thomascarayol";
		this.databasePassword = "azerty";
		this.databaseHote = "mongodb1.alwaysdata.com";
		this.databaseName = "thomascarayol_hti";
	}

	public boolean doConnect() {
		String uri = "mongodb://" + this.databaseUsername + ":"
				+ this.databasePassword + "@" + this.databaseHote + "/"
				+ this.databaseName;
		MongoClientURI mongoClientURI = new MongoClientURI(uri);
		MongoClient mongoClient;
		DB db = null;
		try {
			mongoClient = new MongoClient(mongoClientURI);
			db = mongoClient.getDB(this.databaseName);
			if (db.authenticate(this.databaseUsername,
					this.databasePassword.toCharArray())) {
				Log.i("Auth", "OK");
				this.databaseInst = db;
				return true;
			} else {
				Log.i("Auth", "NOK");
				return false;
			}
			// jongo = new Jongo(db);
		} catch (UnknownHostException e) {
			// TODO LOG
			e.printStackTrace();
		}
		return false;
	}

	public String insertUser(User pUser) {
		// MongoCollection userCollection = jongo.getCollection(USERCOLL);
		DBCollection userCollection = databaseInst.getCollection(USERCOLL);

		BasicDBObject newUser;
		try {
			newUser = new BasicDBObject("userEmail",
					pUser.getUserEmail()).append(
					"userPassword",
					Encode.encode(pUser.getUserPassword(),
							HTIDatabaseConnection.CRYPTALGO)).append("userWeight",
					pUser.getUserWeight());
			return userCollection.insert(newUser).getError();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// return userCollection.insert(pUser).getError();
	}

	public String updateRideRouteId(Ride pRide, int newRouteId) {
		// MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		// return
		// rideCollection.update("{rideId: #}",pRide.getRideId()).with("{$set: {rideRouteId: #}}",
		// newRouteId).getError();
		DBCollection rideCollection = databaseInst.getCollection(RIDECOLL);
		return rideCollection.update(
				new BasicDBObject("rideId", pRide.getRideId()),
				new BasicDBObject("rideRouteId", newRouteId)).getError();
	}

	/**
	 * Return the route corresponding to the given id
	 * 
	 * @param pRouteId
	 * @return
	 */
	public Route getRoute(int pRouteId) {
		// MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		// return routeCollection.findOne("{routeId:#}",
		// pRouteId).as(Route.class);
		Route route = null;
		DBCollection routeCollection = databaseInst.getCollection(ROUTECOLL);
		DBObject routeObject = routeCollection.findOne(new BasicDBObject(
				"routeId", pRouteId));
		if(routeObject != null) {
			route = new Route(
					Integer.parseInt((String) routeObject.get("routeId")),
					(ArrayList<Waypoint>) routeObject.get("routePoints"),
					Float.parseFloat((String) routeObject.get("routeKm")),
					Boolean.parseBoolean((String) routeObject.get(("routeIsTemp"))));
		}
		return route;
	}

	/**
	 * Return the ride corresponding to the given id
	 * 
	 * @param pRideId
	 * @return
	 */
	public Ride getRide(int pRideId) {
		// MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		// return rideCollection.findOne("{rideId:#}", pRideId).as(Ride.class);
		/*
		 * private double rideCalories; private double rideDuration; private
		 * Date rideStartTimestamp; private Date rideStopTimestamp
		 */
		DBCollection routeCollection = databaseInst.getCollection(RIDECOLL);
		DBObject rideObject = routeCollection.findOne(new BasicDBObject(
				"routeId", pRideId));
		Ride ride = new Ride(
				Integer.parseInt((String) rideObject.get("rideId")),
				Integer.parseInt((String) rideObject.get("rideRouteId")),
				Integer.parseInt((String) rideObject.get("rideUserId")),
				Double.parseDouble((String) rideObject.get("rideCalories")),
				Double.parseDouble((String) rideObject.get("rideDuration")),
				(Date) rideObject.get("rideStartTimestamp"),
				(Date) rideObject.get("rideStopTimestamp"));

		return ride;
	}

	/**
	 * Add a route in the DB
	 * 
	 * @param pRoute
	 */
	public String addRoute(Route pRoute) {
		// MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		// return routeCollection.insert(pRoute).getError();
		DBCollection routeCollection = databaseInst.getCollection(ROUTECOLL);
		
		BasicDBObject newRoute = new BasicDBObject("routeId", pRoute.getRouteId()).
											append("routePoints", pRoute.getRoutePoints()).
											append("routeKm", pRoute.getRouteKm());

		return routeCollection.insert(newRoute).getError();
	}

	/**
	 * Add a ride in the DB
	 * 
	 * @param pRide
	 * @param pUserId
	 */
	public String addRide(Ride pRide) {
		// MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		// return rideCollection.insert(pRide).getError();
		DBCollection rideCollection = databaseInst.getCollection(RIDECOLL);

		BasicDBObject newRide = new BasicDBObject("rideId", pRide.getRideId())
				.append("rideRouteId", pRide.getRideRouteId())
				.append("rideCalories", pRide.getRideCalories())
				.append("rideDuration", pRide.getRideDuration())
				.append("rideStartTimestamp", pRide.getRideStartTimestamp())
				.append("rideStopTimestamp", pRide.getRideStopTimestamp())
				.append("rideUserId", pRide.getRideUserId());
		return rideCollection.insert(newRide).getError();
	}

	/**
	 * Get all the rides of an user
	 * 
	 * @param pUserId
	 * @return
	 */
	public List<Ride> getAllUserRides() {
		// MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		// return rideCollection.find("{rideUserId:#}",pUserId).as(Ride.class);
		DBCollection userCollection = databaseInst.getCollection(RIDECOLL);
		DBCursor cursor = userCollection.find();
		ArrayList<Ride> userRides = new ArrayList<Ride>();
		try {
			while (cursor.hasNext()) {
				DBObject next = cursor.next();
				String id = String.valueOf(next.get("rideId"));
				userRides.add(new Ride(Integer.parseInt(id), 0, 0., new Date(), new Date()));
			}
		} finally {
			cursor.close();
		}

		return userRides;
	}

	/**
	 * Get a user
	 * 
	 * @param username
	 * @param clearPassword
	 * @return
	 */
	public User getUser(String username, String clearPassword) {
		DBCollection userCollection = databaseInst.getCollection(USERCOLL);
		DBObject userObject = userCollection.findOne(new BasicDBObject("userEmail",
				username));
		try {
			// The user doesn't exist
			if (userObject == null) {
				return new User(username, "", 0);
			} else {
				// The email already exists, we suppose that he put a wrong
				// password
				if (!Encode.encode(clearPassword, CRYPTALGO).equalsIgnoreCase(
						userObject.get("userPassword").toString())) {
					return null;
				}
				// The user has been found in the database
				else {
					return new User(userObject.get("userEmail").toString(),
							userObject.get("userPassword").toString(),
							Float.parseFloat(userObject.get("userWeight")
									.toString()));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getNumberOfRoutes() {
		DBCollection routeCollection = databaseInst.getCollection(ROUTECOLL);
		return routeCollection.find().count();
	}
	
	public int getNumberOfRides() {
		DBCollection rideCollection = databaseInst.getCollection(RIDECOLL);
		return rideCollection.find().count();
	}
}
