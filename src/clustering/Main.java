package clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import dataStructures.Centroid;
import dataStructures.FCentroid;

public class Main {
	// General setup
	static String path = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\fb_dataset\\facebook\\";
	static boolean virtualCircle = false;
	static boolean addFriendshipsToVectors = true;
	static double friendshipFactor = 1;
	static double featureFactor = 1;
	static int runningTimes = 1;
	static long rootSeed = 475L;
	
	
	static Centroid centroid;
	static ArrayList<String> nodeIds;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		findNodeIds();
		for (Iterator<String> iterator = nodeIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			centroid = new FCentroid(id, friendshipFactor, featureFactor, path,
					virtualCircle, addFriendshipsToVectors).getCentroid();
			Clope clope = new Clope();
			clope.cluster(centroid, 10, 2);
		}
	}
	
	public static void findNodeIds() {
		nodeIds = new ArrayList<String>();
		File folder = new File(path);
		for (final File fileEntry : folder.listFiles()) {
			String nodeString = fileEntry.getName();
			if (nodeString.toLowerCase().contains("edges"))
				nodeIds.add(nodeString.substring(0, nodeString.indexOf(".")));
		}
	}
}
