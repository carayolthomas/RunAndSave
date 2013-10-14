package utils;

import java.net.UnknownHostException;

import model.Ride;
import model.Route;
import model.User;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class HTIDatabaseConnection {

	public static final String ROUTECOLL = "Routes";
	public static final String RIDECOLL = "Rides";
	public static final String USERCOLL = "Users";
	
	private String databaseUsername;
	private String databasePassword;
	private String databaseHote;
	private String databaseName;
	private Jongo jongo;
	private DB databaseInst;
	
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
		doConnect();
	}
	
	private void doConnect() {
		String uri = this.databaseUsername + ":" + this.databasePassword + "@" + this.databaseHote + "/" + this.databaseName;
		MongoClientURI mongoClientURI = new MongoClientURI(uri);
		MongoClient mongoClient;
		DB db = null ;
		try {
			mongoClient = new MongoClient(mongoClientURI);
			db = mongoClient.getDB(this.databaseName);	
			jongo = new Jongo(db);
		} catch (UnknownHostException e) {
			//TODO LOG
			e.printStackTrace();
		}
		this.databaseInst = db ;
	}
	
	//TODO Fonction d'ajout, suppression, etc...
	public String insertUser(User pUser) {
		MongoCollection userCollection = jongo.getCollection(USERCOLL);
		return userCollection.insert(pUser).getError();
	}
	
	public String updateRideRouteId(Ride pRide, int newRouteId) {
		MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		return rideCollection.update("{rideId: #}",pRide.getRideId()).with("{$set: {rideRouteId: #}}", newRouteId).getError();
	}
	
	/**
	 * Return the route corresponding to the given id
	 * @param pRouteId
	 * @return
	 */
	public Route getRoute(int pRouteId) {
		MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		return routeCollection.findOne("{routeId:#}", pRouteId).as(Route.class);
	}
	
	/**
	 * Return the ride corresponding to the given id
	 * @param pRideId
	 * @return
	 */
	public Ride getRide(int pRideId) {
		MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		return rideCollection.findOne("{rideId:#}", pRideId).as(Ride.class);
	}
	
	/**
	 * Add a route in the DB
	 * @param pRoute
	 */
	public String addRoute(Route pRoute) {
		MongoCollection routeCollection = jongo.getCollection(ROUTECOLL);
		return routeCollection.insert(pRoute).getError();
	}
	
	/**
	 * Add a ride in the DB
	 * @param pRide
	 * @param pUserId
	 */
	public String addRide(Ride pRide) {
		MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		return rideCollection.insert(pRide).getError();
	}
	
	/**
	 * Get all the rides of an user
	 * @param pUserId
	 * @return
	 */
	public Iterable<Ride> getAllUserRides(int pUserId) {
		MongoCollection rideCollection = jongo.getCollection(RIDECOLL);
		return rideCollection.find("{rideUserId:#}",pUserId).as(Ride.class);
	}
	
	public User getUser(String username, String encodedPassword) {
		MongoCollection userCollection = jongo.getCollection(USERCOLL);
		return userCollection.findOne("{userName: #, userPassword:#}", username, encodedPassword).as(User.class);
	}
}
