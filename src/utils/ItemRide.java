package utils;

/**
 * Util class for put an icon in each row of the list of rides
 * 
 * @author hti
 * 
 */
public class ItemRide {

	/** The text of a row */
	private String mItemRideText;

	/** The icon of a row */
	private int mItemRideIconFile;

	/**
	 * Constructor used
	 * 
	 * @param pItemRideText
	 * @param pItemRideIconFile
	 */
	public ItemRide(String pItemRideText, int pItemRideIconFile) {
		super();
		this.mItemRideText = pItemRideText;
		this.mItemRideIconFile = pItemRideIconFile;
	}

	/**
	 * Return the text of the row
	 * 
	 * @return string
	 */
	public String getItemRideText() {
		return mItemRideText;
	}

	/**
	 * Set the text of a row
	 * 
	 * @param pItemRideText
	 */
	public void setItemRideText(String pItemRideText) {
		this.mItemRideText = pItemRideText;
	}

	/**
	 * Return the icon file of the row
	 * 
	 * @return int
	 */
	public int getItemRideIconFile() {
		return mItemRideIconFile;
	}

	/**
	 * Set the icon file of a row
	 * 
	 * @param pItemRideIconFile
	 */
	public void setItemRideIconFile(int pItemRideIconFile) {
		this.mItemRideIconFile = pItemRideIconFile;
	}

}
