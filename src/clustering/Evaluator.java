package clustering;

import java.util.LinkedHashSet;

public class Evaluator {

	public enum EVALUATION_TYPE {
		ZEROONE, SYMMETRICDIFF, FSCORE;
	}

	// Compute the loss between a groundtruth cluster l and a predicted cluster
	// lhat
	private double loss(Container l, Container lhat, int N, EVALUATION_TYPE type) {
		if (l.size() == 0) {
			if (lhat.size() == 0)
				return 0;
			return 1.0;
		}
		if (lhat.size() == 0) {
			if (l.size() == 0)
				return 0;
			return 1.0;
		}
		int tp = 0;
		int fp = 0;
		int fn = 0;

		double ll = 0;
		for (Integer i : l.arr)
			if (!lhat.contains(i)) {
				// false negative
				fn++;
				if (type == EVALUATION_TYPE.ZEROONE)
					ll += 1.0 / N;
				else if (type == EVALUATION_TYPE.SYMMETRICDIFF)
					ll += 0.5 / l.size();
			}
		for (Integer i : lhat.arr)
			if (!l.contains(i)) {
				// false positive
				fp++;
				if (type == EVALUATION_TYPE.ZEROONE)
					ll += 1.0 / N;
				else if (type == EVALUATION_TYPE.SYMMETRICDIFF)
					ll += 0.5 / (N - l.size());
			} else
				tp++;

		if ((lhat.size() == 0 || tp == 0) && type == EVALUATION_TYPE.FSCORE)
			return 1.0;
		double precision = (1.0 * tp) / (double) lhat.size();
		double recall = (1.0 * tp) / (double) l.size();
		if (type == EVALUATION_TYPE.FSCORE)
			return 1 - 2 * (precision * recall) / (precision + recall);

		return ll;
	}

	// Compute the optimal loss via linear assignment
	public double totalLoss(LinkedHashSet<Container> clusters,
			LinkedHashSet<Container> chat, int N, EVALUATION_TYPE type) {
		if(clusters.size() == 0)
			return 0;
		double[][] matrix = new double[clusters.size()][chat.size()];

		for (int i = 0; i < (int) clusters.size(); i++)
			for (int j = 0; j < (int) chat.size(); j++)
				matrix[i][j] = loss((Container) clusters.toArray()[i],
						(Container) chat.toArray()[j], N, type);

		Munkres m = new Munkres(matrix);
		int[] res = m.execute();

		double l = 0;
		for (int i = 0; i < res.length; i++) {
			if (res[i] >= 0)
				l += loss((Container) clusters.toArray()[i],
						(Container) chat.toArray()[res[i]], N, type);
		}
		if(chat.size() == 0)
			return 1.0;
		return l
				/ (clusters.size() < chat.size() ? clusters.size() : chat
						.size());
	}
}
