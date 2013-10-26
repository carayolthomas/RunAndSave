package model;


public class User {

	public static final String CRYPTALGO="SHA-1";
	private String userEmail;
	private String userPassword; //encode(password, CRYPTALGO)lol
	private float userWeight;
	//private List<Ride> userRides;
	
	public User(String userEmail, String userPassword, float userWeight) {
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

	public float getUserWeight() {
		return userWeight;
	}

	public void setUserWeight(int userWeight) {
		this.userWeight = userWeight;
	}

	@Override
	public String toString() {
		return "User [userEmail=" + userEmail + ", userPassword="
				+ userPassword + ", userWeight=" + userWeight + "]";
	}
	
	
	/*public List<Ride> getUserRides() {
		return userRides;
	}



	public void setUserRides(List<Ride> userRides) {
		this.userRides = userRides;
	}
	*/
}
