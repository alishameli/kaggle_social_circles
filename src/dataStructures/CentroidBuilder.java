package dataStructures;

import java.util.HashMap;

public class CentroidBuilder {

	String ID;
	int numberOfFeatures;
	boolean virtualCircle;
	HashMap<Integer, FeatureVector> feat = new HashMap<Integer, FeatureVector>();
	HashMap<Integer, Friends> adj = new HashMap<Integer, Friends>();
	HashMap<String, Integer> mappedIDsRealToVirtual = new HashMap<String, Integer>();
	HashMap<Integer, String> mappedIDsVirtualToReal = new HashMap<Integer, String>();
	HashMap<Integer, Circle> circles = new HashMap<Integer, Circle>();
	double friendshipFactor;
	double featureFactor;
	double circleFactor;
	
	public void setID(String ID) {
		this.ID = ID;
	}

	public void setFeat(HashMap<Integer, FeatureVector> feat) {
		this.feat = feat;
	}

	public void setAdj(HashMap<Integer, Friends> adj) {
		this.adj = adj;
	}

	public void setMappedIDsRealToVirtual(HashMap<String, Integer> mappedIDsRealToVirtual) {
		this.mappedIDsRealToVirtual = mappedIDsRealToVirtual;
	}
	
	public void setMappedIDsVirtualToReal(HashMap<Integer, String> mappedIDsVirtualToReal) {
		this.mappedIDsVirtualToReal = mappedIDsVirtualToReal;
	}	

	public void setCircles(HashMap<Integer, Circle> circles) {
		this.circles = circles;
	}

	public void setFriendshipFactor(double friendshipFactor) {
		this.friendshipFactor = friendshipFactor;
	}

	public void setFeatureFactor(double featureFactor) {
		this.featureFactor = featureFactor;
	}

	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}

	public void setVirtualCircle(boolean virtualCircle) {
		this.virtualCircle = virtualCircle;
	}

	public Centroid build() {
		return new Centroid(ID, feat, adj, mappedIDsRealToVirtual, mappedIDsVirtualToReal, circles, circleFactor, 
				friendshipFactor, featureFactor, virtualCircle);
	}

	public void setCircleFactor(double circleFactor) {
		this.circleFactor = circleFactor;		
	}
}
