package classifying;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.FeatureVector;

public class NaiiveBayes6ForClusteringForAdd {
	private Centroid c;
	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, Circle> answerCircles;
	private HashMap<Integer, FeatureVector> feat;
	private int numberOfCircles;
	private int[] numberOfBagOfFeatures;
	private double[] numberOfOnes;
	private double[][] sumOfEachFeature;
	private double[][][] sumOfElementsOfEachCircle;
	private double[][] sumOfCircle;
	private boolean[][] circleMembers;
	private int numberOfFeatures;
	private int numberOfFriendships;
	private ArrayList<Integer> removedFromCircles = new ArrayList<Integer>();

	public void test(Centroid c) {
		this.c = c;
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
		}
		numberOfCircles = c.getCircles().size();
		numberOfFeatures = c.getFeat().get(0).getFeatures().length
				- feat.size();
		numberOfFriendships = feat.size();
		numberOfOnes = new double[numberOfCircles];
		circleMembers = new boolean[numberOfCircles][numberOfFriendships];
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
		computeResults();
		printToFile();
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
						&& !circleMembers[circleId][i - numberOfFeatures])
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
				circleMembers[i][circles.get(i).getCircleMembers().get(j)] = true;

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
						if (circleMembers[i][j - numberOfFeatures]) {
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

	private void computeResults() {
		for (Integer circleId : answerCircles.keySet())
			for (Integer id : answerCircles.get(circleId).getCircleMembers())
				if (!circles.get(circleId).getCircleMembers().contains(id)) {
					circles.get(circleId).add(id);
					feat.get(id).assignCircle(circleId);
				}
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			int i = 0;
			boolean flg = false;
			for (Circle cir : c.getCircles().values()) {
				if (cir.getCircleMembers().size() > 5) {
					flg = true;
					sb.append("circle" + i++ + ": ");
					for (int j = 0; j < cir.getCircleMembers().size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								cir.getCircleMembers().get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									cir.getCircleMembers().get(j))
									+ " ");
				}
				if(flg)
					sb.setLength(sb.length() - 1);
				sb.append("\n");
			}
			// if(!c.getID().equals("2738") && !c.getID().equals("24758") &&
			// !c.getID().equals("1813") && !c.getID().equals("3077"))
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
