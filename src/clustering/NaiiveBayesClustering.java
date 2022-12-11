package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;

import dataStructures.Centroid;
import dataStructures.FeatureVector;

public class NaiiveBayesClustering {

	static int numberOfFeatures;
	static HashMap<Integer, FeatureVector> feat;
	static int numberOfIterationsForSeed;
	static int k; // number of clusters
	static Centroid c;
	static int[] seeds;
	static int[] cluster;
	static BigDecimal[][] pp;
	static BigDecimal[][] p;
	static int[] numberOfPeopleInEachCircle;
	static int[] numberOfFeaturesInEachCircle;
	static int[][] numberOfEachFeatureInEachCircle;
	static int numberOfPeopleWhoHaveACluster = 0;

	public void test(Centroid c, int k, int numberOfIterationsForSeed) {
		this.k = k;
		this.c = c;
		this.numberOfIterationsForSeed = numberOfIterationsForSeed;
		this.numberOfFeatures = c.getFeat().get(0).getFeatures().length;
		feat = c.getFeat();
		findSeeds();
		cluster = new int[feat.size()];
		for (int i = 0; i < feat.size(); i++)
			cluster[i] = -1;
		for (int i = 0; i < k; i++)
			cluster[seeds[i]] = i;
		numberOfPeopleWhoHaveACluster = k;
		makeDataStructure();
		p = new BigDecimal[k][feat.size()];
		pp = new BigDecimal[k][feat.size()];
		for (int i = 0; i < k; i++)
			for (int j = 0; j < feat.size(); j++) {
				p[i][j] = BigDecimal.ZERO;
				pp[i][j] = BigDecimal.ZERO;
			}
		for (int i = 1; i < feat.size(); i++)
			if (cluster[i] == -1)
				for (int j = 0; j < k; j++)
					findPP(i, j);
		for (int i = 0; i < feat.size() - 1 - k; i++) {
			findP();
			BigDecimal bestP = new BigDecimal("-1000000000");
			int bestId = 0;
			int bestCluster = -1;
			for (int j = 1; j < feat.size(); j++) {
				if (cluster[j] != -1)
					continue;
				int maxClusterP = findMaxClusterP(j);
				if (bestP.compareTo(p[maxClusterP][j]) == -1) {
					bestCluster = maxClusterP;
					bestP = p[maxClusterP][j];
					bestId = j;
				}
			}
			cluster[bestId] = bestCluster;
			numberOfPeopleWhoHaveACluster++;
			updateDataStructure(bestId);
			for (int j = 1; j < feat.size(); j++) {
				if (cluster[j] != -1)
					continue;
				findPP(j, bestCluster);
			}
		}
		writeCirclesToFile();
	}

	private void updateDataStructure(int bestId) {
		int circle = cluster[bestId];
		numberOfPeopleInEachCircle[circle]++;
		for (int i = 0; i < numberOfFeatures; i++) {
			numberOfEachFeatureInEachCircle[circle][i] += feat.get(bestId)
					.getFeatures()[i];
			numberOfFeaturesInEachCircle[circle] += feat.get(bestId)
					.getFeatures()[i];
		}
	}

	private void makeDataStructure() {
		numberOfPeopleInEachCircle = new int[k];
		for (int i = 0; i < k; i++)
			numberOfPeopleInEachCircle[i] = 1;
		numberOfFeaturesInEachCircle = new int[k];
		for (int i = 0; i < k; i++)
			numberOfFeaturesInEachCircle[i] = 0;
		numberOfEachFeatureInEachCircle = new int[k][c.getNumberOfFeatures()];
		for (int i = 0; i < k; i++)
			for (int j = 0; j < numberOfFeatures; j++)
				numberOfEachFeatureInEachCircle[i][j] = 0;
		for (int circle = 0; circle < k; circle++) {
			int v = seeds[circle];
			for (int i = 0; i < numberOfFeatures; i++) {
				numberOfEachFeatureInEachCircle[circle][i] += feat.get(v)
						.getFeatures()[i];
				numberOfFeaturesInEachCircle[circle] += feat.get(v)
						.getFeatures()[i];
			}
		}
	}

	int findMaxClusterP(int v) {
		int bestId = -1;
		BigDecimal bestP = new BigDecimal("-1000000000");
		for (int i = 0; i < k; i++)
			if (bestP.compareTo(p[i][v]) == -1) {
				bestP = p[i][v];
				bestId = i;
			}
		return bestId;
	}

	void findPP(int v, int circle) {
		BigDecimal res = BigDecimal.ONE;
		res = res.divide(BigDecimal.valueOf(k));
		for (int i = 0; i < numberOfFeatures; i++) {
			if (feat.get(v).getFeatures()[i] > 0){
				res = res.multiply(BigDecimal.valueOf(1 + numberOfEachFeatureInEachCircle[circle][i]));
				int temp = numberOfPeopleInEachCircle[circle] + k;
				res = res.divide(BigDecimal.valueOf(temp), 200, RoundingMode.HALF_UP);
			}
		}
		pp[circle][v] = res;
	}

	void findP() {
		for (int i = 1; i < c.getFeat().size(); i++) {
			if (cluster[i] != -1)
				continue;
			BigDecimal sumPP = BigDecimal.ZERO;
			for (int j = 0; j < k; j++)
				sumPP = sumPP.add(pp[j][i]);
			for (int j = 0; j < k; j++)
				p[j][i] = pp[j][i].divide(sumPP, 200, RoundingMode.HALF_UP);
		}
	}

	public void findSeeds() {
		int difference = 0;
		int[] people = new int[feat.size() - 1];
		for (int i = 1; i < feat.size(); i++)
			people[i - 1] = i;
		for (int i = 0; i < numberOfIterationsForSeed; i++) {
			int count = 0;
			int[] tempSeeds = new int[k];
			shuffleArray(people);
			for (int j = 0; j < k; j++)
				tempSeeds[j] = people[j];
			for (int j = 0; j < k; j++)
				for (int l = 0; l < k; l++)
					for (int r = 0; r < numberOfFeatures; r++)
						if (feat.get(tempSeeds[j]).getFeatures()[r] != feat
								.get(tempSeeds[l]).getFeatures()[r])
							count++;
			if (i == 0) {
				difference = count;
				seeds = tempSeeds;
			} else if (difference < count) {
				difference = count;
				seeds = tempSeeds;
			}
		}
	}

	static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	public void writeCirclesToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < k; i++) {
				sb.append("circle" + i + ": ");
				for (int j = 1; j < feat.size(); j++)
					if (cluster[j] == i)
						sb.append(c.getMappedIDsVirtualToReal().get(j) + " ");
				sb.append("\n");
			}
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
