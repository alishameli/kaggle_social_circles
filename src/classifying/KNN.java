package classifying;

import java.util.ArrayList;

import dataStructures.DistanceType;
import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class KNN {
	ArrayList<FeatureVector> a;

	public int test(Snapshot snap, final FeatureVector query, int k,
			DistanceType distanceType) throws Exception {
		a = new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			a.add(value);
		}

		int numOfCircles = snap.getCircles().size();

		int[] arr = new int[numOfCircles];
		for (int i = 0; i < numOfCircles; i++)
			arr[i] = 0;

		// int k = 8;
		FeatureVector[] KNearest = getKNearest(k, query, distanceType);

		for (int i = 0; i < k; i++) {
			if (KNearest[i] == null)
				break;
			for (int j = 0; j < KNearest[i].getCircles().size(); j++) {
				int c = KNearest[i].getCircles().get(j);
				arr[c]++;
			}
		}
		int bestID = 0, max = -1;
		for (int i = 0; i < numOfCircles; i++) {
			if (arr[i] > max) {
				max = arr[i];
				bestID = i;
			}
		}
		int secondID = 0;
		max = -1;
		for (int i = 0; i < numOfCircles; i++)
			if (arr[i] > max && i != bestID) {
				max = arr[i];
				secondID = i;
			}
		return new Pair(bestID, secondID).getFirstCircle();

	}

	private FeatureVector[] getKNearest(int k, FeatureVector query,
			DistanceType distanceType) throws Exception {
		FeatureVector[] KNearest = new FeatureVector[k];
		int size = a.size();
		boolean[] checked = new boolean[size];
		double[] distance = new double[size];
		for (int i = 0; i < distance.length; i++) {
			distance[i] = a.get(i).getDistanceFrom(query, distanceType);
		}
		for (int i = 0; i < k; i++) {
			int nearestIndex = -1;
			double nearestDistance = Double.MAX_VALUE;
			for (int j = 0; j < distance.length; j++) {
				if (distance[j] < nearestDistance
						&& a.get(j).getCircles().size() > 0) {
					if (!checked[j]) {
						nearestDistance = distance[j];
						nearestIndex = j;
					}
				}
			}
			if (nearestIndex == -1)
				break;
			checked[nearestIndex] = true;
			KNearest[i] = a.get(nearestIndex);
		}
		return KNearest;
	}
}