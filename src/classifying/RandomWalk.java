package classifying;

import java.util.HashMap;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.DistanceType;
import dataStructures.FeatureVector;
import dataStructures.Friends;

public class RandomWalk {

	private double pageRank[];
	private double weightMatrix[][];
	private double probabilityMatrix[][];
	private HashMap<Integer, Friends> adj = new HashMap<Integer, Friends>();
	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, FeatureVector> feat;
	private int numberOfCircles;
	private int numberOfFeatures;
	private double alpha;

	public void test(Centroid c, double alpha) throws Exception {
		this.alpha = alpha;
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
			adj.put(id, c.getAdj().get(id));
		}
		if (feat.remove(Integer.valueOf(c.getID())) == null) {
			System.err
					.println("Couldn't remove the Centroid from featurevectors");
		}

		numberOfCircles = c.getCircles().size();
		numberOfFeatures = c.getFeat().get(c.getID()).getSize();
		for (Integer circleId : c.getCircles().keySet())
			circles.put(circleId, c.getCircles().get(circleId));
		weightMatrix = new double[adj.size()][adj.size()];
		probabilityMatrix = new double[adj.size()][adj.size()];
		pageRank = new double[adj.size()];

		for (Integer queryId : feat.keySet()) {
			fillWeightMatrix(c);
			fillProbabilityMatrix();
			randomRestart(queryId);
			findPageRank();
			int assignedCircle = findQueryCircles(queryId);
		}
	}

	public void fillWeightMatrix(Centroid c) throws Exception {
		for (int i = 0; i < adj.size(); i++)
			for (int j = 0; j < adj.size(); j++)
				weightMatrix[i][j] = 0;
		for (Integer tempVertex1 : adj.keySet()) {
			Integer tempVertex2;
			for (int i = 0; i < adj.get(tempVertex1).getSize(); i++) {
				tempVertex2 = adj.get(tempVertex1).getFriends()[i];
				int v = tempVertex1;
				int u = tempVertex2;
				weightMatrix[v][u] = assignWeight(tempVertex1, tempVertex2);
			}
		}
	}

	public void fillProbabilityMatrix() {
		for (int i = 0; i < adj.size(); i++)
			for (int j = 0; j < adj.size(); j++)
				probabilityMatrix[i][j] = 0;
		double sum[] = new double[adj.size()];
		for (int i = 0; i < adj.size(); i++)
			for (int j = 0; j < adj.size(); j++)
				sum[i] += weightMatrix[i][j];
		for (int i = 0; i < adj.size(); i++)
			for (int j = 0; j < adj.size(); j++)
				probabilityMatrix[i][j] = weightMatrix[i][j] / sum[i];
	}

	public double assignWeight(int v, int u) throws Exception {
		double res = feat.get(v).getDistanceFrom(feat.get(u), DistanceType.JACCARDIAN);
		return res;
	}

	public void randomRestart(int queryId) {
		for (int i = 0; i < adj.size(); i++)
			for (int j = 0; j < adj.size(); j++)
				probabilityMatrix[i][j] *= (1 - alpha);
		for (int i = 0; i < adj.size(); i++)
			probabilityMatrix[i][queryId] += alpha;
	}

	public int findQueryCircles(int queryId) {
		int maxCircle = -1;
		double maxProbability = -1;
		for (Integer circleId : circles.keySet()) {
			double tempSum = 0;
			for (int j = 0; j < circles.get(circleId).getCircleMembers().size(); j++)
				tempSum += pageRank[circles.get(circleId).getCircleMembers()
						.get(j)];
			if (tempSum >= maxProbability) {
				maxCircle = circleId;
				maxProbability = tempSum;
			}
		}
		return maxCircle;
	}

	public void findPageRank() throws Exception {
		Matrix p = new Matrix(probabilityMatrix);
		EigenvalueDecomposition e = new EigenvalueDecomposition(p);
		double[] ev = e.getRealEigenvalues();
		int index = -1;
		for (int i = 0; i < ev.length; i++) {
			if (Math.abs(ev[i] - 1) < 0.00001) {
				index = i;
				break;
			}
		}
		Matrix answer;
		if (index != -1) {
			answer = e
					.getV()
					.getMatrix(0, probabilityMatrix[0].length - 1,
							new int[] { index }).transpose();
			pageRank = answer.getArray()[0];
		} else {
			throw new Exception("(eigenvalue == 1) is false!");
		}
	}
}
