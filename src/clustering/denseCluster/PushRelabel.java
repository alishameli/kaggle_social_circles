// Adjacency list implementation of FIFO push relabel maximum flow
// with the gap relabeling heuristic.
// Downloaded from http://web.stanford.edu/~liszt90/acm/notebook.html#file3
// and modified by Alejandro Flores: reducing the memory requirements and
// adjusting it to implement the Densest Subgraph algorithm proposed by
// A. V. Goldberg at 'Finding a Maximum Density Subgraph'
// http://www.eecs.berkeley.edu/Pubs/TechRpts/1984/CSD-84-171.pdf

package clustering.denseCluster;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class PushRelabel {
	int N, _g;
	ArrayList<EdgeList> G;
	int[] excess, dist, count;
	boolean[] active;
	Queue<Integer> Q;
	Subgraph S;

	public PushRelabel(Graph _G, Subgraph _S) {
		this.S = _S;
		N = _G.V + 2;
		G = new ArrayList<EdgeList>(N);
		for (int i = 0; i < N; i++) {
			G.add(new EdgeList());
		}
		_g = 0;
		int s = _G.V, t = _G.V + 1;

		for (int i = 0; i < _G.V; i++) {
			for (int j = _G.getAdj(i).size() - 1; j >= 0; j--)
				AddEdge(i, _G.getAdj(i).get(j), 1);
			AddEdge(s, i, _G.E);
			AddEdge(i, t, _G.E - _G.getAdj(i).size());
		}
		Q = new LinkedList<Integer>();
	}

	void AddEdge(int from, int to, int cap) {
		G.get(from).add(new Edge(to, cap, 0, G.get(to).size()));
		if (from == to)
			G.get(from).back().index++;
		G.get(to).add(new Edge(from, 0, 0, G.get(from).size() - 1));
	}

	void Enqueue(int v) {
		if (!active[v] && excess[v] > 0) {
			active[v] = true;
			Q.add(v);
		}
	}

	void Push(int from, Edge e) {
		int amt = Math.min(excess[from], e.cap - e.flow);
		if (dist[from] <= dist[e.to] || amt == 0)
			return;
		e.flow += amt;
		G.get(e.to).get(e.index).flow -= amt;
		excess[e.to] += amt;
		excess[from] -= amt;
		Enqueue(e.to);
	}

	void Gap(int k) {
		for (int v = 0; v < N; v++) {
			if (dist[v] < k)
				continue;
			count[dist[v]]--;
			dist[v] = Math.max(dist[v], N + 1);
			count[dist[v]]++;
			Enqueue(v);
		}
	}

	void Relabel(int v) {
		count[dist[v]]--;
		dist[v] = 2 * N;
		for (int i = 0; i < G.get(v).size(); i++)
			if (G.get(v).get(i).cap - G.get(v).get(i).flow > 0)
				dist[v] = Math.min(dist[v], dist[G.get(v).get(i).to] + 1);
		count[dist[v]]++;
		Enqueue(v);
	}

	void Discharge(int v) {
		for (int i = 0; excess[v] > 0 && i < G.get(v).size(); i++)
			Push(v, G.get(v).get(i));
		if (excess[v] > 0) {
			if (count[dist[v]] == 1)
				Gap(dist[v]);
			else
				Relabel(v);
		}
	}

	void BFS(int v) {
		List<Integer> q = new LinkedList<Integer>();
		q.add(v);
		while(!q.isEmpty()){
			int u = q.get(0);
			for (int i = G.get(u).size() - 1; i >= 0; i--) {
				if (G.get(u).get(i).to != N - 2
						&& G.get(u).get(i).cap - G.get(u).get(i).flow > 0
						&& !S.getState(G.get(u).get(i).to)) {
					S.set(G.get(u).get(i).to, true);
					q.add(G.get(u).get(i).to);
				}
			}
			q.remove(0);
		}
	}
	
	void GetMinCut(int g) {

		int s = N - 2, t = N - 1;
		excess = new int[N];
		dist = new int[N];
		active = new boolean[N];
		count = new int[2 * N];

		int fix = 2 * g - 2 * _g;
		_g = g;
		for (int i = 0; i < G.size(); i++)
			for (int j = 0; j < G.get(i).size(); j++) {
				if (G.get(i).get(j).to == t)
					G.get(i).get(j).cap += fix;
				G.get(i).get(j).flow = 0;
			}

		count[0] = N - 1;
		count[N] = 1;
		dist[s] = N;
		active[s] = active[t] = true;
		for (int i = 0; i < G.get(s).size(); i++) {
			excess[s] += G.get(s).get(i).cap;
			Push(s, G.get(s).get(i));
		}

		while (!Q.isEmpty()) {
			int v = Q.poll();
			active[v] = false;
			Discharge(v);
		}

		excess = new int[N];
		dist = new int[N];
		active = new boolean[N];
		count = new int[2 * N];

		for (int i = 0; i < s; i++)
			S.set(i, false);
		BFS(s);
		S.Calculate();
	}
}

class Edge {
	int to, cap, flow, index;

	public Edge(int to, int cap, int flow, int index) {
		this.to = to;
		this.cap = cap;
		this.flow = flow;
		this.index = index;
	}
}

class EdgeList {
	ArrayList<Edge> edges;

	public EdgeList() {
		this.edges = new ArrayList<Edge>();
	}

	public Edge back() {
		return this.edges.get(edges.size() - 1);
	}

	public int size() {
		return edges.size();
	}

	public void add(Edge e) {
		this.edges.add(e);
	}

	public Edge get(int i) {
		return this.edges.get(i);
	}
}