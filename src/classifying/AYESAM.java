package classifying;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.FeatureVector;

public class AYESAM {
	private HashMap<Integer, Circle> circles;
	private HashMap<Integer, Circle> answerCircles;
	private HashMap<Integer, FeatureVector> feat;
	private int numberOfCircles;
	private int[][] sumOfElementsOfEachCircle;
	private int[] sumOfCircle;
	private int[] numberOfFixedVectorsInCircles;
	private int numberOfFeatures;
	private ArrayList<Integer> removedFromCircles1 = new ArrayList<Integer>();
	private ArrayList<Integer> removedFromCircles2 = new ArrayList<Integer>();
	
	public void test(Centroid c) {
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		for (Integer id : c.getFeat().keySet()) {
			feat.put(id, c.getFeat().get(id));
		}

		numberOfCircles = c.getCircles().size();
		numberOfFeatures = c.getFeat().get(c.getID()).getSize();
		sumOfElementsOfEachCircle = new int[numberOfCircles][numberOfFeatures];
		sumOfCircle = new int[numberOfCircles];
		numberOfFixedVectorsInCircles = new int[numberOfCircles];
		if (feat.remove(Integer.valueOf(c.getID())) == null) {
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
		computeF1();
		printResultsToFile(c);
	}

	public void fillEssentialDataStructures() {
		for (Integer tempFeatureVectorID : feat.keySet()) {
			FeatureVector temp = feat.get(tempFeatureVectorID);
			ArrayList<Integer> tempCircle = temp.getCircles();
			for (int k = 0; k < tempCircle.size(); k++)
				numberOfFixedVectorsInCircles[temp.getCircles().get(k)]++;
			for (int j = 0; j < numberOfFeatures; j++)
				if (temp.getFeatures()[j] > 0) {
					for (int k = 0; k < tempCircle.size(); k++) {
						sumOfElementsOfEachCircle[temp.getCircles().get(k)][j] += temp
								.getFeatures()[j];
						sumOfCircle[temp.getCircles().get(k)] += temp
								.getFeatures()[j];
					}
				}
		}
	}

	private void removeFeatureVectorFromDataStructures1(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < query.getCircles().size(); i++) {
			numberOfFixedVectorsInCircles[query.getCircles().get(i)]--;
			for (int j = 0; j < query.getFeatures().length; j++) {
				sumOfElementsOfEachCircle[query.getCircles().get(i)][j] -= query
						.getFeatures()[j];
				sumOfCircle[query.getCircles().get(i)] -= query.getFeatures()[j];
			}
		}
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				removedFromCircles1.add(circleID);
			}
		}
	}

	private void addFeatureVectorFromDataStructures1(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < query.getCircles().size(); i++) {
			numberOfFixedVectorsInCircles[query.getCircles().get(i)]++;
			for (int j = 0; j < query.getFeatures().length; j++) {
				sumOfElementsOfEachCircle[query.getCircles().get(i)][j] += query
						.getFeatures()[j];
				sumOfCircle[query.getCircles().get(i)] += query.getFeatures()[j];
			}
		}
		for (int i = 0; i < removedFromCircles1.size(); i++) {
			circles.get(removedFromCircles1.get(i)).add(queryId);
		}
		removedFromCircles1.clear();
	}

	private void removeFeatureVectorFromDataStructures2(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < query.getCircles().size(); i++) {
			numberOfFixedVectorsInCircles[query.getCircles().get(i)]--;
			for (int j = 0; j < query.getFeatures().length; j++) {
				sumOfElementsOfEachCircle[query.getCircles().get(i)][j] -= query
						.getFeatures()[j];
				sumOfCircle[query.getCircles().get(i)] -= query.getFeatures()[j];
			}
		}
		for (Integer circleID : circles.keySet()) {
			if (circles.get(circleID).getCircleMembers().contains(queryId)) {
				circles.get(circleID).getCircleMembers().remove(queryId);
				removedFromCircles2.add(circleID);
			}
		}
	}

	private void addFeatureVectorFromDataStructures2(Integer queryId) {
		FeatureVector query = feat.get(queryId);
		for (int i = 0; i < query.getCircles().size(); i++) {
			numberOfFixedVectorsInCircles[query.getCircles().get(i)]++;
			for (int j = 0; j < query.getFeatures().length; j++) {
				sumOfElementsOfEachCircle[query.getCircles().get(i)][j] += query
						.getFeatures()[j];
				sumOfCircle[query.getCircles().get(i)] += query.getFeatures()[j];
			}
		}
		for (int i = 0; i < removedFromCircles2.size(); i++) {
			circles.get(removedFromCircles2.get(i)).add(queryId);
		}
		removedFromCircles2.clear();
	}
	
	private int checkForSmallCircles(Integer queryId) {
		boolean runNaiive = false;
		for (Integer circleId : circles.keySet()) {
			if (circles.get(circleId).getCircleMembers().size() == 2
					&& circles.get(circleId).getCircleMembers()
							.contains(queryId))
				runNaiive = true;
			if (circles.get(circleId).getCircleMembers().size() == 1
					&& !circles.get(circleId).getCircleMembers()
							.contains(queryId))
				runNaiive = true;
		}
		int res = -1;
		if (runNaiive) {
			removeFeatureVectorFromDataStructures1(queryId);
			int ans = naiiveBayes(feat.get(queryId));
			if (circles.get(Integer.valueOf(ans)).getCircleMembers().size() == 1)
				res = ans;
			addFeatureVectorFromDataStructures1(queryId);
		}
		return res;
	}

	private void findQueryCircles(Integer queryId) {
		int smallCircle = checkForSmallCircles(queryId);
		if (smallCircle >= 0)
			answerCircles.get(smallCircle).add(queryId);

		removeFeatureVectorFromDataStructures1(queryId);
		for (Integer circleId : circles.keySet()) {
			if (circles.get(circleId).getCircleMembers().size() == 0) {
				answerCircles.get(circleId).add(queryId);
				continue;
			}
			if (circles.get(circleId).getCircleMembers().size() == 1)
				continue;
			boolean assigned = assignCircle(queryId, circleId);
			if (assigned)
				answerCircles.get(circleId).add(queryId);
		}
		addFeatureVectorFromDataStructures1(queryId);
	}

	private boolean assignCircle(Integer queryId, Integer circleId) {
		ArrayList<Integer> circleMembers = circles.get(circleId)
				.getCircleMembers();
		int count = 0;
		for (int i = 0; i < circleMembers.size(); i++) {
			Integer helper = circleMembers.get(i);
			removeFeatureVectorFromDataStructures2(helper);
			double queryProb = naiiveBayes(feat.get(queryId), circleId);
			double helperProb = naiiveBayes(feat.get(helper), circleId);
			if(circleId == 0 && queryId == 11){
//				System.out.println(queryProb + " " + helperProb);
			}
			if (queryProb >= helperProb)
				count++;
			addFeatureVectorFromDataStructures2(helper);
		}
		if (count == circleMembers.size())
			return true;
		return false;
	}

	private double naiiveBayes(FeatureVector query, Integer circleId) {
		double score = 0;
		score += Math.log10((double) numberOfFixedVectorsInCircles[circleId]
				/ (double) (feat.size() - 1));
		for (int j = 0; j < numberOfFeatures; j++) {
			if (query.getFeatures()[j] > 0) {
				double numberOfCurrentFeature = query.getFeatures()[j];
				score += numberOfCurrentFeature
						* Math.log10((double) (sumOfElementsOfEachCircle[circleId][j] + 1)
								/ (double) (sumOfCircle[circleId] + numberOfFeatures));
			}
		}
		return score;
	}

	private int naiiveBayes(FeatureVector query) {
		double max = -1000 * 1000;
		int idMax = -1;
		for (int i = 0; i < numberOfCircles; i++) {
			if (numberOfFixedVectorsInCircles[i] == 0)
				continue;
			double score = naiiveBayes(query, i);
			if (score > max) {
				max = score;
				idMax = i;
			}
		}
		return idMax;
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
	
	private double computeF1(){
		double TP = 0, FP = 0, TN = 0;
		for(Integer circleId : answerCircles.keySet()){
			for (Integer id : answerCircles.get(circleId).getCircleMembers()) {
				if(circles.get(circleId).getCircleMembers().contains(id))
					TP++;
				else
					FP++;
			}			
		}
		for(Integer circleId : circles.keySet()){
			for(Integer id : circles.get(circleId).getCircleMembers()){
				if(!answerCircles.get(circleId).getCircleMembers().contains(id))
					TN++;
			}
		}
		double Precision = TP / (TP + FP);
		double Recall = TP / (TP + TN);
		double F1 = (2 * Precision * Recall) / (Precision + Recall);
		System.out.println("Precision : " + Precision);
		System.out.println("Recall : " + Recall);
		System.out.println("F1 : " + F1);
		return F1;
	}
}
