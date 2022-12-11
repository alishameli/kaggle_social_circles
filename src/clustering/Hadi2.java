package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataStructures.Centroid;
import dataStructures.DistanceType;

public class Hadi2 {

	Centroid c;
	double averageDif[];
	boolean mark[];
	double differenceMatrix[][];
	ArrayList<Integer> people;
	int percentage;

	public void findEdges(Centroid c, int percentage) throws Exception {
		this.c = c;
		this.percentage = percentage;
		people = new ArrayList<Integer>();
		for (int i = 0; i < c.getCircles().get(0).getCircleMembers().size(); i++)
			people.add(c.getCircles().get(0).getCircleMembers().get(i));	
		differenceMatrix = new double[people.size()][people.size()];
		averageDif = new double[people.size()];
		mark = new boolean[people.size()];
		for(int i = 0;i < people.size();i++)
			mark[i] = false;
		
		for (int i = 0; i < people.size(); i++)
			for (int j = i + 1; j < people.size(); j++)
				findDifference(i, j);
		for (int i = 0; i < people.size(); i++) {
			for (int j = 0; j < people.size(); j++)
				averageDif[i] += differenceMatrix[i][j];
			averageDif[i] /= (double) people.size();
		}
		for(int i = 0;i < ((double) percentage / 100.0) * people.size();i++){
			double Max = 0;
			int idMax = -1;
			for(int j = 0;j < people.size();j++)
				if(!mark[j] && Max <= averageDif[j]){
					Max = averageDif[j];
					idMax = j;
				}
			mark[idMax] = true;
		}
//		for (int i = 0; i < ((double) percentage / 100.0)
//				* (double) people.size(); i++) {
//			int idMax = -1;
//			double Max = 0;
//			for (int j = 0; j < people.size(); j++){
//				if (!mark[j] && Max <= averageDif[j]) {
//					Max = averageDif[j];
//					idMax = j;
//				}
//			}
//			mark[idMax] = true;
//			System.out.println(idMax);
//			for (int j = 0; j < people.size(); j++)
//				if (!mark[j]){
//					if(c.getID().equals("24857"))
//						System.out.println(differenceMatrix[idMax][j] + " " + (people.size() -i) + " " + averageDif[j]);
//					averageDif[j] = (averageDif[j]
//							* (double) (people.size() - i) - differenceMatrix[idMax][j])
//							/ (double) (people.size() - i - 1);
//					if(averageDif[j] < 0)
//						System.out.println(j + " " + averageDif[j] + " " + (people.size() - i - 1));
//				}
//		}
		printToFile();
	}
	
	private void findDifference(int v, int u) throws Exception {
		differenceMatrix[u][v] = c
				.getFeat()
				.get(people.get(v))
				.getDistanceFrom(c.getFeat().get(people.get(u)),
						DistanceType.JACCARDIAN);
		differenceMatrix[v][u] = differenceMatrix[u][v];
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			sb.append("circle0: ");
			for(int i = 0;i < people.size();i++)
				if(!mark[i])
					sb.append(c.getMappedIDsVirtualToReal().get(people.get(i)) + " ");
			sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
