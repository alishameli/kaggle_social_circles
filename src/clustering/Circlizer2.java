package clustering;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dataStructures.Centroid;

public class Circlizer2 {

	/*
	 * c = circle, v = vector, f = feature
	 */
	double alpha = 1, beta = 1, gamma = 1;
	
	double[][] externalDistance;
	double[] circleDensity;

	// double[][][] cvf;
	int c;
	int v;
	int f;

	double[] featureCount;
	double[][] circleFeatureCount;
	double[][] circleVector;

	Centroid centroid;
	ArrayList<Container> circles;

	public Circlizer2(Centroid centroid, ArrayList<Container> circles) {
		this.centroid = centroid;
		this.circles = circles;

		this.c = circles.size();
		// TODO: remove centroid from vectors or not
		this.v = centroid.getFeat().size();
		// TODO: friendship state in vectors
		this.f = centroid.getFeat().get(0).getSize();
		
		
		// fillCVF();
		fillValues();
	}

	public LinkedHashSet<Integer> circlize() {
		/*
		 * LinkedHashSet<Integer> best = new LinkedHashSet<Integer>(); double
		 * bScore = 0; for (int i = 1; i < c; i++) { LinkedHashSet<Integer> set
		 * = circlize(i); double score = computeScore(set); if (score > bScore)
		 * { bScore = score; best.clear(); for (Integer tmp : set) {
		 * best.add(tmp); } } } return best;
		 */
		externalDistance=new double[c][c];
		circleDensity=new double[c];

		
		double totalBestScore = 0;
		LinkedHashSet<Integer> totalBestSet = new LinkedHashSet<Integer>();
		totalBestSet.clear();
		double bestScore = 0;
		Set<Integer> bestSet = new LinkedHashSet<Integer>();
		bestSet.clear();
		boolean mark[] = new boolean[c];
		int bestj = -1;
		for (int i = 0; i < c; i++) {
			bestScore = 0;
			for (int j = 0; j < c; j++) {
				if (mark[j] == false) {
					bestSet.add(j);
					mark[j] = true;
					double score = computeScore(bestSet);
					if (score > bestScore) {
						bestj = j;
						bestScore = score;
					}
					mark[j] = false;
					bestSet.remove(j);
				}
				bestSet.add(bestj);
				mark[bestj] = true;

				double score = computeScore(bestSet);
				if (score > totalBestScore) {
					totalBestScore = score;
					totalBestSet.clear();
					for (Integer tmp : bestSet)
						totalBestSet.add(tmp);
				}

			}
		}
		return totalBestSet;
	}

//	public LinkedHashSet<Integer> circlize(int p) {
//
//		// Set<Integer> bestSet = new LinkedHashSet<Integer>();
//		boolean mark[] = new boolean[c];
//		Set<Integer> bestSet = new LinkedHashSet<Integer>();
//		bestSet.clear();
//		int bestj = -1;
//		double bestScore = 0;
//		for (int i = 0; i < p; i++) {
//			bestScore = 0;
//			for (int j = 0; j < c; j++) {
//				if (mark[j] == false) {
//					bestSet.add(j);
//					mark[j] = true;
//					double score = computeScore(bestSet);
//					if (score > bestScore) {
//						bestj = j;
//						bestScore = score;
//					}
//					mark[j] = false;
//					bestSet.remove(j);
//				}
//				bestSet.add(bestj);
//				mark[bestj] = true;
//			}
//		}
//		return (LinkedHashSet<Integer>) bestSet;
//	}

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
	 */	private double computeScore(Set<Integer> set) {
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
			// coverage = Math.log1p(coverage);// Reduce the effect of coverage. Can
			// be
			// commented.
			// coverage = Math.pow(coverage, 6);

			// if (avgExternalDistance == 0)
			// avgExternalDistance = 0.01;

			return alpha * coverage + beta * avgCircleDensity + gamma
					* avgExternalDistance;
			// return coverage /* avgCircleDensity*/ / avgCircleCloseness;
		}

		// private void fillCVF() {
		// cvf = new double[c][v][f];
		// for (int i = 0; i < c; i++) {
		// for (int j = 0; j < v; j++) {
		// if (circles.get(i).arr.contains(Integer.valueOf(j))) {
		// double[] features = centroid.getFeat()
		// .get(Integer.valueOf(j)).getFeatures();
		// cvf[i][j] = features;
		// }
		// }
		// }
		// }
	}
