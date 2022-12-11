package clustering;

import java.util.ArrayList;

import dataStructures.Centroid;
import dataStructures.FeatureVector;

public class Prune {

	double[] scoreOfEachPerson;
	double[] averageOfAllCircles;
	double[][] averageOfEachCircle;
	int numberOfFeatures; // Just Features! It does not consider any friendship
							// as a feature.
	int pruningPercentage;
	Centroid centroid;

	public void pruner(Centroid centroid, int pruningPercentage) {
		this.centroid = centroid;
		this.pruningPercentage = pruningPercentage;
		numberOfFeatures = centroid.getFeat().get(0).getFeatures().length
				- centroid.getFeat().size();
		averageOfAllCircles = new double[numberOfFeatures];
		averageOfEachCircle = new double[centroid.getCircles().size()][numberOfFeatures];
		scoreOfEachPerson = new double[centroid.getFeat().size()];
		fillAverageMatrices();
		for (int i = 0; i < centroid.getCircles().size(); i++)
			prune(i);
	}

	private void prune(int circle) {
		System.out.println("Circle " + circle);
		for (int i = 0; i < centroid.getFeat().size(); i++)
			scoreOfEachPerson[i] = 0.0;
		for (int i = 0; i < centroid.getCircles().get(circle)
				.getCircleMembers().size(); i++) {
			int id = centroid.getCircles().get(circle).getCircleMembers()
					.get(i);
			for (int l = 0; l < numberOfFeatures; l++)
				if (centroid.getFeat().get(id).getFeatures()[l] == centroid
						.getFeatureFactor()
						&& averageOfAllCircles[l] < averageOfEachCircle[circle][l])
					scoreOfEachPerson[id] += averageOfEachCircle[circle][l]
							- averageOfAllCircles[l];
			System.out.println(scoreOfEachPerson[id]);
		}
		// sort
		int[] sortedByScores = new int[centroid.getFeat().size()];
		boolean[] mark = new boolean[centroid.getFeat().size()];
		for (int i = 0; i < centroid.getFeat().size(); i++) {
			double Max = -1;
			int idMax = 0;
			for (int j = 0; j < centroid.getFeat().size(); j++)
				if (!mark[j] && Max < scoreOfEachPerson[j]) {
					Max = scoreOfEachPerson[j];
					idMax = j;
				}
			mark[idMax] = true;
			sortedByScores[i] = idMax;
		}
		// end of sort
		// finding people who will be pruned.
		ArrayList<Integer> kickout = new ArrayList<Integer>();
		double remainPeople = ((100.0 - (double) pruningPercentage) / 100.0)
				* (double) centroid.getCircles().get(circle).getCircleMembers()
						.size();
		if(scoreOfEachPerson[sortedByScores[(int)remainPeople]] == 0)
			return;
		for (int i = (int) remainPeople; i < centroid.getFeat().size(); i++) {
			if(scoreOfEachPerson[sortedByScores[i]] > 0)
				continue;
			int id = sortedByScores[i];
			if(centroid.getCircles().get(circle).getCircleMembers().contains(id))
				kickout.add(id);
		}
		// end
		// start pruning
		for(int i = 0;i < kickout.size();i++){
			int id = kickout.get(i);
			for(int j = 0;j < centroid.getCircles().get(circle).getCircleMembers().size();j++)
				if(centroid.getCircles().get(circle).getCircleMembers().get(j) == id){
					centroid.getCircles().get(circle).getCircleMembers().remove(j);
					break;
				}
			FeatureVector fv = centroid.getFeat().get(id);
			ArrayList<Integer> circles1 = fv.getCircles();
			int t = circles1.size();
			for(int j = 0;j < t;j++)
				if(centroid.getFeat().get(id).getCircles().get(j) == circle){
					centroid.getFeat().get(id).getCircles().remove(j);
					break;
				}
		}
		// end of pruning
	}

	private void fillAverageMatrices() {
		for (int i = 0; i < centroid.getCircles().size(); i++)
			for (int j = 0; j < centroid.getCircles().get(i).getCircleMembers()
					.size(); j++)
				for (int l = 0; l < numberOfFeatures; l++)
					averageOfEachCircle[i][l] += centroid
							.getFeat()
							.get(centroid.getCircles().get(i)
									.getCircleMembers().get(j)).getFeatures()[l];
		for (int i = 0; i < centroid.getCircles().size(); i++)
			for (int l = 0; l < numberOfFeatures; l++)
				averageOfEachCircle[i][l] /= (double) centroid.getCircles()
						.get(i).getCircleMembers().size();
		for (int i = 0; i < centroid.getCircles().size(); i++)
			for (int l = 0; l < numberOfFeatures; l++)
				averageOfAllCircles[l] += averageOfEachCircle[i][l];
		for (int l = 0; l < numberOfFeatures; l++)
			averageOfAllCircles[l] /= (double) centroid.getCircles().size();
	}
}
