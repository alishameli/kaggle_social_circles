package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import dataStructures.Centroid;
import dataStructures.Friends;

public class CoreFinder_KCore {
	boolean[] dfsMark;
	Centroid c;
	private HashMap<Integer, Friends> adj;
	ArrayList<PriorityQueue<Integer>> cores = new ArrayList<PriorityQueue<Integer>>();
	HashMap<Integer, Integer> deg = new HashMap<Integer, Integer>();

	Comparator<Integer> comparator = new Comparator<Integer>() {
		public int compare(Integer o1, Integer o2) {
			return deg.get(o1).compareTo(deg.get(o2));
		}
	};
	
	// k is the minimum degree of the vertices remaining in the graph.
	public ArrayList<PriorityQueue<Integer>> findCore(Centroid c, int k) { 
		this.c = c;
		PriorityQueue<Integer> nodes = new PriorityQueue<Integer>(1, comparator);
		adj = c.getAdj();

		for (Integer node : adj.keySet())
			deg.put(node, adj.get(node).getSize());

		for (Integer node : adj.keySet())
			if (node != 0)
				nodes.add(node);
		while (nodes.size() > 0 && deg.get(nodes.peek()) < k) {

			int u = nodes.poll();
			for (int neighbor : adj.get(u).getFriends()) {
				if (!nodes.contains(neighbor))
					continue;
				nodes.remove(neighbor);
				int degree = deg.remove(neighbor);
				deg.put(neighbor, degree - 1);
				nodes.add(neighbor);
			}
		}
		return dfsMethod(nodes);
	}

	public ArrayList<PriorityQueue<Integer>> dfsMethod(
			PriorityQueue<Integer> nodes1) {
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for (Integer element : nodes1)
			nodes.add(element);
		dfsMark = new boolean[adj.size()];
		for (int i = 0; i < dfsMark.length; i++)
			dfsMark[i] = false;
		for (int i = 0; i < nodes.size(); i++)
			if (!dfsMark[nodes.get(i)]) {
				PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
				cores.add(dfs(nodes.get(i), nodes, pq));
			}
		writeCoresToFile(cores);
		return cores;
	}
	
	public PriorityQueue<Integer> dfs(int v, ArrayList<Integer> nodes,
			PriorityQueue<Integer> pq) {
		dfsMark[v] = true;
		pq.add(v);
		for (int neighbor : adj.get(v).getFriends())
			if (nodes.contains(neighbor) && !dfsMark[neighbor])
				dfs(neighbor, nodes, pq);
		return pq;
	}
	
	public void writeCoresToFile(ArrayList<PriorityQueue<Integer>> cores){	
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID() + ".circles"));
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i < cores.size();i++){
				sb.append("circle" + i + ": ");
				for(Integer node : cores.get(i))
					sb.append(c.getMappedIDsVirtualToReal().get(node) + " ");
				sb.append("\n");
			}
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
