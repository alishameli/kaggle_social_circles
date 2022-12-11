package clustering;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataStructures.Centroid;
import dataStructures.DistanceType;

public class Alish2 {

	Centroid c;
	ArrayList<Integer> answer;
	ArrayList<Integer> stack;
	double minDistance = 1000 * 1000 * 1000;
	int k;

	public void findCluster(Centroid c, int k) throws Exception {
		this.c = c;
		this.k = k;
		FileReader FR = new FileReader(
				"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\testPageRank\\test_pagerank\\"
						+ c.getID() + ".txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text = BR.readLine();
		String Separator = "\\s+";
		String[] Ids = Text.split(Separator);
		BR.close();
		answer = new ArrayList<Integer>();
		stack = new ArrayList<Integer>();

		System.out.println(Integer.parseInt(Ids[1]));
		answer.add(c.getMappedIDsRealToVirtual().get(Ids[1]));
		stack.add(answer.get(0));

		findBest(c, k, 0);
		printToFile();
	}

	private void findBest(Centroid c, int k, double sum) throws Exception {
		if(k==0){
			if(sum<minDistance){
				minDistance=sum;
				answer.clear();
				for(int u:stack)
					answer.add(u);
			}
			return;
		}
		for (Integer v : c.getFeat().keySet()) {
			boolean isFriend=true, isDifferent=true;
			for(int i=0; i<stack.size(); i++){
				if(!c.getAdj().get(v).contains(stack.get(i)))
					isFriend=false;
				if(v==stack.get(i))
					isDifferent=false;
			}
			if (isDifferent==true && isFriend==true) {
				double newSum=sum;
				for(int i=0; i<stack.size(); i++){
					newSum+=c.getFeat().get(v).getDistanceFrom(c.getFeat().get(stack.get(i)), DistanceType.JACCARDIAN);
				}
				
				stack.add(v);
				findBest(c, k-1, newSum);
				stack.remove(stack.size()-1);
			}
		}
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
