package model;

import java.security.NoSuchAlgorithmException;

import utils.Encode;
import utils.HTIDatabaseConnection;

public class User {

	private static final String CRYPTALGO="SHA-1";
	private int userId;
	private String userName;
	private String userEmail;
	private String userPassword; //encode(password, CRYPTALGO)
	private int userWeight;
	//private List<Ride> userRides;
	
	
	public User(int userId, String userName, String userEmail, String userPassword, int userWeight) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userWeight = userWeight;
	}

	

	public int getUserId() {
		return userId;
	}


	public String getUserName() {
		return userName;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public String getUserPassword() {
		return userPassword;
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
