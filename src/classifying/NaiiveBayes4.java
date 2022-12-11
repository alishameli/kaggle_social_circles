package classifying;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.FeatureVector;

public class NaiiveBayes4 {

	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, Circle> answerCircles;
	private HashMap<Integer, FeatureVector> feat;
	private int numberOfCircles;
	private int[] numberOfBagOfFeatures;
	private double[] numberOfOnes;
	private double[][] sumOfEachFeature;
	private double[][][] sumOfElementsOfEachCircle;
	private double[][] sumOfCircle;
	private boolean[][] circleFriendshipMembers;
	private boolean[][] circleFeatureMembers;
	private int numberOfFeatures;
	private int numberOfFriendships;
	private ArrayList<Integer> removedFromCircles = new ArrayList<Integer>();

	public double test(Centroid c) {
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
		}
		numberOfCircles = c.getCircles().size();
		numberOfFeatures = c.getFeat()
				.get(c.getMappedIDsRealToVirtual().get(c.getID())).getSize()
				- feat.size();
		numberOfFriendships = feat.size();
		numberOfOnes = new double[numberOfCircles];
		circleFeatureMembers = new boolean[numberOfCircles][numberOfFeatures];
		circleFriendshipMembers = new boolean[numberOfCircles][numberOfFriendships];
		sumOfElementsOfEachCircle = new double[numberOfCircles][numberOfCircles][numberOfFeatures
				+ numberOfFriendships];
		numberOfBagOfFeatures = new int[numberOfCircles];
		sumOfCircle = new double[numberOfCircles][numberOfCircles];
		sumOfEachFeature = new double[numberOfCircles][numberOfFeatures
				+ numberOfFriendships];
		if (feat.remove(Integer.valueOf(c.getMappedIDsRealToVirtual().get(
				c.getID()))) == null) {
			System.err
					.println("Couldn't remove the Centroid from featurevectors");
		}
		answerCircles = new HashMap<Integer, Circle>();
		for (Integer circleId : c.getCircles().keySet()) {
			circles.put(circleId, c.getCircles().get(circleId));
			answerCircles.put(circleId, new Circle());
		}
		fillEssentialDataStructures();
		for (Integer queryId : feat.keySet()) {
			findQueryCircles(queryId);
		}
		return computeF1();
	}

	private void findQueryCircles(Integer queryId) {
		removeFeatureVectorFromDataStructures(queryId);
		for (Integer circleId : circles.keySet()) {
			if (circles.get(circleId).getCircleMembers().size() == 0)
				continue;
			boolean assigned = assignCircle(queryId, circleId);
			if (assigned)
				answerCircles.get(circleId).add(queryId);
		}
		addFeatureVectorFromDataStructures(queryId);
	}

	private boolean assignCircle(Integer queryId, Integer circleId) {
		double assignScore = 0, notAssignScore = 0;
		assignScore += Math.log10((double) circles.get(circleId)
				.getCircleMembers().size()
				/ (double) (feat.size() - 1));
		notAssignScore += Math.log10(1
				- (double) circles.get(circleId).getCircleMembers().size()
				/ (double) (feat.size() - 1));
		for (int i = 0; i < numberOfFeatures + numberOfFriendships; i++) {
			if (feat.get(queryId).getFeatures()[i] > 0) {
				if (i >= numberOfFeatures
						&& !circleFriendshipMembers[circleId][i
								- numberOfFeatures])
					continue;
				if (i < numberOfFeatures && !circleFeatureMembers[circleId][i])
					continue;
				assignScore += Math
						.log10(Math
								.pow((double) (1 + sumOfElementsOfEachCircle[circleId][circleId][i])
										/ (double) (numberOfBagOfFeatures[circleId] + sumOfCircle[circleId][circleId]),
										feat.get(queryId).getFeatures()[i]));
				notAssignScore += Math
						.log10(Math
								.pow((double) (1 + sumOfEachFeature[circleId][i] - sumOfElementsOfEachCircle[circleId][circleId][i])
										/ (double) (numberOfBagOfFeatures[circleId]
												+ numberOfOnes[circleId] - sumOfCircle[circleId][circleId]),
										feat.get(queryId).getFeatures()[i]));
			}
		}
		return (assignScore > notAssignScore);
	}

	private void removeFeatureVectorFromDataStructures(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < numberOfCircles; i++)
			for (int j = 0; j < query.getFeatures().length; j++) {
				numberOfOnes[i] -= query.getFeatures()[j];
				sumOfEachFeature[i][j] -= query.getFeatures()[j];
			}
		for (int k = 0; k < numberOfCircles; k++)
			for (int i = 0; i < query.getCircles().size(); i++) {
				for (int j = 0; j < query.getFeatures().length; j++) {
					sumOfElementsOfEachCircle[k][query.getCircles().get(i)][j] -= query
							.getFeatures()[j];
					sumOfCircle[k][query.getCircles().get(i)] -= query
							.getFeatures()[j];
				}
			}
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				removedFromCircles.add(circleID);
			}
		}
	}

	private void addFeatureVectorFromDataStructures(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < numberOfCircles; i++)
			for (int j = 0; j < query.getFeatures().length; j++) {
				numberOfOnes[i] += query.getFeatures()[j];
				sumOfEachFeature[i][j] += query.getFeatures()[j];
			}
		for (int k = 0; k < numberOfCircles; k++)
			for (int i = 0; i < query.getCircles().size(); i++) {
				for (int j = 0; j < query.getFeatures().length; j++) {
					sumOfElementsOfEachCircle[k][query.getCircles().get(i)][j] += query
							.getFeatures()[j];
					sumOfCircle[k][query.getCircles().get(i)] += query
							.getFeatures()[j];
				}
			}
		for (int i = 0; i < removedFromCircles.size(); i++) {
			circles.get(removedFromCircles.get(i)).add(queryId);
		}
		removedFromCircles.clear();
	}

	private void fillEssentialDataStructures() {
		for (int i = 0; i < numberOfCircles; i++)
			for (int j = 0; j < circles.get(i).getCircleMembers().size(); j++)
				circleFriendshipMembers[i][circles.get(i).getCircleMembers()
						.get(j)] = true;

		for (Integer tempFeatureVectorID : feat.keySet()) {
			FeatureVector temp = feat.get(tempFeatureVectorID);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int i = 0; i < numberOfCircles; i++) {
						numberOfOnes[i] += temp.getFeatures()[j];
						sumOfEachFeature[i][j] += temp.getFeatures()[j];
						for (int k = 0; k < tempCircle.size(); k++) {
							sumOfElementsOfEachCircle[i][temp.getCircles().get(
									k)][j] += temp.getFeatures()[j];
							sumOfCircle[i][temp.getCircles().get(k)] += temp
									.getFeatures()[j];
						}
					}
				}
			for (int j = numberOfFeatures; j < numberOfFeatures
					+ numberOfFriendships; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int i = 0; i < numberOfCircles; i++) {
						if (circleFriendshipMembers[i][j - numberOfFeatures]) {
							numberOfOnes[i] += temp.getFeatures()[j];
							sumOfEachFeature[i][j] += temp.getFeatures()[j];
							for (int k = 0; k < tempCircle.size(); k++) {
								sumOfElementsOfEachCircle[i][temp.getCircles()
										.get(k)][j] += temp.getFeatures()[j];
								sumOfCircle[i][temp.getCircles().get(k)] += temp
										.getFeatures()[j];
							}
						}
					}
				}
		}

		for (int i = 0; i < numberOfCircles; i++) {
			for (int j = 0; j < numberOfFeatures; j++) {
				if (sumOfElementsOfEachCircle[i][i][j]
						/ (double) circles.get(i).getCircleMembers().size() > sumOfEachFeature[i][j]
						/ (double) (feat.size())) {
					circleFeatureMembers[i][j] = true;
				}
			}
		}
		for(int i = 0;i < numberOfCircles;i++){
			numberOfOnes[i] = 0;
			for(int j = 0;j < numberOfFeatures + numberOfFriendships;j++)
				sumOfEachFeature[i][j] = 0;
			for(int j = 0;j < numberOfCircles;j++){
				sumOfCircle[i][j] = 0;
				for(int k = 0;k < numberOfFeatures + numberOfFriendships;k++)
					sumOfElementsOfEachCircle[i][j][k] = 0;
			}
		}
			
		
		for (Integer tempFeatureVectorID : feat.keySet()) {
			FeatureVector temp = feat.get(tempFeatureVectorID);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int i = 0; i < numberOfCircles; i++) {
						if (circleFeatureMembers[i][j]) {
							numberOfOnes[i] += temp.getFeatures()[j];
							sumOfEachFeature[i][j] += temp.getFeatures()[j];
							for (int k = 0; k < tempCircle.size(); k++) {
								sumOfElementsOfEachCircle[i][temp.getCircles()
										.get(k)][j] += temp.getFeatures()[j];
								sumOfCircle[i][temp.getCircles().get(k)] += temp
										.getFeatures()[j];
							}
						}
					}
				}
			for (int j = numberOfFeatures; j < numberOfFeatures
					+ numberOfFriendships; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int i = 0; i < numberOfCircles; i++) {
						if (circleFriendshipMembers[i][j - numberOfFeatures]) {
							numberOfOnes[i] += temp.getFeatures()[j];
							sumOfEachFeature[i][j] += temp.getFeatures()[j];
							for (int k = 0; k < tempCircle.size(); k++) {
								sumOfElementsOfEachCircle[i][temp.getCircles()
										.get(k)][j] += temp.getFeatures()[j];
								sumOfCircle[i][temp.getCircles().get(k)] += temp
										.getFeatures()[j];
							}
						}
					}
				}
		}
		for (int j = 0; j < numberOfCircles; j++)
			for (int i = 0; i < numberOfFeatures + numberOfFriendships; i++)
				if (sumOfEachFeature[j][i] > 0)
					numberOfBagOfFeatures[j]++;
	}

	private double computeF1() {
		double TP = 0, FP = 0, FN = 0;
		for (Integer circleId : answerCircles.keySet()) {
			for (Integer id : answerCircles.get(circleId).getCircleMembers()) {
				if (circles.get(circleId).getCircleMembers().contains(id))
					TP++;
				else
					FP++;
			}
		}
		for (Integer circleId : circles.keySet()) {
			for (Integer id : circles.get(circleId).getCircleMembers()) {
				if (!answerCircles.get(circleId).getCircleMembers()
						.contains(id))
					FN++;
			}
		}
		double Precision = TP / (TP + FP);
		double Recall = TP / (TP + FN);
		double F1 = (2 * Precision * Recall) / (Precision + Recall);
		System.out.print("Precision : " + Precision);
		System.out.print("\tRecall : " + Recall);
		System.out.println("\tF1 : " + F1);
		return F1;
	}
}
