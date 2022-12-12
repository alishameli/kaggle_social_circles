package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Centroid;

public class BIC {

	HashMap<Integer, Double> ll;
	HashMap<Integer, Double> zeroOne;
	HashMap<Integer, Double> sym;
	HashMap<Integer, Double> fscore;
	HashMap<Integer, String> clusters;
	HashMap<Integer, String> theta;
	HashMap<Integer, String> alpha;
	HashMap<Integer, Double> runtime;
	HashMap<Integer, Double> bic;
	ArrayList<String> files;
	String path = "BIC\\";
	Centroid c;
	double e = 0;

	public BIC(Centroid c) throws IOException {
		this.c = c;
		ll = new HashMap<Integer, Double>();
		zeroOne = new HashMap<Integer, Double>();
		sym = new HashMap<Integer, Double>();
		fscore = new HashMap<Integer, Double>();
		clusters = new HashMap<Integer, String>();
		theta = new HashMap<Integer, String>();
		alpha = new HashMap<Integer, String>();
		runtime = new HashMap<Integer, Double>();
		files = new ArrayList<String>();
		bic = new HashMap<Integer, Double>();
	}

	private void printToFile(int numberOfCircles) {
		ArrayList<Container> output = new ArrayList<Container>();
		String circles = clusters.get(numberOfCircles);
		int circleCounter = -1;
		int num = 0;
		Container cont = new Container();
		for (int i = 1; i < circles.length() - 1; i++) {
			if (circles.charAt(i) == '[') {
				circleCounter++;
				cont = new Container();
			} else if (circles.charAt(i) == ',' && circles.charAt(i + 1) != '[') {
				cont.arr.add(num);
				num = 0;
			} else if (circles.charAt(i) >= '0' && circles.charAt(i) <= '9') {
				num *= 10;
				num += circles.charAt(i) - '0';
			} else if (circles.charAt(i) == ']')
				output.add(cont);
		}
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < output.size(); i++) {
				sb.append("circle" + i + ": ");
				for (int j = 0; j < output.get(i).arr.size(); j++)
					sb.append(output.get(i).arr.get(j) + " ");
				sb.setLength(sb.length() - 1);
				sb.append("\n");
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double find() throws IOException {
		findFiles();
		readFiles();
		findE();
		return findBIC();
	}

	private void findFiles() {
		File folder = new File(path);
		for (final File fileEntry : folder.listFiles()) {
			String nodeString = fileEntry.getName();
			if (nodeString.toLowerCase().contains("out")
					&& nodeString.toLowerCase().startsWith(c.getID() + "-"))
				files.add(nodeString.substring(0, nodeString.indexOf(".")));
		}
	}

	private void readFiles() throws IOException {
		for (int i = 0; i < files.size(); i++) {
			FileReader FR = new FileReader(path + files.get(i) + ".out");
			Integer numberOfCircles = Integer.parseInt(files.get(i).substring(
					files.get(i).indexOf("-") + 1));
			BufferedReader BR = new BufferedReader(FR);
			String Text;
			while ((Text = BR.readLine()) != null) {
				String Separator = "\\s+";
				String[] Ids = Text.split(Separator);
				if (Ids[0].equals("ll"))
					ll.put(numberOfCircles, Double.parseDouble(Ids[2]));
				else if (Ids[0].equals("loss_zeroone"))
					zeroOne.put(numberOfCircles, Double.parseDouble(Ids[2]));
				else if (Ids[0].equals("loss_symmetric"))
					sym.put(numberOfCircles, Double.parseDouble(Ids[2]));
				else if (Ids[0].equals("fscore"))
					fscore.put(numberOfCircles, Double.parseDouble(Ids[2]));
				else if (Ids[0].equals("clusters"))
					clusters.put(numberOfCircles, Ids[2]);
				else if (Ids[0].equals("theta"))
					theta.put(numberOfCircles, Ids[2]);
				else if (Ids[0].equals("alpha"))
					alpha.put(numberOfCircles, Ids[2]);
				else if (Ids[0].equals("runtime"))
					runtime.put(numberOfCircles, Double.parseDouble(Ids[2]));
			}
			BR.close();
		}
		if (c.getID().equals("239")) {
			System.out.println(clusters.get(5));
		}
	}

	private double findBIC() {
		for (Integer numberOfCircles : ll.keySet()) {
			double res = -(2.0) * ll.get(numberOfCircles);
			String salpha = alpha.get(numberOfCircles);
			String stheta = theta.get(numberOfCircles);
			double tmp = findNumberOfCommas(stheta) + 1.0
					+ findNumberOfCommas(salpha) + 1.0;
			tmp *= Math.log(e);
			res += tmp;
			bic.put(numberOfCircles, res);
		}
		int idMin = -1;
		double Min = 1000.0 * 1000.0 * 1000.0;
		for (Integer numberOfCircles : bic.keySet()) {
			if (bic.get(numberOfCircles) < Min) {
				Min = bic.get(numberOfCircles);
				idMin = numberOfCircles;
			}
		}
		System.out.println("Circles: " + idMin);
		System.out.println("F1: " + fscore.get(idMin));
		printToFile(idMin);
		return fscore.get(idMin);
	}

	private double findNumberOfCommas(String s) {
		int res = 0;
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == ',')
				res++;
		return res;

	}

	private void findE() {
		for (Integer v : c.getAdj().keySet())
			e += c.getAdj().size();
		e /= 2;
	}
}