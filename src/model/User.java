package model;

import java.security.NoSuchAlgorithmException;

import utils.Encode;
import utils.HTIDatabaseConnection;

public class User {

	private static final String CRYPTALGO="SHA-1";
	private String userEmail;
	private String userPassword; //encode(password, CRYPTALGO)lol
	private int userWeight;
	//private List<Ride> userRides;
	
	public User(String userEmail, String userPassword, int userWeight) {
		super();
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userWeight = userWeight;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public int getUserWeight() {
		return userWeight;
	}

	public void setUserWeight(int userWeight) {
		this.userWeight = userWeight;
	}
	
	public static User login(String username, String clearPassword) throws NoSuchAlgorithmException {
		HTIDatabaseConnection htiDbConnection = HTIDatabaseConnection.getInstance();
		return htiDbConnection.getUser(username, Encode.encode(clearPassword, CRYPTALGO));
	}
	/*public List<Ride> getUserRides() {
		return userRides;
	}



	public void setUserRides(List<Ride> userRides) {
		this.userRides = userRides;
	}
	*/
}
