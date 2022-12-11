package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import dataStructures.Centroid;
import dataStructures.DistanceType;

public class Alish5 {

	Centroid c;//
	ArrayList<Integer> answer;//
	ArrayList<Container> output;//
	ArrayList<Integer> stack;//
	ArrayList<Integer> keyset;//
	double[] minDistance = new double[1000];//
	int k;//
	long startTime;//
	int initialK;//
	int biggestClique;//

	public void findCluster(Centroid c, int k) throws Exception {
		this.c = c;
		this.k = k;
		this.initialK = k;
		output = new ArrayList<Container>();
		answer = new ArrayList<Integer>();
		stack = new ArrayList<Integer>();
		keyset=new ArrayList<Integer>();

		FileReader FR = new FileReader(
				"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\"
						+ c.getID() + ".txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text = BR.readLine();
		String Separator = "\\s+";
		String[] Ids = Text.split(Separator);
		BR.close();

		
		for(int i=1; i<Ids.length; i++)
			keyset.add(c.getMappedIDsRealToVirtual().get(Ids[i]));
		
		int people = 0;
		for (int iterator = 1; iterator < Ids.length; iterator++) {
			boolean isUsed = false;
			for (int i = 0; i < output.size() && !isUsed; i++)
				for (int j = 0; j < output.get(i).arr.size(); j++)
					if (output.get(i).arr.get(j) == c
							.getMappedIDsRealToVirtual().get(Ids[iterator])) {
						isUsed = true;
						break;
					}
			if (isUsed)
				continue;

			biggestClique = 0;
			for (int i = 0; i < minDistance.length; i++)
				minDistance[i] = 1000 * 1000 * 1000;
			answer.clear();
			stack.clear();

			int highestPR = c.getMappedIDsRealToVirtual().get(Ids[iterator]);
			answer.add(highestPR);
			stack.add(highestPR);
			startTime = System.currentTimeMillis();
			findBest(c, k, 0);

			Container container = new Container();
			container.arr = new ArrayList<Integer>();
			for (int u : answer)
				container.arr.add(u);
			output.add(container);
			for (int i = 0; i < container.arr.size(); i++)
				System.out.print(c.getMappedIDsVirtualToReal().get(
						container.arr.get(i))
						+ " ");
			System.out.println();
			people += container.arr.size();
			System.out.println(people + " " + c.getFeat().size());
			if (people > c.getFeat().size())
				break;
			if(container.arr.size() <= 3)
				output.remove(output.size() - 1);
			if (output.size() == 3)
				break;
		}

		printToFile();
	}

	private void findBest(Centroid c, int k, double sum) throws Exception {
		if (System.currentTimeMillis() - startTime > 1 * 1000)
			return;
		if (initialK - k > biggestClique)
			biggestClique = initialK - k;
		if (sum < minDistance[initialK - k]) {
			minDistance[initialK - k] = sum;
			if (initialK - k == biggestClique) {
				answer.clear();
				for (int u : stack)
					answer.add(u);
			}
		}
		if (k == 0)
			return;
		for (Integer v : keyset) {
			boolean isFriend = true, isDifferent = true;
			for (int i = 0; i < stack.size(); i++) {
				if (!c.getAdj().get(v).contains(stack.get(i)))
					isFriend = false;
				if (v == stack.get(i))
					isDifferent = false;
			}
			if (isDifferent == true && isFriend == true) {
				double newSum = sum;
				for (int i = 0; i < stack.size(); i++) {
					newSum += c
							.getFeat()
							.get(v)
							.getDistanceFrom(c.getFeat().get(stack.get(i)),
									DistanceType.JACCARDIAN);
				}

				stack.add(v);
				findBest(c, k - 1, newSum);
				stack.remove(stack.size() - 1);
			}
		}
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < output.size(); i++) {
				sb.append("circle" + i + ": ");
				if (output.get(i).arr.size() > 5)
					for (int j = 0; j < output.get(i).arr.size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								output.get(i).arr.get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									output.get(i).arr.get(j))
									+ " ");

				sb.setLength(sb.length() - 1);
				sb.append("\n");
			}
			sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
