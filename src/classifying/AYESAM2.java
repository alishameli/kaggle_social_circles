package classifying;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.DistanceType;
import dataStructures.FeatureVector;

public class AYESAM2 {
	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, Circle> answerCircles;
	private HashMap<Integer, FeatureVector> feat;
	private double[][] interactions;
	private ArrayList<Integer> removedFromCircles1 = new ArrayList<Integer>();
	private ArrayList<Integer> removedFromCircles2 = new ArrayList<Integer>();

	public void test(Centroid c) throws Exception {
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
		}
		if (feat.remove(Integer.valueOf(c.getID())) == null) {
			System.err
					.println("Couldn't remove the Centroid from featurevectors");
		}

		int maxIdValue = 0;
		for (Integer id : feat.keySet()) {
			if (id > maxIdValue)
				maxIdValue = id;
		}
		interactions = new double[maxIdValue + 1][maxIdValue + 1];
		answerCircles = new HashMap<Integer, Circle>();
		for (Integer circleId : c.getCircles().keySet()) {
			circles.put(circleId, c.getCircles().get(circleId));
			answerCircles.put(circleId, new Circle());
		}
		computeInteractions();
		for (Integer queryId : feat.keySet()) {
			findQueryCircles(queryId);
		}
		printResultsToFile(c);
	}

	public void computeInteractions() throws Exception {
		for (Integer f1 : feat.keySet()) {
			FeatureVector fv1 = feat.get(f1);
			for (Integer f2 : feat.keySet()) {
				FeatureVector fv2 = feat.get(f2);
				double value = getDistance(fv1, fv2);
				interactions[f1][f2] = value;
				interactions[f2][f1] = value;
			}
		}
	}

	private double getDistance(FeatureVector fv1, FeatureVector fv2)
			throws Exception {
		return fv1.getDistanceFrom(fv2, DistanceType.JACCARDIAN);
	}

	private void removeFeatureVectorFromDataStructures1(Integer queryId) {
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				feat.get(queryId).getCircles().remove(circleID);
				removedFromCircles1.add(circleID);
			}
		}
	}

	private void addFeatureVectorToDataStructures1(Integer queryId) {
		for (int i = 0; i < removedFromCircles1.size(); i++) {
			circles.get(removedFromCircles1.get(i)).add(queryId);
			feat.get(queryId).assignCircle(removedFromCircles1.get(i));
		}
		removedFromCircles1.clear();
	}

	private void removeFeatureVectorFromDataStructures2(Integer queryId) {
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				feat.get(queryId).getCircles().remove(circleID);
				removedFromCircles2.add(circleID);
			}
		}
	}

	private void addFeatureVectorToDataStructures2(Integer queryId) {
		for (int i = 0; i < removedFromCircles2.size(); i++) {
			circles.get(removedFromCircles2.get(i)).add(queryId);
			feat.get(queryId).assignCircle(removedFromCircles2.get(i));
		}
		removedFromCircles2.clear();
	}

	private void findQueryCircles(Integer queryId) {

		removeFeatureVectorFromDataStructures1(queryId);
		boolean assigned = false;
		for (Integer circleId : circles.keySet()) {
			// if (circles.get(circleId).getCircleMembers().size() < 4)
			// assigned = handleSmallCircles(queryId, circleId);
			// else
			assigned = assignCircle(queryId, circleId);
			if (assigned)
				answerCircles.get(circleId).add(queryId);
		}
		addFeatureVectorToDataStructures1(queryId);
	}

	private boolean handleSmallCircles(Integer queryId, Integer circleId) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean assignCircle(Integer queryId, Integer circleId) {
		ArrayList<Integer> circleMembers = circles.get(circleId)
				.getCircleMembers();
		int count = 0;
		for (int i = 0; i < circleMembers.size(); i++) {
			Integer helper = circleMembers.get(i);
			removeFeatureVectorFromDataStructures2(helper);
			double queryScore = computeScore(queryId, circleId);
			double helperScore = computeScore(helper, circleId);
			if (queryScore <= helperScore)
				count++;
			addFeatureVectorToDataStructures2(helper);
		}
		if (count == circleMembers.size())
			return true;
		return false;
	}

	private double computeScore(Integer query, Integer circleId) {
		double score = 0;
		for (Integer circleMember : circles.get(circleId).getCircleMembers()) {
			score += interactions[query][circleMember];
		}
		return score;
	}

	private void printResultsToFile(Centroid c) {
		try {
			FileWriter fw = new FileWriter(new File("./AYESAM/" + c.getID()
					+ ".circles"));
			for (Integer circleId : answerCircles.keySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Circle" + circleId);
				for (Integer id : answerCircles.get(circleId)
						.getCircleMembers()) {
					sb.append(" " + id);
				}
				sb.append(System.getProperty("line.separator"));
				fw.write(sb.toString());
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

