package clustering.denseCluster;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import clustering.Container;
import dataStructures.Centroid;

public class DenseClusterFinder {
	Centroid c;
	Graph G;
	ArrayList<Container> output;
	boolean[] marked;
	int remainedNodes;

	public DenseClusterFinder(Centroid c) {
		this.c = c;
		output = new ArrayList<Container>();
		marked = new boolean[c.getFriendsSize()];
		remainedNodes = c.getFriendsSize();
		for (int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
	}

	// private void rebuildGraph() {
	// G = new Graph(remainedNodes);
	// for (int i = 0; i < marked.length; i++) {
	// if (!marked[i]) {
	// for (Integer friend : c.getAdj().get(i + 1).getFriends()) {
	// if (friend != 0 && !marked[friend - 1]) {
	// G.AddEdge(i, friend - 1);
	// }
	// }
	// }
	// }
	// }

	private void rebuildGraph() {
		G = new Graph(remainedNodes);
		for (int i = 0; i < marked.length; i++) {
			for (Integer friend : c.getAdj().get(i + 1).getFriends()) {
				if (friend == 0)
					continue;
				if (marked[i] && marked[friend - 1])
					continue;
				G.AddEdge(i, friend - 1);
			}
		}
	}

	public void find() {
		rebuildGraph();
		while (remainedNodes > 0) {
			Goldberg gold = new Goldberg(G);
			Subgraph sub = gold.findDensest();
			Container cont = new Container();
			for (int i = 0; i < sub.V; i++) {
				cont.arr.add(sub.G.ExternalID(i) + 1);
				marked[sub.G.ExternalID(i)] = true;
				//remainedNodes--;
			}
			if (sub.V == 0)
				break;
			output.add(cont);
			 if (output.size() == 10)
			 break;
			rebuildGraph();
		}
		printToFile();
	}

	private void printToFile() {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			boolean flg = false;
			for (int i = 0; i < output.size(); i++) {
				if (output.get(i).arr.size() > 5) {
					flg = true;
					sb.append("circle" + i + ": ");
					for (int j = 0; j < output.get(i).arr.size(); j++)
						if (c.getMappedIDsVirtualToReal().get(
								output.get(i).arr.get(j)) != null)
							sb.append(c.getMappedIDsVirtualToReal().get(
									output.get(i).arr.get(j))
									+ " ");
				}
				if (flg)
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

}
