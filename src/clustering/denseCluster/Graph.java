package clustering.denseCluster;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

	private ArrayList<AdjList> g;
	public int V, E;
	private HashMap<Integer, Integer> ExIn;
	int[] InEx;

	public Graph(int size) {
		V = E = 0;
		g = new ArrayList<AdjList>(size);
		for (int i = 0; i < size; i++) {
			AdjList adj = new AdjList();
			g.add(adj);
		}
		InEx = new int[size];
		ExIn = new HashMap<Integer, Integer>();
	}

	double Density() {
		return (double) E / V;
	}

	void AddDirEdge(int a, int b) {
		a = InternalID(a);
		b = InternalID(b);
		g.get(a).add(b);
	}

	void AddEdge(int a, int b) {
		a = InternalID(a);
		b = InternalID(b);
		g.get(a).add(b);
		if (a != b)
			g.get(b).add(a);
		E++;
	}

	int InternalID(Integer v) {
		if (!ExIn.containsKey(v)) {
			ExIn.put(v, V);
			InEx[V] = v;
			return V++;
		} else
			return (ExIn.get(v));
	}

	public int ExternalID(int v) {
		return InEx[v];
	}

	public AdjList getAdj(int i) {
		return g.get(i);
	}
}

class AdjList {
	ArrayList<Integer> adjList;

	public AdjList() {
		this.adjList = new ArrayList<Integer>();
	}

	public void add(Integer b) {
		this.adjList.add(b);
	}

	public int size() {
		return this.adjList.size();
	}

	public Integer get(int index) {
		return this.adjList.get(index);
	}
}
