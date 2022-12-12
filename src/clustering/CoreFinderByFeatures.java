package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataStructures.Centroid;

public class CoreFinderByFeatures {

	Centroid c;
	ArrayList<Container> output;

	public void find(Centroid c) {
		this.c = c;
		output = new ArrayList<Container>();
		int numberOfFeatures = c.getFeat().get(0).getFeatures().length
				- c.getFeat().size();
		for (int i = 0;i < numberOfFeatures;i++){
			Container container = new Container();
			for (Integer v: c.getFeat().keySet())
				if(c.getFeat().get(v).getFeatures()[i] > 0)
					container.arr.add(v);
			output.add(container);
		}
		printToFile();
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < output.size(); i++) {
				boolean flg = false;
				if (output.get(i).arr.size() > 2) {
					flg = true;
					sb.append("circle" + i + ": ");
					for (int j = 0; j < output.get(i).arr.size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								output.get(i).arr.get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									output.get(i).arr.get(j))
									+ " ");
				}
				if(flg){
					sb.setLength(sb.length() - 1);
					sb.append("\n");
				}
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
