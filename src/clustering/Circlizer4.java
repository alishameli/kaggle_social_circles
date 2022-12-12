package clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import clustering.Evaluator.EVALUATION_TYPE;
import dataStructures.Centroid;

public class Circlizer4 {

	double alpha = 1, beta = 1, gamma = 1;

	double[][] externalDistance;
	double[] circleDensity;

	/*
	 * c = circle, v = vector, f = feature
	 */
	// double[][][] cvf;
	int c;
	int v;
	int f;

	Centroid centroid;
	ArrayList<Container> circles;
	LinkedHashSet<Container> clusters;

	StringBuilder sb = new StringBuilder();
	double bestScore = 0;
	double bestF1Score = 0;
	int bestF1Circle = 0;

	public Circlizer4(Centroid centroid, ArrayList<Container> circles)
			throws IOException {
		this.centroid = centroid;
		this.circles = circles;

		this.c = circles.size();
		// TODO: remove centroid from vectors or not
		this.v = centroid.getFeat().size();
		// TODO: friendship state in vectors
		this.f = centroid.getFeat().get(0).getSize();

		fillValues();
		clusters = findGroundTruthCircles(centroid);
	}

	public LinkedHashSet<Integer> circlize() throws IOException {

		externalDistance = new double[c][c];
		circleDensity = new double[c];

		LinkedHashSet<Integer> circlesSet = new LinkedHashSet<Integer>();
		for (int i = 0; i < c; i++) {
			circlesSet.add(i);
		}
		Set<Integer> bestSet = new LinkedHashSet<Integer>();
		Set<Set<Integer>> powerset = powerSet(circlesSet);
		sb.append("coverage\t\t");
		sb.append("internal\t\t");
		sb.append("external\t\t");
		sb.append("size\t\t");
		sb.append("score\t\t");
		sb.append("f1\n");

		for (Set<Integer> set : powerset) {
			// if (set.size() > 2)
			// continue;
			if (!set.isEmpty()) {
				double score = computeScore(set);
				evaluate(centroid, set, score);
				// System.out.println(score);
				if (score >= bestScore) {
					bestScore = score;
					bestSet = set;
				}
			}
		}
		sb.append("best score\n");
		evaluate(centroid, bestSet, bestScore);
		sb.append(bestF1Score + "\n");
		sb.append(bestF1Circle);
		FileWriter fw = new FileWriter("scores\\" + centroid.getID() + ".txt");
		fw.write(sb.toString());
		fw.close();

		return (LinkedHashSet<Integer>) bestSet;
	}

	double[] featureCount;
	double[][] circleFeatureCount;
	double[][] circleVector;

	private void fillValues() {
		featureCount = new double[f];
		circleFeatureCount = new double[c][f];
		circleVector = new double[c][f];

		for (int i = 0; i < v; i++)
			for (int j = 0; j < f; j++)
				featureCount[j] += centroid.getFeat().get(i).getFeatures()[j];

		for (int i = 0; i < c; i++)
			for (int j = 0; j < v; j++)
				for (int k = 0; k < f; k++) {
					double temp = 0;
					if (circles.get(i).arr.contains(Integer.valueOf(j))) {
						temp = centroid.getFeat().get(Integer.valueOf(j))
								.getFeatures()[k];
						circleFeatureCount[i][k] += temp;
					}
				}
		for (int i = 0; i < c; i++)
			for (int j = 0; j < f; j++)
				if (featureCount[j] != 0)
					circleVector[i][j] = circleFeatureCount[i][j]
							/ featureCount[j];
				else
					circleVector[i][j] = 0;
		for (int i = 0; i < c; i++) {
			double length = 0;
			for (int j = 0; j < f; j++)
				length += circleVector[i][j] * circleVector[i][j];
			length = Math.sqrt(length);

			if (length == 0)
				length = 1;
			for (int j = 0; j < f; j++)
				circleVector[i][j] = circleVector[i][j] / length;
		}
	}

	/**
	 * 
	 * @param set
	 *            set of circle ids that we want to compute their score
	 * @return score of this set
	 */
	private double computeScore(Set<Integer> set) {
		double avgCircleDensity = 0;
		double avgExternalDistance = 0;
		double coverage = 0;
		for (int i : set)
			for (int j : set) {
				if (i == j)
					continue;
				double dist = 0;
				if (externalDistance[i][j] == 0) {
					for (int k = 0; k < f; k++)
						externalDistance[i][j] += Math.pow(circleVector[i][k]
								- circleVector[j][k], 2);
					externalDistance[i][j] = Math.sqrt(externalDistance[i][j]);
				}
				avgExternalDistance += externalDistance[i][j];
			}
		if (set.size() > 1)
			avgExternalDistance /= set.size() * (set.size() - 1);

		for (int i : set) {
			if (circleDensity[i] == 0) {
				for (int j = 0; j < v; j++)
					for (int k = 0; k < f; k++) {
						double temp = 0;
						if (circles.get(i).arr.contains(Integer.valueOf(j))) {
							temp = centroid.getFeat().get(Integer.valueOf(j))
									.getFeatures()[k];
						}
						circleDensity[i] += (temp * circleVector[i][k])
								/ circles.get(i).arr.size();
					}
			}
			avgCircleDensity += circleDensity[i];
		}
		avgCircleDensity /= set.size();

		double total = v;
		double covered = 0;
		for (int i = 0; i < v; i++)
			for (int j : set) {
				if (circles.get(j).arr.contains(Integer.valueOf(i))) {
					covered += 1;
					break;
				}
			}
		coverage = covered / total;// we can use
		if (avgExternalDistance == 0)
			avgExternalDistance = 1;
		sb.append(coverage + "\t");
		sb.append(avgCircleDensity + "\t");
		sb.append(avgExternalDistance + "\t");
		return alpha * coverage + beta * avgCircleDensity + gamma
				* avgExternalDistance;
	}

	public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
		Set<Set<Integer>> sets = new LinkedHashSet<Set<Integer>>();
		if (originalSet.isEmpty()) {
			sets.add(new LinkedHashSet<Integer>());
			return sets;
		}
		List<Integer> list = new ArrayList<Integer>(originalSet);
		Integer head = list.get(0);
		Set<Integer> rest = new LinkedHashSet<Integer>(list.subList(1,
				list.size()));
		for (Set<Integer> set : powerSet(rest)) {
			Set<Integer> newSet = new LinkedHashSet<Integer>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}

	private void evaluate(Centroid c, Set<Integer> set, double score)
			throws IOException {
		LinkedHashSet<Container> chat = new LinkedHashSet<Container>();
		for (Integer i : set)
			chat.add(circles.get(i));
		Evaluator evaluator = new Evaluator();
		double fScore = evaluator.totalLoss(clusters, chat, c.getFriendsSize(),
				EVALUATION_TYPE.FSCORE);
		if (1 - fScore > bestF1Score) {
			bestF1Score = 1 - fScore;
			bestF1Circle = chat.size();
		}
		sb.append(set.size() + "\t");
		sb.append(score + "\t");
		sb.append((1.0 - fScore) + "\n");
	}

	private static LinkedHashSet<Container> findGroundTruthCircles(Centroid cent)
			throws IOException {
		int dataSet = 3;
		String groundTruthCirclePath = null;
		if (dataSet == 2)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\fb_dataset\\ground truth circles\\";
		else if (dataSet == 1)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\g+ dataset\\gplus.tar\\ground truth circles\\";
		else if (dataSet == 4)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\tw dataset\\ground truth circles\\";
		else if (dataSet == 3)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\Training\\";
		LinkedHashSet<Container> res = new LinkedHashSet<Container>();
		FileReader FR = new FileReader(groundTruthCirclePath + cent.getID()
				+ ".circles");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			Container c = new Container();
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int i = 1; i < Ids.length; i++) {
				c.arr.add(cent.getMappedIDsRealToVirtual().get(Ids[i]));
			}
			res.add(c);
		}
		BR.close();
		return res;
	}

}
