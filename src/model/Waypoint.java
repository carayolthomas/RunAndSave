package model;

public class Waypoint {
	private Double waypointLat;
	private Double waypointLng;

	public Waypoint(Double waypointLat, Double waypointLng) {
		super();
		this.waypointLat = waypointLat;
		this.waypointLng = waypointLng;
	}

	public Double getWaypointLat() {
		return waypointLat;
	}

	public Double getWaypointLng() {
		return waypointLng;
	}

}
