package classifying;

import java.util.ArrayList;

import dataStructures.FeatureVector;
import dataStructures.Snapshot;

// Term Frequency- Inverse Document Frequency
public class TFIDF {
	ArrayList<FeatureVector> vectors;
	private int numberOfFeatures;
	private int numberOfCircles;
	private double[] circleScores;
	private int[][] sumOfElementsOfEachCircle;
	private int[] documentFrequency;

	public int test(Snapshot snap, final FeatureVector query) throws Exception {
		numberOfCircles = snap.getCircles().size();
		this.numberOfFeatures = snap.getNumberOfFeatures();
		circleScores = new double[numberOfCircles];
		documentFrequency = new int[numberOfFeatures];
		sumOfElementsOfEachCircle = new int[numberOfCircles][numberOfFeatures];
		vectors = new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			vectors.add(value);
		}

		fillEssentialDataStructures();
		calculateCircleScores(query);
		return 0;
	}

	private void fillEssentialDataStructures() {
		for (int i = 0; i < vectors.size(); i++) {
			FeatureVector temp = vectors.get(i);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					if (tempCircle.size() > 0)
						for (int k = 0; k < tempCircle.size(); k++) {
							documentFrequency[j]++;
							sumOfElementsOfEachCircle[temp.getCircles().get(k)][j] += temp
									.getFeatures()[j];
						}
				}
		}
	}

	private void calculateCircleScores(FeatureVector query) {
		double[] tempScores = new double[numberOfCircles];
		for (int i = 0; i < numberOfCircles; i++) {
			for (int j = 0; j < numberOfFeatures; j++) {
				if (query.getFeatures()[j] > 0
						&& sumOfElementsOfEachCircle[i][j] > 0) {
					tempScores[i] += (1 + Math
							.log10(sumOfElementsOfEachCircle[i][j]))
							* (Math.log10(numberOfFeatures
									/ documentFrequency[j]));
				}
			}
		}
		double[] distance = new double[numberOfCircles];
		for (int i = 0; i < numberOfCircles; i++) {
			double temp = 0;
			for (int j = 0; j < numberOfFeatures; j++) {
				temp += sumOfElementsOfEachCircle[i][j]
						* sumOfElementsOfEachCircle[i][j];
			}
			distance[i] = Math.sqrt(temp);
		}
		for (int i = 0; i < numberOfCircles; i++) {
			circleScores[i] = tempScores[i] / distance[i];
		}
	}
}
