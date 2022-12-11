package classifying;

import java.util.ArrayList;

import dataStructures.FeatureVector;
import dataStructures.Snapshot;

public class Weka {
	ArrayList<FeatureVector> a;

	public int test(Snapshot snap, final FeatureVector query) throws Exception {
		a= new ArrayList<FeatureVector>();
		for (FeatureVector value : snap.getFeat().values()) {
			a.add(value);

		}
		for(int i=0;i<a.size();i++)
		{
			for(int j=0;j<a.get(i).getSize();j++)	
				System.out.print(a.get(i).getFeatures()[j] + "    ");
			System.out.println();
			
		}
		return 0;
	}

}
