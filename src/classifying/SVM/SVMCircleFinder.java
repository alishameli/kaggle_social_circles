package classifying.SVM;

import java.util.ArrayList;
import java.util.HashMap;

import classifying.ResultPair;
import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.FeatureVector;

public class SVMCircleFinder {

	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, Circle> answerCircles;
	private HashMap<Integer, FeatureVector> feat;
	private int numberOfFriendships;
	private ArrayList<Integer> removedFromCircles = new ArrayList<Integer>();
	private FeatureNode[][] featureNodes;

	public ResultPair test(Centroid c) {
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
		}
		numberOfFriendships = feat.size();
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
		createFeaturesNodes();
		for (Integer queryId : feat.keySet()) 
			findQueryCircles(queryId);
		return computeResults();
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

	private void createFeaturesNodes() {
		featureNodes = new FeatureNode[numberOfFriendships - 1][];
		for (int i = 0; i < featureNodes.length; i++) {
			ArrayList<FeatureNode> featureNodesList = new ArrayList<FeatureNode>();
			double[] fv = feat.get(i + 1).getFeatures();
			for (int j = 0; j < fv.length; j++) {
				if (fv[j] > 0) {
					featureNodesList.add(new FeatureNode(j + 1, 1));
				}
			}
			FeatureNode[] tempArray = new FeatureNode[featureNodesList.size()];
			for (int j = 0; j < tempArray.length; j++) {
				tempArray[j] = featureNodesList.get(j);
			}
			featureNodes[i] = tempArray;
		}
	}

	private boolean assignCircle(Integer queryId, Integer circleId) {
		SVM svm = new SVM();
		Problem problem = new Problem();
		int[] classes = new int[numberOfFriendships - 2];
		ArrayList<FeatureNode[]> examples = new ArrayList<FeatureNode[]>();
		FeatureNode[] query = null;
		for (int i = 1; i < feat.size(); i++) {
			if (i != queryId) {
				examples.add(featureNodes[i - 1]);
				boolean contains = feat.get(i).getCircles().contains(circleId);
				if (contains)
					classes[i - 1] = 1;
				else
					classes[i - 1] = 0;
			} else {
				query = featureNodes[i - 1];
			}
		}
		problem.createProblem(examples, classes);
		svm.svmTrain(problem);
		double assigned = svm.svmTestOne(query);
		return (assigned == 1 ? true : false);
	}

	private void removeFeatureVectorFromDataStructures(Integer queryId) {
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				removedFromCircles.add(circleID);
			}
		}
	}

	private void addFeatureVectorFromDataStructures(Integer queryId) {
		for (int i = 0; i < removedFromCircles.size(); i++) {
			circles.get(removedFromCircles.get(i)).add(queryId);
		}
		removedFromCircles.clear();
	}

	private ResultPair computeResults() {
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
		double Precision;
		if (TP + FP == 0)
			Precision = 1;
		else
			Precision = TP / (TP + FP);
		double Recall;
		if (TP + FN == 0)
			Recall = 1;
		else
			Recall = TP / (TP + FN);
		double F1;
		if (Precision == 0 && Recall == 0)
			F1 = 0;
		else
			F1 = (2 * Precision * Recall) / (Precision + Recall);
		System.out.print("Precision : " + Precision);
		System.out.print("\tRecall : " + Recall);
		System.out.print("\tF1 : " + F1);
		ResultPair res = new ResultPair();
		res.setF1(F1);
		for (Integer circleId : answerCircles.keySet()) {
			for (Integer id : answerCircles.get(circleId).getCircleMembers()) {
				if (circles.get(circleId).getCircleMembers().contains(id))
					TP++;
				else
					FP++;
			}
		}
		double BER = 0;
		for (Integer circleId : circles.keySet()) {
			FN = 0;
			FP = 0;
			for (Integer id : circles.get(circleId).getCircleMembers()) {
				if (!answerCircles.get(circleId).getCircleMembers()
						.contains(id))
					FN++;
			}
			for (Integer id : answerCircles.get(circleId).getCircleMembers()) {
				if (!circles.get(circleId).getCircleMembers().contains(id))
					FP++;
			}
			if (answerCircles.get(circleId).getCircleMembers().size() != 0
					&& circles.get(circleId).getCircleMembers().size() != 0)
				BER += 1 - (FN
						/ circles.get(circleId).getCircleMembers().size() + FP
						/ answerCircles.get(circleId).getCircleMembers().size()) * (0.5);
			else if (answerCircles.get(circleId).getCircleMembers().size() != 0)
				BER += 1 - (FP / answerCircles.get(circleId).getCircleMembers()
						.size()) * (0.5);
			else if (circles.get(circleId).getCircleMembers().size() != 0)
				BER += 1 - (FN / circles.get(circleId).getCircleMembers()
						.size()) * (0.5);
		}
		res.setBER(BER / circles.size());
		System.out.println("      BER: " + BER / circles.size());
		return res;
	}
}
