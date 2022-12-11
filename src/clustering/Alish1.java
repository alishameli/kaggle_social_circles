
package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataStructures.Centroid;
import dataStructures.DistanceType;

public class Alish1 {

	Centroid c;
	ArrayList<Integer> answer;
	int k;

	public void findCluster(Centroid c, int k) throws Exception {
		this.c = c;
		this.k = k;
		FileReader FR = new FileReader("C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\"
				+ c.getID() + ".txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text = BR.readLine();
		String Separator = "\\s+";
		String[] Ids = Text.split(Separator);
		BR.close();
		answer = new ArrayList<Integer>();
		
		System.out.println(Integer.parseInt(Ids[1]));
		answer.add(c.getMappedIDsRealToVirtual().get(Ids[1]));
		int highestPR = answer.get(0);

		double minDistance = 1000 * 1000 * 1000;
		int bestId1 = -1, bestId2 = -1;

		for (Integer u : c.getFeat().keySet())
			for (Integer v : c.getFeat().keySet()) {
				if (u != highestPR && v != highestPR && u != v
						&& c.getAdj().get(highestPR).contains(u)
						&& c.getAdj().get(highestPR).contains(v)
						&& c.getAdj().get(u).contains(v)) {
					double tmp = c
							.getFeat()
							.get(u)
							.getDistanceFrom(c.getFeat().get(v),
									DistanceType.JACCARDIAN);
					tmp += c.getFeat()
							.get(highestPR)
							.getDistanceFrom(c.getFeat().get(u),
									DistanceType.JACCARDIAN);
					tmp += c.getFeat()
							.get(highestPR)
							.getDistanceFrom(c.getFeat().get(v),
									DistanceType.JACCARDIAN);

					if (tmp < minDistance) {
						bestId1 = u;
						bestId2 = v;
						minDistance = tmp;
					}
				}
			}
		if (bestId1 != -1)
			answer.add(bestId1);
		if (bestId2 != -1)
			answer.add(bestId2);
		printToFile();
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			sb.append("circle0: ");
			for (int i = 0; i < answer.size(); i++)
				sb.append(c.getMappedIDsVirtualToReal().get(answer.get(i))
						+ " ");
			sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
