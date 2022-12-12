package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import dataStructures.Centroid;

public class KMeans {

	double epsilon = 0.5;
	Centroid c;
	int k;
	long seed;
	int iterations;

	double[][] centerVector;
	AssignedCenter[] center;
	ArrayList<Container> clusters;

	public KMeans(Centroid c, int k, int iterations, long seeds) {
		this.c = c;
		this.k = k;
		if(k > c.getFriendsSize())
			this.k = 2;
		this.iterations = iterations;
		this.seed = seeds;
		centerVector = new double[this.k][c.getFeat().get(0).getSize()];
		clusters = new ArrayList<Container>(this.k);
		center = new AssignedCenter[c.getFriendsSize()];
	}

	public ArrayList<Container> find() {
		clusters = new ArrayList<Container>();
		for (int i = 0; i < k; i++) {
			clusters.add(new Container());
		}
		init();
		run();
		return clusters;
//		printToFile();
	}

	private void init() {
		randomCenters();
		for (int i = 0; i < c.getFriendsSize(); i++) {
			int id = i + 1;
			AssignedCenter cd = findBestCluster(id);
			int bestCenter = cd.centerId;
			center[i] = cd;
			clusters.get(bestCenter).arr.add(id);
		}
	}

	private AssignedCenter findBestCluster(int id) {
		double minDistance = Double.MAX_VALUE;
		int bestCenter = -1;
		double[] features = c.getFeat().get(id).getFeatures();
		for (int j = 0; j < centerVector.length; j++) {
			double distance = euclideanDistance(centerVector[j], features);
			if (distance < minDistance) {
				minDistance = distance;
				bestCenter = j;
			}
		}
		AssignedCenter cd = new AssignedCenter(bestCenter, minDistance);
		return cd;
	}

	private void randomCenters() {
		Random rnd = new Random(seed);
		int[] ids = new int[c.getFriendsSize()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = i;
		}
		for (int i = ids.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ids[index];
			ids[index] = ids[i];
			ids[i] = a;
		}
		for (int i = 0; i < centerVector.length; i++) {
			centerVector[i] = c.getFeat().get(ids[i] + 1).getFeatures();
		}
	}

	private void run() {
		boolean changed = true;
		int iters = 0;
		while (changed && iters < iterations) {
			double[][] centersCopy = new double[centerVector.length][centerVector[0].length];
			for (int i = 0; i < centersCopy.length; i++) {
				for (int j = 0; j < centersCopy[0].length; j++) {
					centersCopy[i][j] = centerVector[i][j];
				}
			}
			updateCenters();
			changed = false;
			for (int i = 0; i < centersCopy.length; i++) {
				if (euclideanDistance(centersCopy[i], centerVector[i]) > epsilon) {
					changed = true;
				}
			}
			if (!changed)
				break;
			for (int i = 0; i < c.getFriendsSize(); i++) {
				int id = i + 1;
				AssignedCenter bestCluster = findBestCluster(id);
				// System.out.println(cd.distance + "\t" + distances[i].distance
				// + "\t" + (cd.distance - distances[i].distance));
				clusters.get(center[i].centerId).arr.remove((Integer) id);
				center[i] = bestCluster;
				clusters.get(bestCluster.centerId).arr.add(id);
			}
			iters++;
		}

	}

	private void updateCenters() {
		for (int i = 0; i < centerVector.length; i++) {
			int clusterSize = clusters.get(i).size();
			int featuresSize = centerVector[0].length;
			for (int j = 0; j < featuresSize; j++) {
				double sum = 0;
				centerVector[i][j] = 0;
				for (int k = 0; k < clusterSize; k++) {
					int id = clusters.get(i).arr.get(k);
					sum += c.getFeat().get(id).getFeatures()[j] / clusterSize;
				}
				centerVector[i][j] = sum;
			}
		}
	}

	public static double euclideanDistance(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException(
					"The dimensions have to be equal!");
		}

		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}

		return Math.sqrt(sum);
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < clusters.size(); i++) {
				if (clusters.get(i).arr.size() > 5) {
					sb.append("circle" + i + ": ");
					for (int j = 0; j < clusters.get(i).arr.size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								clusters.get(i).arr.get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									clusters.get(i).arr.get(j))
									+ " ");
				}
				if (sb.length() > 0) {
					sb.setLength(sb.length() - 1);
					sb.append("\n");
				}
			}
			// if(!c.getID().equals("2738") && !c.getID().equals("24758") &&
			// !c.getID().equals("1813") && !c.getID().equals("3077"))
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class AssignedCenter {
	int centerId;
	double distance;

	public AssignedCenter(int centerId, double distance) {
		this.centerId = centerId;
		this.distance = distance;
	}
}
