package dataStructures;

import java.util.ArrayList;

public class FeatureVector {

	private int size;
	private double length;
	private double[] featuresVector;
	private ArrayList<Integer> circles = new ArrayList<Integer>();

	public FeatureVector(Double[] featuresVector) {
		double[] temp = new double[featuresVector.length];
		for (int i = 0; i < featuresVector.length; i++) {
			temp[i] = Double.valueOf(featuresVector[i]);
		}
		this.featuresVector = temp;
		this.size = featuresVector.length;
		this.length = computeLength();
	}

	public void expandFeatures(double[] expansions) {
		int size = this.size + expansions.length;
		double[] temp = new double[size];
		for (int i = 0; i < this.size; i++) {
			temp[i] = featuresVector[i];
		}
		for (int i = this.size; i < size; i++) {
			temp[i] = expansions[i - this.size];
		}
		this.size = size;
		this.featuresVector = temp;
		this.length = computeLength();
	}

	public int getSize() {
		return this.size;
	}

	public double getLength() {
		return length;
	}

	public double[] getFeatures() {
		return this.featuresVector;
	}

	public void assignCircle(int circle) {
		circles.add(circle);
	}

	public ArrayList<Integer> getCircles() {
		return circles;
	}

	public double getDistanceFrom(FeatureVector target,
			DistanceType distanceType) throws Exception {
		if (distanceType == DistanceType.COSINE) {
			return cosineDistance(target);
		} else if (distanceType == DistanceType.EUCLIDEAN) {
			return euclideanDistance(target);
		} else if (distanceType == DistanceType.JACCARDIAN) {
			return jaccardDistance(target);
		} else {
			throw new Exception("Distance method type is not correct!");
		}
	}

	private double cosineDistance(FeatureVector target) throws Exception {
		double[] v1 = target.getFeatures();
		double[] v2 = this.getFeatures();
		if (v1.length != v2.length)
			throw new Exception(
					"Can not measure the distance between two different sized vectors");
		double dot = 0;
		for (int i = 0; i < v1.length; i++) {
			dot += (double) v1[i] * (double) v2[i];
		}
		double lengthProduct = this.length * target.length;
		return dot / lengthProduct;
	}

	private double euclideanDistance(FeatureVector target) throws Exception {
		double[] v1 = target.getFeatures();
		double[] v2 = this.getFeatures();
		if (v1.length != v2.length)
			throw new Exception(
					"Can not measure the distance between two different sized vectors");
		double ans = 0;
		for (int i = 0; i < v1.length; i++) {
			ans += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return Math.sqrt(ans);
	}

	private double jaccardDistance(FeatureVector target) throws Exception {
		double[] v1 = target.getFeatures();
		double[] v2 = this.getFeatures();

		if (v1.length != v2.length)
			throw new Exception(
					"Can not measure the distance between two different sized vectors");
		double intersection = 0;
		double union = 0;
		for (int i = 0; i < v1.length; i++) {
			double t1 = v1[i];
			double t2 = v2[i];
			if ((t1 == t2) && (t1 != 0))
				intersection++;
			if ((t1 != 0) || (t2 != 0))
				union++;
		}
		if (union == 0)
			return 1;
		return 1 - (intersection / union);
	}

	private double computeLength() {
		double length = 0;
		for (int i = 0; i < this.featuresVector.length; i++) {
			length += featuresVector[i] * featuresVector[i];
		}
		return Math.sqrt(length);
	}
}
