package dataStructures;

import java.util.HashMap;

public class Centroid {

	private String ID;
	private int numberOfFeatures;
	private boolean virtualCirlce;
	private HashMap<Integer, FeatureVector> feat = new HashMap<Integer, FeatureVector>();
	private HashMap<Integer, Friends> adj = new HashMap<Integer, Friends>();
	private HashMap<String, Integer> mappedIDsRealToVirtual = new HashMap<String, Integer>();
	private HashMap<Integer, String> mappedIDsVirtualToReal = new HashMap<Integer, String>();
	private HashMap<Integer, Circle> circles = new HashMap<Integer, Circle>();
	private double friendshipFactor;
	private double featureFactor;

	public Centroid(final String ID,
			final HashMap<Integer, FeatureVector> feat,
			final HashMap<Integer, Friends> adj,
			final HashMap<String, Integer> mappedIDsRealToVirtual,
			final HashMap<Integer, String> mappedIDsVirtualToReal,
			final HashMap<Integer, Circle> circles, final double circleFactor,
			final double frienshipFactor, final double featureFactor,
			boolean virtualCircle) {
		this.ID = ID;
		this.numberOfFeatures = feat.size();
		this.feat = feat;
		this.adj = adj;
		this.mappedIDsRealToVirtual = mappedIDsRealToVirtual;
		this.mappedIDsVirtualToReal = mappedIDsVirtualToReal;
		this.circles = circles;
		this.friendshipFactor = frienshipFactor;
		this.featureFactor = featureFactor;
		this.virtualCirlce = virtualCircle;
	}

	public String getID() {
		return ID;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public HashMap<Integer, FeatureVector> getFeat() {
		return feat;
	}

	public HashMap<Integer, Friends> getAdj() {
		return adj;
	}

	public HashMap<String, Integer> getMappedIDsRealToVirtual() {
		return mappedIDsRealToVirtual;
	}

	public HashMap<Integer, String> getMappedIDsVirtualToReal() {
		return mappedIDsVirtualToReal;
	}

	public HashMap<Integer, Circle> getCircles() {
		return circles;
	}

	public double getFriendshipFactor() {
		return friendshipFactor;
	}

	public double getFeatureFactor() {
		return featureFactor;
	}

	public int getFriendsSize() {
		return adj.get(mappedIDsRealToVirtual.get(ID)).getSize();
	}

	public boolean getVirtualCircleState() {
		return this.virtualCirlce;
	}

	public Snapshot getSnapshot(int percentage, long seed) {
		return new Snapshot(this, percentage, seed);
	}

	public void setCircles(HashMap<Integer, Circle> newCircles) {
		this.circles = newCircles;
	}
}
