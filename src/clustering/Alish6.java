package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.DistanceType;

public class Alish6 {

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
	long baseStartTime;// = System.currentTimeMillis();

	public void findCluster(Centroid c, int k, int dataSet) throws Exception {
		long baseStartTime = System.currentTimeMillis();
		this.c = c;
		this.k = k;
		this.initialK = k;
		output = new ArrayList<Container>();
		answer = new ArrayList<Integer>();
		stack = new ArrayList<Integer>();
		keyset = new ArrayList<Integer>();

		FileReader FR = null;
		if (dataSet == 3 || dataSet == 5)
			FR = new FileReader(
					"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\Kaggle\\"
							+ c.getID() + ".txt");
		else if (dataSet == 1)
			FR = new FileReader(
					"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\Google+\\"
							+ c.getID() + ".txt");
		else if (dataSet == 2)
			FR = new FileReader(
					"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\Facebook\\"
							+ c.getID() + ".txt");
		else if (dataSet == 4)
			FR = new FileReader(
					"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\Twitter\\"
							+ c.getID() + ".txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text = BR.readLine();
		String Separator = "\\s+";
		String[] Ids = Text.split(Separator);
		BR.close();

		for (int i = 1; i < Ids.length; i++)
			keyset.add(c.getMappedIDsRealToVirtual().get(Ids[i]));

		ArrayList<Integer> people = new ArrayList<Integer>();
		for (int iterator = 1; iterator < Ids.length; iterator++) {
			if (System.currentTimeMillis() - baseStartTime > 10000)
				break;

			c.getCircles().clear();
			for (int i = 0; i < c.getFeat().size(); i++)
				c.getFeat().get(i).getCircles().clear();

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
			int counter = 0;
			Circle CIRCLE = new Circle();
			c.getCircles().put(0, CIRCLE);
			for (int u : answer) {
				c.getCircles().get(0).add(u);
				c.getFeat().get(u).assignCircle(0);
				container.arr.add(u);
				if (people.contains(u))
					counter++;
			}
			if (container.arr.size() <= 5)
				continue;

			if (container.arr.size() * 5 / 100 < counter)
				continue;
			//
			for (int u : c.getCircles().get(0).getCircleMembers()) {
				if (!people.contains(u))
					people.add(u);
			}
			output.add(container);
			if (output.size() == 10)
				break;
		}
		if (output.size() == 10)
			System.out.println("YES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// calling circlizer
		// LinkedHashSet<Integer> choose = new LinkedHashSet<Integer>();
		// Circlizer ccc = new Circlizer();
		// choose = (LinkedHashSet<Integer>) ccc.Circlize(output, c);
		// System.out.println(choose.size());
		// ArrayList<Container> output1 = new ArrayList<Container>();
		// for (Integer circle : choose)
		// output1.add(output.get(circle));
		// output = output1;

		// int MAX = output.size() / 2;
		// if (5 > MAX)
		// MAX = 4;
		// for (; output.size() > MAX;)
		// output.remove(output.size() - 1);

		printToFile();
		// makeCircleFiles();
	}

	private void findBest(Centroid c, int k, double sum) throws Exception {
		int TIME = 0;
		boolean flg = true;
		if (flg)
			TIME = 10;
		else
			TIME = 1000;
		if (System.currentTimeMillis() - startTime > TIME)
			return;
		if (initialK - k > biggestClique) {
			biggestClique = initialK - k;
			answer.clear();
			for (int u : stack)
				answer.add(u);
		}
		if (sum < minDistance[initialK - k]) {
			minDistance[initialK - k] = sum;
			/*
			 * if (initialK - k == biggestClique) { answer.clear(); for (int u :
			 * stack) answer.add(u); }
			 */
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
				if (output.get(i).arr.size() > 5) {
					sb.append("circle" + i + ": ");
					for (int j = 0; j < output.get(i).arr.size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								output.get(i).arr.get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									output.get(i).arr.get(j))
									+ " ");
				}
				sb.setLength(sb.length() - 1);
				sb.append("\n");
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

	private void makeCircleFiles() {
		String text = "";
		text += c.getID() + ",";
		for (int i = 0; i < output.size(); i++) {
			if (i != 0)
				text += ";";
			for (int j = 0; j < output.get(i).arr.size(); j++) {
				if (c.getMappedIDsVirtualToReal().get(output.get(i).arr.get(j))
						.equals(null))
					continue;
				text += c.getMappedIDsVirtualToReal().get(
						output.get(i).arr.get(j));
				if (j != output.get(i).arr.size() - 1)
					text += " ";
			}
		}
		text += "\n";
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + "submit"
					+ ".csv"), true);
			StringBuilder sb = new StringBuilder();
			sb.append(text);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
