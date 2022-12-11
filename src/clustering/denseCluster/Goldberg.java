package clustering.denseCluster;

public class Goldberg {

	Graph G;

	public Goldberg(Graph G) {
		this.G = G;
	}

	public Subgraph findDensest() {
		Subgraph S = new Subgraph(G);
		Subgraph _S = new Subgraph(G);
		PushRelabel P = new PushRelabel(G, _S);

		int l = 0, u = G.E, g = -1;
		while (true) {
			int _g = g;
			g = (u + l) / 2;
			if (_g == g)
				break;
			P.GetMinCut(g);
			if (_S.V == 0) {
				u = g;
			} else {
				l = g;
				S = _S;
			}
		}

		//S.Print();
		return S;
	}
}
