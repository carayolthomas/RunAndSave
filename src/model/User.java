package model;

/**
 * A class to describe a User
 * 
 * @author hti
 * 
 */
public class User {

	/** Algorithm for the password */
	public static final String CRYPTALGO = "SHA-1";

	/** User mail adress */
	private String mUserEmail;

	/** User password */
	private String mUserPassword;

	/** User weight */
	private int mUserWeight;

	/**
	 * Constructor of user
	 * 
	 * @param pUserEmail
	 * @param pUserPassword
	 * @param pUserWeight
	 */
	public User(String pUserEmail, String pUserPassword, int pUserWeight) {
		super();
		this.mUserEmail = pUserEmail;
		this.mUserPassword = pUserPassword;
		this.mUserWeight = pUserWeight;
	}

	/**
	 * Get User email
	 * 
	 * @return
	 */
	public String getUserEmail() {
		return mUserEmail;
	}

	/**
	 * Get User password
	 * 
	 * @return
	 */
	public String getUserPassword() {
		return mUserPassword;
	}

	/**
	 * Set User password
	 * 
	 * @param userPassword
	 */
	public void setUserPassword(String userPassword) {
		this.mUserPassword = userPassword;
	}

	/**
	 * Get User weight
	 * 
	 * @return
	 */
	public int getUserWeight() {
		return mUserWeight;
	}

	/**
	 * Set User weight
	 * 
	 * @param userWeight
	 */
	public void setUserWeight(int userWeight) {
		this.mUserWeight = userWeight;
	}

	/**
	 * Debug fonction
	 */
	@Override
	public String toString() {
		return "User [userEmail=" + mUserEmail + ", userPassword="
				+ mUserPassword + ", userWeight=" + mUserWeight + "]";
	}
}
