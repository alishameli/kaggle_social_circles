package classifying;

import java.util.ArrayList;

import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class LanguageModel {
	// how we can use vectors --> arraylist of circles & features

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

	public Pair test(Snapshot snapshot, FeatureVector query, double landa) {
		vectors = new ArrayList<FeatureVector>();
		for (FeatureVector value : snapshot.getFeat().values()) {
			vectors.add(value);
		}
		this.landa = landa;
		this.query = query;
		numberOfCircles = snapshot.getCircles().size();
		numberOfFeatures = snapshot.getNumberOfFeatures();
		sumOfFixedVectors = new int[numberOfFeatures];
		sumOfElementsOfEachCircle = new int[numberOfCircles][numberOfFeatures];
		sumOfCircle = new int[numberOfCircles];
		numberOfFixedVectorsInCircles = new int[numberOfCircles];
		fillEssentialDataStructures();
		scoreOfCircles = new double[numberOfCircles];

		findScoreOfCirclesWithLanda();
		return findBestCircles();
	}

	public Pair findBestCircles() {
		Pair result = new Pair(0, 0);
		for (int i = 1; i < numberOfCircles; i++)
			if (scoreOfCircles[i] > scoreOfCircles[result.getFirstCircle()]) {
				result.setSecondCircle(result.getFirstCircle());
				result.setFirstCircle(i);
			}
		if (result.getFirstCircle() == 0) {
			result.setSecondCircle(1);
			for (int i = 2; i < numberOfCircles; i++)
				if (scoreOfCircles[i] > scoreOfCircles[result.getSecondCircle()])
					result.setSecondCircle(i);
		}
		return result;
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
}
