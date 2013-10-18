package utils;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import model.Ride;
import model.Route;
import model.User;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class HTIDatabaseConnection {

	public static final String ROUTECOLL = "Routes";
	public static final String RIDECOLL = "Rides";
	public static final String USERCOLL = "Users";
	public static final String CRYPTALGO = "SHA-1" ;
	
	private String databaseUsername;
	private String databasePassword;
	private String databaseHote;
	private String databaseName;
	//private Jongo jongo;
	private static DB databaseInst;
	
	private static HTIDatabaseConnection me = null;
	
	public static HTIDatabaseConnection getInstance() {
		if(HTIDatabaseConnection.me == null) {
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
		String uri = "mongodb://" + this.databaseUsername + ":" + this.databasePassword + "@" + this.databaseHote + "/" + this.databaseName;
		MongoClientURI mongoClientURI = new MongoClientURI(uri);
		MongoClient mongoClient;
		DB db = null ;
		try {
			mongoClient = new MongoClient(mongoClientURI);
			db = mongoClient.getDB(this.databaseName);	
			if(db.authenticate(this.databaseUsername, this.databasePassword.toCharArray())) {
				Log.i("Auth", "OK");
				this.databaseInst = db ;
				return true;
			} else {
				Log.i("Auth", "NOK");
				return false;
			}
			//jongo = new Jongo(db);
		} catch (UnknownHostException e) {
			//TODO LOG
			e.printStackTrace();
		}
		return false;
	}
	
	public String insertUser(User pUser) {
		//MongoCollection userCollection = jongo.getCollection(USERCOLL);
		//return userCollection.insert(pUser).getError();
		return null;
	}
	
	public String updateRideRouteId(Ride pRide, int newRouteId) {
		//MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		//return rideCollection.update("{rideId: #}",pRide.getRideId()).with("{$set: {rideRouteId: #}}", newRouteId).getError();
		return null;
	}
	
	/**
	 * Return the route corresponding to the given id
	 * @param pRouteId
	 * @return
	 */
	public Route getRoute(int pRouteId) {
		//MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		//return routeCollection.findOne("{routeId:#}", pRouteId).as(Route.class);
		return null;
	}
	
	/**
	 * Return the ride corresponding to the given id
	 * @param pRideId
	 * @return
	 */
	public Ride getRide(int pRideId) {
		//MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		//return rideCollection.findOne("{rideId:#}", pRideId).as(Ride.class);
		return null;
	}
	
	/**
	 * Add a route in the DB
	 * @param pRoute
	 */
	public String addRoute(Route pRoute) {
		//MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		//return routeCollection.insert(pRoute).getError();
		return null;
	}
	
	/**
	 * Add a ride in the DB
	 * @param pRide
	 * @param pUserId
	 */
	public String addRide(Ride pRide) {
		//MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		//return rideCollection.insert(pRide).getError();
		return null;
	}
	
	/**
	 * Get all the rides of an user
	 * @param pUserId
	 * @return
	 */
	public Iterable<Ride> getAllUserRides(int pUserId) {
		//MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		//return rideCollection.find("{rideUserId:#}",pUserId).as(Ride.class);
		return null;
	}
	
	/**
	 * Get a user 
	 * @param username
	 * @param clearPassword
	 * @return
	 */
	public User getUser(String username, String clearPassword) {
		DBCollection userCollection = databaseInst.getCollection(USERCOLL);
		DBObject userObject = userCollection.findOne(new BasicDBObject("email", username));
		try {
			// The user doesn't exist
			if(userObject == null) {
				return new User(username, "", 0);
			} else {
				// The email already exists, we suppose that he put a wrong password
				if(!Encode.encode(clearPassword, CRYPTALGO).equalsIgnoreCase(userObject.get("password").toString())) {
					return null; 
				} 
				// The user has been found in the database
				else {
					return new User(userObject.get("email").toString(),
					        		userObject.get("password").toString(),
					        		Integer.parseInt(userObject.get("weight").toString()));
				}
			} 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
