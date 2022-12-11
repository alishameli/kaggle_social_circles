package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dataStructures.Centroid;
import dataStructures.FeatureVector;

public class Hadi1 {

	Centroid c;
	int differenceMatrix[][];
	int differenceMatrixIndex[][];
	static HashMap<Integer, FeatureVector> feat;
	int percentage;

	public void findEdges(Centroid c, int percentage) {
		this.c = c;
		this.percentage = percentage;
		feat = c.getFeat();
		differenceMatrix = new int[feat.size()][feat.size()];
		differenceMatrixIndex = new int[feat.size()][feat.size()];
		for (int i = 0; i < feat.size(); i++)
			for (int j = 0; j < feat.size(); j++)
				differenceMatrix[i][j] = 0;
		for (int i = 0; i < feat.size(); i++)
			for (int j = i + 1; j < feat.size(); j++)
				findDifference(i, j);
		for (int i = 1; i < feat.size(); i++)
			SORT(i);
		for (int i = 1; i < feat.size(); i++)
			makeEdges(i);
		printToFile();
		// for(int j = 0;j < feat.size();j++)
		// System.out.print(differenceMatrixIndex[1][j] + " " +
		// differenceMatrix[1][differenceMatrixIndex[1][j]] + " ");
		// System.out.print(feat.get(0).getFeatures().length);
		// System.out.println();
	}

	private void makeEdges(int i) {
		for (int j = 0; j < (double) feat.size()
				* ((double) percentage / 100.0); j++)
			if (differenceMatrixIndex[i][j] != i)
				if (!c.getAdj().get(i).contains(differenceMatrixIndex[i][j])) {
					c.getAdj().get(i).add(differenceMatrixIndex[i][j]);
					c.getAdj().get(differenceMatrixIndex[i][j]).add(i);
				}
	}

	private void SORT(int i) {
		int[] values = differenceMatrix[i];
		int[] output = new int[values.length];
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (int n = 0; n < values.length; n++) {
			map.put(values[n] * values.length + n, n);
		}
		int n = 0;
		for (Integer index : map.values()) {
			output[n++] = index;
		}
		differenceMatrixIndex[i] = output;
	}

	private void findDifference(int v, int u) {
		int LENGTH = feat.get(0).getFeatures().length;
		for (int i = 0; i < LENGTH; i++)
			if (feat.get(v).getFeatures()[i] != feat.get(u).getFeatures()[i])
				differenceMatrix[u][v]++;
		differenceMatrix[v][u] = differenceMatrix[u][v];
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".EDGES"));
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < feat.size(); i++)
				for (int j = 0; j < c.getAdj().get(i).getFriends().length; j++)
					if (i < c.getAdj().get(i).getFriends()[j] && c.getAdj().get(i).getFriends()[j] != 0)
						sb.append(c.getMappedIDsVirtualToReal().get(i)
								+ " "
								+ c.getMappedIDsVirtualToReal().get(c.getAdj().get(i)
										.getFriends()[j]) + "\n");
			sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
