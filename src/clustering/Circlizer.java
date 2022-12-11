package clustering;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dataStructures.Centroid;

public class Circlizer {

	/*
	 * c = circle, v = vector, f = feature
	 */
	double[][][] cvf;
	int c;
	int v;
	int f;

	Centroid centroid;
	ArrayList<Container> circles;

	double bestScore = 0;

	public LinkedHashSet<Integer> Circlize(ArrayList<Container> circles,
			Centroid centroid) {
		this.centroid = centroid;
		this.circles = circles;

		this.c = circles.size();
		// TODO: remove centroid from vectors or not
		this.v = centroid.getFeat().size();
		// TODO: friendship state in vectors
		this.f = centroid.getFeat().get(0).getSize();

		fillCVF();
		fillValues();

		LinkedHashSet<Integer> circlesSet = new LinkedHashSet<Integer>();
		for (int i = 0; i < c; i++) {
			circlesSet.add(i);
		}
		Set<Integer> bestSet = new LinkedHashSet<Integer>();
		Set<Set<Integer>> powerset = powerSet(circlesSet);
		for (Set<Integer> set : powerset) {
			if (set.size() > 2)
				continue;
			if (!set.isEmpty()) {
				double score = computeScore(set);
		//		System.out.println(score);
				if (score >= bestScore) {
					bestScore = score;
					bestSet = set;
				}
			}
		}
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
				for (int k = 0; k < f; k++)
					circleFeatureCount[i][k] += cvf[i][j][k];

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
		double avgCircleCloseness = 0;
		double coverage = 0;
		for (int i : set)
			for (int j : set) {
				if (i == j)
					continue;
				for (int k = 0; k < f; k++)
					avgCircleCloseness += circleVector[i][k]
							* circleVector[j][k];
			}
		if (set.size() > 1)
			avgCircleCloseness /= set.size() * (set.size() - 1);

		for (int i : set)
			for (int j = 0; j < v; j++)
				for (int k = 0; k < f; k++) {
					avgCircleDensity += (cvf[i][j][k] * circleVector[i][k])
							/ circles.get(i).arr.size();
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
//		coverage = Math.log1p(coverage);// Reduce the effect of coverage. Can
		// be
		// commented.
		coverage = Math.pow(coverage, 6);

		if (avgCircleCloseness == 0)
			avgCircleCloseness = 0.01;
		return coverage /* avgCircleDensity*/ / avgCircleCloseness;
	}

	private void fillCVF() {
		cvf = new double[c][v][f];
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < v; j++) {
				if (circles.get(i).arr.contains(Integer.valueOf(j))) {
					double[] features = centroid.getFeat()
							.get(Integer.valueOf(j)).getFeatures();
					cvf[i][j] = features;
				}
			}
		}
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
}
