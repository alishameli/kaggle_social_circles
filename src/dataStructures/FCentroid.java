package dataStructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FCentroid {

	private String ID;
	private boolean virtualCircle;
	private int numberOfFeatures;
	private HashMap<Integer, FeatureVector> feat = new HashMap<Integer, FeatureVector>();
	private HashMap<Integer, Friends> adj = new HashMap<Integer, Friends>();
	private HashMap<Integer, String> mappedIDsVirtualToReal = new HashMap<Integer, String>();
	private HashMap<String, Integer> mappedIDsRealToVirtual = new HashMap<String, Integer>();
	private HashMap<Integer, Circle> circles = new HashMap<Integer, Circle>();
	private ArrayList<Integer> usefullFeatures = new ArrayList<Integer>();
	private ArrayList<String> featNames = new ArrayList<String>();
	private String facebookPath;
	private double featureFactor;
	private double friendshipFactor;

	private Centroid centroid;
	private CentroidBuilder builder;

	public FCentroid(String ID, double friendshipFactor, double featureFactor,
			String path, boolean virtualCircle, boolean addFriendshipsToVectors)
			throws NumberFormatException, IOException {
		this.ID = ID;
		mappedIDsRealToVirtual.put(this.ID, 0);
		this.virtualCircle = virtualCircle;
		this.featureFactor = featureFactor;
		this.friendshipFactor = friendshipFactor;
		this.facebookPath = path;
		findFeatNames();
		findFeatures();
		findFriendsFeatures();
		findFriends();
		findCircles();
		if (addFriendshipsToVectors)
			addFriendshipToVector();
		build();
	}

	private void findFeatNames() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(facebookPath + ID + ".featnames");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			featNames.add(Ids[1]);
		}
		BR.close();
	}

	private void findFeatures() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(facebookPath + ID + ".egofeat");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		ArrayList<Double> temp = new ArrayList<Double>();
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int j = 0; j < Ids.length; j++)
				if (Double.valueOf(Ids[j]) > 0
						&& !featNames.get(j).contains("birthday")
						&& !featNames.get(j).contains("first_name")
						&& !featNames.get(j).contains("last_name")
						&& !featNames.get(j).contains("gender")) {
					usefullFeatures.add(j);
					temp.add(Double.valueOf(featureFactor));
				}
		}
		numberOfFeatures = temp.size();
		FeatureVector fv = new FeatureVector(Arrays.copyOf(temp.toArray(),
				numberOfFeatures, Double[].class));
		feat.put(mappedIDsRealToVirtual.get(ID), fv);
		BR.close();
	}

	private void findFriendsFeatures() throws NumberFormatException,
			IOException {
		FileReader FR = new FileReader(facebookPath + ID + ".feat");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		int id = 1;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			Double[] friendsFeatures = new Double[numberOfFeatures];
			for (int j = 0; j < usefullFeatures.size(); j++)
				friendsFeatures[j] = Double
						.valueOf(Ids[usefullFeatures.get(j) + 1]);
			for (int j = 0; j < numberOfFeatures; j++) {
				if (friendsFeatures[j] == (double) 1)
					friendsFeatures[j] = featureFactor;
			}
			FeatureVector fv = new FeatureVector(friendsFeatures);
			mappedIDsRealToVirtual.put(Ids[0], id);
			mappedIDsVirtualToReal.put(id, Ids[0]);
			id++;
			feat.put(mappedIDsRealToVirtual.get(Ids[0]), fv);
		}
		BR.close();
	}

	private void findFriends() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(facebookPath + ID + ".edges");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			String v1 = Ids[0];
			String u1 = Ids[1];
			int v = mappedIDsRealToVirtual.get(v1);
			int u = mappedIDsRealToVirtual.get(u1);
			if (adj.containsKey(v)) {
				adj.get(v).add(u);
			} else {
				Friends f = new Friends();
				f.add(mappedIDsRealToVirtual.get(ID));
				f.add(u);
				adj.put(v, f);
			}
			if (adj.containsKey(u)) {
				adj.get(u).add(v);//
			} else {
				Friends f = new Friends();
				f.add(mappedIDsRealToVirtual.get(ID));
				f.add(v);//
				adj.put(u, f);
			}
		}
		Friends egoFriends = new Friends();
		for (Integer key : adj.keySet()) {
			egoFriends.add(key);
		}
		adj.put(mappedIDsRealToVirtual.get(ID), egoFriends);
		for (Integer key : feat.keySet()) {
			if (!adj.containsKey(key)) {
				Friends f = new Friends();
				f.add(mappedIDsRealToVirtual.get(ID));
				adj.put(key, f);
				adj.get(mappedIDsRealToVirtual.get(ID)).add(key);
			}
		}
		BR.close();
	}

	private void findCircles() throws NumberFormatException, IOException {
		FileReader FR = new FileReader(facebookPath + ID + ".circles");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		int counter = 0;
		while ((Text = BR.readLine()) != null) {
			Circle c = new Circle();
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int i = 1; i < Ids.length; i++) {
				int member = mappedIDsRealToVirtual.get(Ids[i]);
				feat.get(member).assignCircle(counter);
				c.add(member);
			}
			circles.put(counter++, c);
		}
		BR.close();

		if (virtualCircle) {
			Circle VirtualCircle = new Circle();
			for (Integer member : feat.keySet()) {
				if (feat.get(member).getCircles().size() < 1) {
					VirtualCircle.add(member);
					feat.get(member).assignCircle(counter);
				}
			}
			circles.put(counter++, VirtualCircle);
		}
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
