package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dataStructures.Centroid;

public class Clope {
	int[][] occurence;
	int[] size, numOfTransactions, width;
	int profit = 0;

	int[] answer;
	int numberOfFeatures;
	Centroid c;
	int k, r;

	public void cluster(Centroid c, int k/* number of clusters */, int r/*
																		 * r is
																		 * an
																		 * integer
																		 * constant
																		 * usually
																		 * set
																		 * to 2
																		 */) {
		this.c = c;
		this.k = k;
		this.r = r;
		numberOfFeatures = c.getFeat().get(0).getFeatures().length;
		occurence = new int[k][numberOfFeatures];
		size = new int[k];
		numOfTransactions = new int[k];
		width = new int[k];
		answer = new int[c.getFeat().size()];

		// phase 1-initialization
		for (int i = 0; i < k; i++)
			for (int j = 0; j < numberOfFeatures; j++)
				occurence[i][j] = 0;
		for (int i = 0; i < k; i++) {
			size[i] = 0;
			numOfTransactions[i] = 0;
			width[i] = 0;
		}
		for(int i = 0;i < c.getFeat().size();i++)
			answer[i] = -1;
		for (int i : c.getFeat().keySet()) {
			if(i == 0)
				continue;
			int j = getBestCircle(i, k, r);
			profit += DeltaAdd(j, i, r);
			answer[i] = j;

			double[] tmp = c.getFeat().get(i).getFeatures();
			for (int l = 0; l < tmp.length; l++)
				size[j] += tmp[l];
			for (int l = 0; l < tmp.length; l++)
				if (tmp[l] > 0) {
					if (occurence[j][l] == 0)
						width[j]++;
					occurence[j][l] += tmp[l];
				}
			numOfTransactions[j]++;
		}

		// phase 2-iteration
		boolean moved;
		do {
			moved = false;
			for (int i : c.getFeat().keySet()) {
				if(i == 0)
					continue;
				int j = getBestCircle(i, answer[i], k, r);
				if (j != answer[i]) {
					moved = true;

					double[] tmp = c.getFeat().get(i).getFeatures();
					for (int l = 0; l < tmp.length; l++) {
						size[j] += tmp[l];
						size[answer[i]] -= tmp[l];
					}
					for (int l = 0; l < tmp.length; l++)
						if (tmp[l] > 0) {
							if (occurence[j][l] == 0)
								width[j]++;
							occurence[j][l] += tmp[l];
							occurence[answer[i]][l] -= tmp[l];
							if (occurence[answer[i]][l] == 0)
								width[answer[i]]--;
						}
					numOfTransactions[j]++;
					numOfTransactions[answer[i]]--;
					answer[i] = j;
				}
			}
		} while (moved);
		writeCirclesToFile();
	}

	private int getBestCircle(int t, int c, int k, int r) {// best cluster for
															// moving
															// transaction t
															// from cluster c to
															// that cluster
		double best = -1;
		int bestID = 0;
		double tmp = DeltaRem(c, t, r);
		for (int i = 0; i < k; i++)
			if (DeltaAdd(i, t, r) - tmp > best) {
				best = DeltaAdd(i, t, r) - tmp;
				bestID = i;
			}
		return bestID;
	}

	private double DeltaAdd(int circle, int t, int r) {// difference of proft
														// after adding
														// transaction t ro
														// cluster c
		// TODO Auto-generated method stub
		double S_new = size[circle];
		double[] tmp = c.getFeat().get(t).getFeatures();
		for (int i = 0; i < numberOfFeatures; i++)
			S_new += tmp[i];
		int W_new = width[circle];
		for (int i = 0; i < numberOfFeatures; i++)
			if (occurence[circle][i] == 0 && tmp[i] != 0)
				W_new++;

		return S_new * (numOfTransactions[circle] + 1) / Math.pow(W_new, r)
				- size[circle] * numOfTransactions[circle]
				/ Math.pow(width[circle], r);
	}

	private double DeltaRem(int circle, int t, int r) {
		double S_new = size[circle];
		double[] tmp = c.getFeat().get(t).getFeatures();
		for (int i = 0; i < numberOfFeatures; i++)
			S_new -= tmp[i];

		int W_new = width[circle];
		for (int i = 0; i < tmp.length; i++)
			if (occurence[circle][i] == tmp[i] && tmp[i] != 0)
				W_new--;

		return size[circle] * (numOfTransactions[circle])
				/ Math.pow(width[circle], r) - S_new
				* (numOfTransactions[circle] - 1) / Math.pow(W_new, r);
	}

	private int getBestCircle(int t, int k, int r) {// best cluster to put
													// transaction t in
		double best = -1;
		int bestID = 0;
		for (int i = 0; i < k; i++)
			if (DeltaAdd(i, t, r) > best) {
				best = DeltaAdd(i, t, r);
				bestID = i;
			}
		return bestID;
	}

	public void writeCirclesToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < k; i++) {
				sb.append("circle" + i + ": ");
				for (int j = 1; j < c.getFeat().size(); j++)
					if (answer[j] == i)
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
