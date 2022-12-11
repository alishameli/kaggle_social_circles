package dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class Snapshot {
	private Centroid centroid;
	private HashMap<Integer, Circle> circles;
	private int[] numberOfElementsOfEachCircle;
	private HashMap<Integer, FeatureVector> feat;
	private HashMap<Integer, Friends> adj;
	private HashSet<Integer> deleted;

	private int percentage;
	private long seed;

	public Snapshot(Centroid centroid, int percentage, long seed) {
		this.centroid = centroid;
		this.percentage = percentage;
		this.seed = seed;
		circles = new HashMap<Integer, Circle>();
		feat = new HashMap<Integer, FeatureVector>();
		adj = new HashMap<Integer, Friends>();
		deleted = new HashSet<Integer>();
		numberOfElementsOfEachCircle = new int[centroid.getCircles().size()];
		deleteNodesAndMakeSnapshot();
	}

	private void fillNumberOfElementsOfEachCircle() {
		for (Map.Entry<Integer, Circle> entry : centroid.getCircles()
				.entrySet())
			numberOfElementsOfEachCircle[entry.getKey()] = entry.getValue()
					.getCircleMembers().size();
	}

	private void deleteNodesAndMakeSnapshot() {
		int size = centroid.getFriendsSize();
		int deleteSize = (size * percentage) / 100;
		Integer[] friends = centroid
				.getAdj()
				.get(centroid.getMappedIDsRealToVirtual().get(centroid.getID()))
				.getFriends();

		fillNumberOfElementsOfEachCircle();

		shuffleArray(friends);
		int counterOfDeletedSize = 0;
		for (int i = 0; counterOfDeletedSize < deleteSize; i++) {
			if (i == friends.length) 
				break;
			int temp = friends[i];
			boolean flg = true;
			if (centroid.getFeat().get(temp).getCircles().size() == 0)
				continue;
			for (int j = 0; j < centroid.getFeat().get(temp).getCircles()
					.size(); j++) {
				int tempCircle = centroid.getFeat().get(temp).getCircles()
						.get(j);
				if (numberOfElementsOfEachCircle[tempCircle] == 1) {
					flg = false;
					break;
				}
			}
			if (!flg)
				continue;
			counterOfDeletedSize++;
			for (int j = 0; j < centroid.getFeat().get(temp).getCircles()
					.size(); j++) {
				int tempCircle = centroid.getFeat().get(temp).getCircles()
						.get(j);
				numberOfElementsOfEachCircle[tempCircle]--;
			}
			deleted.add(temp);
		}

		// recreating circles
		for (Map.Entry<Integer, Circle> entry : centroid.getCircles()
				.entrySet()) {
			Circle c = new Circle();
			Circle circle = entry.getValue();
			Integer key = entry.getKey();
			ArrayList<Integer> circleMembers = circle.getCircleMembers();
			for (int i = 0; i < circleMembers.size(); i++) {
				Integer member = circleMembers.get(i);
				if (!deleted.contains(member)) {
					c.add(member);
				}
			}
			circles.put(key, c);
		}
		// recreating feat
		for (Map.Entry<Integer, FeatureVector> entry : centroid.getFeat()
				.entrySet()) {
			Integer key = entry.getKey();
			FeatureVector value = entry.getValue();
			if (!deleted.contains(key)) {
				feat.put(key, value);
			}
		}

		// recreating adj
		for (Map.Entry<Integer, Friends> entry : centroid.getAdj().entrySet()) {
			Integer key = entry.getKey();
			Friends value = entry.getValue();
			Integer[] f = value.getFriends();
			Friends ans = new Friends();
			for (int i = 0; i < f.length; i++) {
				Integer friend = f[i];
				if (key == centroid.getMappedIDsRealToVirtual().get(
						centroid.getID())
						&& deleted.contains(friend))
					continue;
				else if (deleted.contains(key)
						&& friend == centroid.getMappedIDsRealToVirtual().get(
								centroid.getID()))
					continue;
				ans.add(friend);
			}
			adj.put(key, ans);
		}
	}

	// Implementing Fisher–Yates shuffle
	private void shuffleArray(Integer[] integers) {
		Random rnd = new Random(seed);
		for (int i = integers.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = integers[index];
			integers[index] = integers[i];
			integers[i] = a;
		}
	}

	public HashMap<Integer, Circle> getCircles() {
		return circles;
	}

	public HashMap<Integer, FeatureVector> getFeat() {
		return feat;
	}

	public HashMap<Integer, Friends> getAdj() {
		return adj;
	}

	public HashSet<Integer> getDeleted() {
		return deleted;
	}

	public int getPercentage() {
		return percentage;
	}

	public int getNumberOfFeatures() {
		return feat.get(centroid.getMappedIDsRealToVirtual().get(centroid.getID())).getFeatures().length;
	}

	public String getCenetroidID() {
		return centroid.getID();
	}
}
