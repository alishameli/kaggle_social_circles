package clustering.denseCluster;

public class Subgraph {
	Graph G;

	public boolean[] S;
	int V, E;

	public Subgraph(Graph G) {
		this.G = G;
		S = new boolean[G.V];
		for (int i = 0; i < G.V; i++) {
			S[i] = true;
		}
		V = G.V;
		E = G.E;
	}

	public boolean getState(int i) {
		return S[i];
	}

	// TODO
	// void operator= (Subgraph sg) {
	// S = sg.S;
	// V = sg.V;
	// E = sg.E;
	// }

	void set(int i, boolean value) {
		S[i] = value;
	}

	void Calculate() {
		V = E = 0;
		for (int i = 0; i < G.V; i++) {
			if (!S[i])
				continue;
			V++;
			for (int j = G.getAdj(i).size() - 1; j >= 0; j--) {
				if (!S[G.getAdj(i).get(j)])
					continue;
				if (i == G.getAdj(i).get(j))
					E++;
				E++;
			}
		}
		E /= 2;
	}

	double Density() {
		return (double) E / V;
	}

	void Print() {
		System.out.print("1\n" + Density() + "\t{");
		boolean comma = false;
		for (int i = 0; i < G.V; i++)
			if (S[i]) {
				System.out.printf((comma ? ",%d" : "%d"), G.ExternalID(i));
				comma = true;
			}
		System.out.print("}\t{");

		comma = false;
		for (int i = 0; i < G.V; i++) {
			if (!S[i])
				continue;
			for (int j = G.getAdj(i).size() - 1; j >= 0; j--) {
				if (!S[G.getAdj(i).get(j)])
					continue;
				System.out.printf((comma ? ",(%d,%d)" : "(%d,%d)"),
						G.ExternalID(i), G.ExternalID(G.getAdj(i).get(j)));
				comma = true;
			}
			S[i] = false;
		}

		System.out.printf("}\n");
	}

	Graph Shrink() {
		Graph _G = new Graph(V);
		_G.E = E;
		for (int i = 0; i < G.V; i++) {
			if (!S[i])
				continue;
			for (int j = G.getAdj(i).size() - 1; j >= 0; j--) {
				if (!S[G.getAdj(i).get(j)])
					continue;
				_G.AddDirEdge(G.ExternalID(i), G.ExternalID(G.getAdj(i).get(j)));
			}
		}
		return _G;
	}
}
