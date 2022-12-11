package classifying;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main2 {
	public static void main(String[] args) throws IOException {
		ArrayList<String> nodeIds = new ArrayList<String>();
		String rPath = "D:\\Research_KDD2015\\WEKA\\";
		String wPath = "D:\\Research_KDD2015\\WEKA\\";

		File folder = new File(rPath);
		for (final File fileEntry : folder.listFiles()) {
			String nodeString = fileEntry.getName();
			if (nodeString.toLowerCase().contains("out")) {
				nodeIds.add(nodeString.substring(0, nodeString.indexOf(".")));
			}
		}

		for (String id : nodeIds) {
			StringBuilder sb = new StringBuilder();
			FileWriter fw = new FileWriter(new File(wPath + id + ".circles"));
			FileReader FR = new FileReader(rPath + id + ".out");
			BufferedReader BR = new BufferedReader(FR);
			String Text;
			while ((Text = BR.readLine()) != null) {
				String Separator = ",";
				String[] Ids = Text.split(Separator);
				sb.append("Circle0 ");
				String[] circles = Ids[1].split(";");
				sb.append(circles[1]);
				sb.append(System.getProperty("line.separator"));
				for (int i = 1; i < circles.length; i++) {
					sb.append("Circle" + i + " ");
					sb.append(circles[i]);
					if (i != circles.length - 1)
						sb.append(System.getProperty("line.separator"));
				}
			}
			fw.write(sb.toString().trim());
			BR.close();
			fw.close();
		}
	}
}