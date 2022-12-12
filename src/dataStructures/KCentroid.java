// this class has written for Kaggle data set.

package dataStructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KCentroid {

	private String ID;
	private boolean virtualCircle;
	private int numberOfFeatures;
	private HashMap<String, ArrayList<String>> stringFeatures;
	private HashMap<String, Integer> featNameToFeat = new HashMap<String, Integer>();
	private HashMap<Integer, FeatureVector> feat = new HashMap<Integer, FeatureVector>();
	private HashMap<Integer, Friends> adj = new HashMap<Integer, Friends>();
	private HashMap<Integer, String> mappedIDsVirtualToReal = new HashMap<Integer, String>();
	private HashMap<String, Integer> mappedIDsRealToVirtual = new HashMap<String, Integer>();
	private HashMap<Integer, Circle> circles = new HashMap<Integer, Circle>();
	private ArrayList<String> people = new ArrayList<String>();
	private String kagglePath;
	private double featureFactor;
	private double friendshipFactor;
	private int dataSet;

	private Centroid centroid;
	private CentroidBuilder builder;

	public KCentroid(String ID, double friendshipFactor, double featureFactor,
			String path, boolean virtualCircle,
			boolean addFriendshipsToVectors,
			HashMap<String, ArrayList<String>> stringFeatures, int dataSet)
			throws NumberFormatException, IOException {
		this.dataSet = dataSet;
		this.ID = ID;
		this.stringFeatures = stringFeatures;
		mappedIDsRealToVirtual.put(this.ID, 0);
		this.virtualCircle = virtualCircle;
		this.featureFactor = featureFactor;
		this.friendshipFactor = friendshipFactor;
		this.kagglePath = path;
		findFriends();
		findFeatures();
		findCircles();
		if (addFriendshipsToVectors)
			addFriendshipToVector();
		build();
	}

	private void findFriends() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(kagglePath + ID + ".egonet");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		Friends f = new Friends();
		adj.put(0, f);
		people.add(ID);
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			String newNode = Ids[0].substring(0, Ids[0].indexOf(':'));
			if (!mappedIDsRealToVirtual.containsKey(newNode)) {
				mappedIDsRealToVirtual.put(newNode, people.size());
				mappedIDsVirtualToReal.put(people.size(), newNode);
				people.add(newNode);
			}
			adj.get(0).add(mappedIDsRealToVirtual.get(newNode));
			Friends f1 = new Friends();
			adj.put(mappedIDsRealToVirtual.get(newNode), f1);
			adj.get(mappedIDsRealToVirtual.get(newNode)).add(0);
			for (int i = 1; i < Ids.length; i++) {
				if (!mappedIDsRealToVirtual.containsKey(Ids[i])) {
					mappedIDsRealToVirtual.put(Ids[i], people.size());
					mappedIDsVirtualToReal.put(people.size(), Ids[i]);
					people.add(Ids[i]);
				}
				adj.get(mappedIDsRealToVirtual.get(newNode)).add(
						mappedIDsRealToVirtual.get(Ids[i]));
			}
		}
		BR.close();
	}

	private void findFeatures() {
//		int feature = 0;
//		for (int i = 0; i < people.size(); i++) {
//			String currentId = people.get(i);
//			for (int j = 0; j < stringFeatures.get(currentId).size(); j++) {
//				String newFeature = stringFeatures.get(currentId).get(j);
//				if (!featNameToFeat.containsKey(newFeature))
//					featNameToFeat.put(newFeature, feature++);
//			}
//		}
//		numberOfFeatures = feature;
//		for (int i = 0; i < people.size(); i++) {
//			String currentId = people.get(i);
//			Double[] temp = new Double[numberOfFeatures];
//			for (int j = 0; j < numberOfFeatures; j++)
//				temp[j] = 0.0;
//			for (int j = 0; j < stringFeatures.get(currentId).size(); j++) {
//				String newFeature = stringFeatures.get(currentId).get(j);
//				temp[featNameToFeat.get(newFeature)] = featureFactor;
//			}
//			FeatureVector fv = new FeatureVector(temp);
//			feat.put(mappedIDsRealToVirtual.get(currentId), fv);
//		}

		int feature = 0;
		for (int i = 0; i < 1; i++) {
			String currentId = people.get(i);
			for (int j = 0; j < stringFeatures.get(currentId).size(); j++) {
				String newFeature = stringFeatures.get(currentId).get(j);
				if (!featNameToFeat.containsKey(newFeature) /*
															 * &&
															 * !newFeature.contains
															 * ("birthday") &&
															 * !newFeature
															 * .contains
															 * ("first_name") &&
															 * !
															 * newFeature.contains
															 * ("last_name") &&
															 * !
															 * newFeature.contains
															 * ("middle_name")
															 * &&
															 * !newFeature.contains
															 * ("gender")
															 */)
					featNameToFeat.put(newFeature, feature++);
			}
		}
		numberOfFeatures = feature;
		for (int i = 0; i < people.size(); i++) {
			String currentId = people.get(i);
			Double[] temp = new Double[numberOfFeatures];
			for (int j = 0; j < numberOfFeatures; j++)
				temp[j] = 0.0;
			for (int j = 0; j < stringFeatures.get(ID).size(); j++) {
				String newFeature = stringFeatures.get(ID).get(j);
				if (stringFeatures.get(currentId).contains(newFeature))
					temp[featNameToFeat.get(newFeature)] = featureFactor;
			}
			FeatureVector fv = new FeatureVector(temp);
			feat.put(mappedIDsRealToVirtual.get(currentId), fv);
		}
	}

	private void findCircles() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(kagglePath + ID + ".circles");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		int counter = 0;
		while ((Text = BR.readLine()) != null) {
			Circle c = new Circle();
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int i = 1; i < Ids.length; i++) {
				int member = mappedIDsRealToVirtual.get(Ids[i]);
				if (member == 0)
					continue;
				feat.get(member).assignCircle(counter);
				c.add(member);
			}
			circles.put(counter++, c);
		}
		BR.close();
	}

	private void addFriendshipToVector() {
		for (Map.Entry<Integer, FeatureVector> entry : feat.entrySet()) {
			FeatureVector fv = entry.getValue();
			double[] expansions = new double[feat.size()];
			Integer[] friends = this.adj.get(entry.getKey()).getFriends();
			for (int i = 0; i < friends.length; i++) {
				expansions[friends[i]] = friendshipFactor;
			}
			fv.expandFeatures(expansions);
		}
	}

	public Centroid getCentroid() {
		return this.centroid;
	}

	private void build() {
		builder = new CentroidBuilder();
		builder.setVirtualCircle(virtualCircle);
		builder.setID(this.ID);
		builder.setNumberOfFeatures(numberOfFeatures);
		builder.setFriendshipFactor(friendshipFactor);
		builder.setFeatureFactor(featureFactor);
		builder.setCircles(circles);
		builder.setFeat(feat);
		builder.setMappedIDsRealToVirtual(mappedIDsRealToVirtual);
		builder.setMappedIDsVirtualToReal(mappedIDsVirtualToReal);
		builder.setAdj(adj);
		centroid = builder.build();
	}
}
