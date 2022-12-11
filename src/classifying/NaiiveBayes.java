package classifying;

import java.util.ArrayList;

import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class NaiiveBayes {
	private double[] scoreOfCircles;
	private int numberOfCircles;
	private int[][] sumOfElementsOfEachCircle;
	private int[] sumOfCircle;
	private int[] numberOfFixedVectorsInCircles;
	private int numberOfFixedVectors;
	private int numberOfFeatures;
	private ArrayList<FeatureVector> vectors;
	private FeatureVector query;

	public Pair test(Snapshot snap, final FeatureVector query) {
		vectors = new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			vectors.add(value);
		}
		numberOfFixedVectors = vectors.size();
		this.query = query;
		numberOfCircles = snap.getCircles().size();
		numberOfFeatures = snap.getNumberOfFeatures();
		sumOfElementsOfEachCircle = new int[numberOfCircles][numberOfFeatures];
		sumOfCircle = new int[numberOfCircles];
		numberOfFixedVectorsInCircles = new int[numberOfCircles];
		scoreOfCircles = new double[numberOfCircles];

		fillEssentialDataStructures();
		findScoreOfCircles();
		
		int bestID = 0;
		double max = -1000000;
		for (int i = 0; i < numberOfCircles; i++) {
			if (scoreOfCircles[i] > max) {
				max = scoreOfCircles[i];
				bestID = i;
			}
		}
		int secondID = 0; max = -1000000;
		for(int i = 0; i < numberOfCircles;i++)
			if(scoreOfCircles[i] > max && i != bestID){
				max = scoreOfCircles[i];
				secondID = i;
			}
		return new Pair(bestID, secondID);
	}

	public void findScoreOfCircles() {
		for (int i = 0; i < numberOfCircles; i++) {
			if (numberOfFixedVectorsInCircles[i] == 0)
				continue;
			scoreOfCircles[i] += Math
					.log10((double) numberOfFixedVectorsInCircles[i]
							/ (double) numberOfFixedVectors);
			for (int j = 0; j < numberOfFeatures; j++) {
				if (query.getFeatures()[j] > 0) {
					int numberOfCurrentFeature = (int) query.getFeatures()[j]; 
					scoreOfCircles[i] += numberOfCurrentFeature * Math
							.log10((double) (sumOfElementsOfEachCircle[i][j] + 1)
									/ (double) (sumOfCircle[i] + numberOfFeatures));
				}
			}
		}
	}

	public void fillEssentialDataStructures() {
		for (int i = 0; i < vectors.size(); i++) {
			FeatureVector temp = vectors.get(i);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int k = 0; k < tempCircle.size(); k++)
				numberOfFixedVectorsInCircles[temp.getCircles().get(k)]++;
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int k = 0; k < tempCircle.size(); k++) {
						sumOfElementsOfEachCircle[temp.getCircles().get(k)][j] += temp
								.getFeatures()[j];
						sumOfCircle[temp.getCircles().get(k)] += temp
								.getFeatures()[j];
					}
				}
		}
	}
}
