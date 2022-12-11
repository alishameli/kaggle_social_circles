package classifying;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import dataStructures.Circle;
import dataStructures.DistanceType;
import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class Rocchio {

	ArrayList<FeatureVector> a;

	public Rocchio() {
		a = new ArrayList<FeatureVector>();
	}

	private FeatureVector findMeanVector(ArrayList<FeatureVector> fv)
			throws Exception {
		int size = fv.size();
		int vectorSize = fv.get(0).getSize();
		Double[] meanVector = new Double[vectorSize];
		for (int i = 0; i < meanVector.length; i++) {
			meanVector[i] = new Double(0);
		}
		for (Iterator<FeatureVector> iterator = fv.iterator(); iterator
				.hasNext();) {
			FeatureVector featureVector = (FeatureVector) iterator.next();
			double[] features = featureVector.getFeatures();
			if (features.length != vectorSize)
				throw new Exception("FeatureVectors have different sizes!");
			for (int i = 0; i < features.length; i++) {
				meanVector[i] += features[i] / (double) size;
			}
		}
		return new FeatureVector(meanVector);
	}

	public int test(Snapshot snap, FeatureVector query,
			DistanceType distanceType) throws Exception {
		// a: arrayList which includes that average vector of each circle,
		// query: query vector
		for (Map.Entry<Integer, Circle> entry : snap.getCircles().entrySet()) {
			Circle value = entry.getValue();
			ArrayList<Integer> members = value.getCircleMembers();

			ArrayList<FeatureVector> tmp = new ArrayList<FeatureVector>();
			for (int i = 0; i < members.size(); i++)
				tmp.add(snap.getFeat().get(members.get(i)));

			if (tmp.size() != 0)
				a.add(findMeanVector(tmp));
		}

		double minDis = Double.MAX_VALUE;
		int bestID = 0;

		for (int i = 0; i < a.size(); i++)
			if (query.getDistanceFrom(a.get(i), distanceType) < minDis) {
				minDis = query.getDistanceFrom(a.get(i), distanceType);
				bestID = i;
			}
		return bestID;
	}

}
