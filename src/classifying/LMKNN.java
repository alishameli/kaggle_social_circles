package classifying;

import java.util.ArrayList;

import dataStructures.DistanceType;
import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class LMKNN {

	ArrayList<FeatureVector> a;
	private int[] knnCircles;

	double landa;
	private int[] numberOfFixedVectorsInCircles;
	private double[] scoreOfCircles;
	private int numberOfCircles;
	private int[][] sumOfElementsOfEachCircle;
	private int[] sumOfCircle;
	private int sumOfAll;
	private int numberOfFeatures;
	private int[] sumOfFixedVectors;
	private ArrayList<FeatureVector> vectors;
	private FeatureVector query;
	private DistanceType distanceType;

	public int test(Snapshot snap, FeatureVector query, int k, double landa,
			DistanceType distanceType) throws Exception {
		this.distanceType = distanceType;
		this.landa = landa;
		a = new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			a.add(value);
		}

		int numOfCircles = snap.getCircles().size();

		knnCircles = new int[numOfCircles];
		for (int i = 0; i < numOfCircles; i++)
			knnCircles[i] = 0;

		FeatureVector[] KNearest = getKNearest(k, query);

		for (int i = 0; i < k; i++) {
			if (KNearest[i] == null)
				break;
			for (int j = 0; j < KNearest[i].getCircles().size(); j++) {
				int c = KNearest[i].getCircles().get(j);
				knnCircles[c]++;
			}
		}

		return getIdOfCircleByLanguageModel(snap, query, .999);
	}

	private FeatureVector[] getKNearest(int k, FeatureVector query)
			throws Exception {
		FeatureVector[] KNearest = new FeatureVector[k];
		int size = a.size();
		boolean[] checked = new boolean[size];
		double[] distance = new double[size];
		for (int i = 0; i < distance.length; i++) {
			distance[i] = a.get(i).getDistanceFrom(query, distanceType);
		}
		for (int i = 0; i < k; i++) {
			int nearestIndex = -1;
			double nearestDistance = Double.MAX_VALUE;
			for (int j = 0; j < distance.length; j++) {
				if (distance[j] < nearestDistance
						&& a.get(j).getCircles().size() > 0) {
					if (!checked[j]) {
						nearestDistance = distance[j];
						nearestIndex = j;
					}
				}
			}
			if (nearestIndex == -1)
				break;
			checked[nearestIndex] = true;
			KNearest[i] = a.get(nearestIndex);
		}
		return KNearest;
	}

	public int getIdOfCircleByLanguageModel(Snapshot snap, FeatureVector query,
			double landa) {
		vectors = new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			vectors.add(value);
		}
		this.landa = landa;
		this.query = query;
		numberOfCircles = snap.getCircles().size();
		numberOfFeatures = snap.getNumberOfFeatures();
		sumOfFixedVectors = new int[numberOfFeatures];
		sumOfElementsOfEachCircle = new int[numberOfCircles][numberOfFeatures];
		sumOfCircle = new int[numberOfCircles];
		numberOfFixedVectorsInCircles = new int[numberOfCircles];
		fillEssentialDataStructures();
		scoreOfCircles = new double[numberOfCircles];
		findScoreOfCirclesWithLanda();
		return findBestCircle();
	}

	public void findScoreOfCirclesWithLanda() {
		for (int i = 0; i < numberOfCircles; i++)
			for (int j = 0; j < numberOfFeatures; j++)
				if (query.getFeatures()[j] > 0) {
					int numberOfCurrentFeature = (int) query.getFeatures()[j];
					scoreOfCircles[i] += numberOfCurrentFeature
							* Math.log10(landa
									* ((double) sumOfElementsOfEachCircle[i][j] / (double) sumOfCircle[i])
									+ (1.0 - landa)
									* ((double) sumOfFixedVectors[j] / (double) sumOfAll));
				}
	}

	public void fillEssentialDataStructures() {
		for (int i = 0; i < vectors.size(); i++) {
			FeatureVector temp = vectors.get(i);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					sumOfFixedVectors[j] += temp.getFeatures()[j];
					sumOfAll += temp.getFeatures()[j];
					if (tempCircle.size() > 0)
						for (int k = 0; k < tempCircle.size(); k++) {
							sumOfElementsOfEachCircle[temp.getCircles().get(k)][j] += temp
									.getFeatures()[j];
							sumOfCircle[temp.getCircles().get(k)] += temp
									.getFeatures()[j];
							if (j == 0)
								numberOfFixedVectorsInCircles[temp.getCircles()
										.get(k)]++;
						}
				}
		}
	}

	private int findBestCircle() {
		int result = -1;
		for (int i = 0; i < numberOfCircles; i++) {
			if (result == -1 && knnCircles[i] > 0)
				result = i;
			else if (result > -1 && scoreOfCircles[i] > scoreOfCircles[result]
					&& knnCircles[i] > 0)
				result = i;
		}
		if (result == -1) {
			result = 0;
			for (int i = 1; i < numberOfCircles; i++)
				if (scoreOfCircles[i] > scoreOfCircles[result])
					result = i;
		}
		return result;
	}
}