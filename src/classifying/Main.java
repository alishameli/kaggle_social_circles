package classifying;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

import classifying.SVM.SVMCircleFinder;
import clustering.Alish1;
import clustering.Alish2;
import clustering.Alish3;
import clustering.Alish4;
import clustering.Alish5;
import clustering.Alish6;
import clustering.Alish7;
import clustering.BIC;
import clustering.Circlizer;
import clustering.Circlizer2;
import clustering.Circlizer3;
import clustering.Circlizer4;
import clustering.Clope;
import clustering.Container;
import clustering.CoreFinderByFeatures;
import clustering.CoreFinder_KCore;
import clustering.Evaluator;
import clustering.Evaluator.EVALUATION_TYPE;
import clustering.Hadi1;
import clustering.Hadi2;
import clustering.KMeans;
import clustering.NaiiveBayesClustering;
import clustering.Prune;
import clustering.denseCluster.DenseClusterFinder;
import dataStructures.Centroid;
import dataStructures.Circle;
import dataStructures.DistanceType;
import dataStructures.FCentroid;
import dataStructures.KCentroid;
import dataStructures.Snapshot;

public class Main {

	// General setup
	static String path1 = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\g+ dataset\\gplus.tar\\gplus\\";
	static String path2 = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\fb_dataset\\facebook\\";
	static String path3 = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\egonets\\";
	static String path4 = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\tw dataset\\twitter\\";
	static String path5 = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\KAGGLE_TEST\\egonets\\";
	static String path;

	static HashMap<String, ArrayList<String>> stringFeaturesForKaggle = new HashMap<String, ArrayList<String>>();
	static boolean virtualCircle = false;
	static boolean addFriendshipsToVectors = true;
	static int percentage = 20;
	static double friendshipFactor = 1;
	static double featureFactor = 1;
	static int runningTimes = 1;
	static long rootSeed = 475L;

	// KNN setup
	static int k = 7;
	static DistanceType KNNDistanceMethod = DistanceType.JACCARDIAN;
	static boolean testKNN = false;

	// Language Model setup
	static double landa = 0.999;
	static boolean testLM = false;

	// Rocchio setup
	static boolean testRocchio = false;
	static DistanceType rocchioDistanceMethod = DistanceType.EUCLIDEAN;

	// Naiive Bayes setup
	static boolean testNaiiveBayes = false;

	// LMKNN setup
	static boolean testLMKNN = false;
	static int kPrime = 7;
	static double landaPrime = 0.999;
	static DistanceType LMKNNDistanceMethod = DistanceType.JACCARDIAN;

	// TFIDF setup
	static boolean testTFIDF = false;

	// AYESAM setup
	static boolean testAYESAM = false;

	// RANDOMWALK setup
	static boolean testRANDOMWALK = false;
	static double alpha;

	// NaiiveBayes2 setup
	static boolean testNaiiveBayes2 = false;

	// NaiiveBayes3 setup
	static boolean testNaiiveBayes3 = true;

	// NaiiveBayes4 setup
	static boolean testNaiiveBayes4 = false;

	static boolean testNaiiveBayes7 = false;

	// CoreFinder_KCore setup
	static int k_CoreFinder = 7;
	static boolean testCoreFinderKCore = false;

	// Clustering setup
	static int k_NumberOfClusters = 18;

	// NaiiveBayesClustering setup
	static int numberOfIterationsForSeed = 100;
	static boolean testNaiiveBayesClustering = false;

	// Clope setup
	static int r_clope = 2;
	static boolean testClope = false;

	// Hadi Clustering setup
	static boolean testHadi1 = false;
	static int hadiPercentage = 2;

	// Hadi Clustering setup
	static boolean testHadi2 = false;
	static int hadiPercentage2 = 50;

	// Alish1 Clustering setup
	static boolean testAlish1 = false;
	static int Alish1K = 3;

	// Alish2 Clustering setup
	static boolean testAlish2 = false;
	static int Alish2K = 4;

	// Alish3 Clustering setup
	static boolean testAlish3 = false;
	static int Alish3K = 25;

	// Alish4 Clustering setup
	static boolean testAlish4 = false;
	static int Alish4K = 25;

	// Alish5 Clustering setup
	static boolean testAlish5 = false;
	static int Alish5K = 25;

	static boolean SVM = false;

	static boolean testBIC = false;

	static boolean testCoreFeature = false;

	// KMeans setup
	static boolean testKMeans = false;
	static int kmK = 25;
	static int kmIters = 7;

	static int dataSet = 2;

	static boolean dense = false;

	// Alish6 Clustering setup
	static boolean testAlish6 = false; // dar Alish6 clique ha peyda mishan ke
										// mizane hadaksar eshterak set
										// mishavad.
	static int Alish6K = 19;

	static boolean testAlish7 = false; // cliquehaye disjoint miyabad.

	// NaiiveBayes5 setup
	static boolean testNaiiveBayes5 = false;
	static boolean testNaiiveBayes5RemoveOption = false;
	static boolean testNaiiveBayes5AddOption = true;
	static boolean testNaiiveBayes5PruneOption = false;
	static int pruningPercentage = 10;
	static int addingTimesNaiive = 5;
	
	// Circlizer
	static boolean circlize1 = false; // algorithm tavani zarbi

	static boolean circlize2 = false; // 2-approx jami buggi

	static boolean circlize3 = false; // 2-approx jami ok!

	static boolean circlize4 = false; // algorithme tavani jami

	static boolean evaluator = false;

	static Centroid centroid;
	static Snapshot snap;
	static ArrayList<String> nodeIds;
	static long[] seeds;
	static double knnAns;
	static double lmAns;
	static double roAns;
	static double nbAns;
	static double lmknnAns;
	static double tfidfAns;
	static double ayesamAns;
	static double randomWalkAns;
	static double nb2Ans;
	static double nb3Ans;
	static double berAns;
	static double nb4Ans;
	static double svmAns;

	public static void main(String[] args) throws Exception {
		if (dataSet == 1)
			path = path1;
		else if (dataSet == 2)
			path = path2;
		else if (dataSet == 3)
			path = path3;
		else if (dataSet == 4)
			path = path4;
		else if (dataSet == 5)
			path = path5;
		setSeeds();
		findNodeIds();
		if (dataSet == 3 || dataSet == 5)
			computeStringFeaturesForKaggle();
		for (Iterator<String> iterator = nodeIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			if (dataSet == 1 || dataSet == 2 || dataSet == 4)
				centroid = new FCentroid(id, friendshipFactor, featureFactor,
						path, virtualCircle, addFriendshipsToVectors)
						.getCentroid();
			else {
				centroid = new KCentroid(id, friendshipFactor, featureFactor,
						path, virtualCircle, addFriendshipsToVectors,
						stringFeaturesForKaggle, dataSet).getCentroid();
			}
			runTests();
			if (dataSet == 5
					&& (testNaiiveBayes5 || circlize1 || testNaiiveBayes7))
				makeCircleFiles(false);
			if (dataSet == 3
					&& (testNaiiveBayes5 || circlize1 || testNaiiveBayes7))
				makeCircleFiles(false);
		}
		if (dataSet == 5 && (testNaiiveBayes5 || circlize1 || testNaiiveBayes7))
			makeCircleFiles(true);
		if (dataSet == 3 && (testNaiiveBayes5 || circlize1 || testNaiiveBayes7))
			makeCircleFiles(true);
		printResults();
	}

	private static void findFeatures() {
		for (Circle c : centroid.getCircles().values()) {
			int numberOfFeatures = centroid.getFeat().get(0).getFeatures().length
					- centroid.getFeat().size();
			double features[] = new double[numberOfFeatures];
			for (int i = 0; i < numberOfFeatures; i++)
				for (int j = 0; j < c.getCircleMembers().size(); j++)
					features[i] += centroid.getFeat()
							.get(c.getCircleMembers().get(j)).getFeatures()[i];
			System.out.print(c.getCircleMembers().size() + "\t");
			for (int i = 0; i < numberOfFeatures; i++)
				System.out.print(features[i] + "\t");
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}

	private static void computeStringFeaturesForKaggle() throws IOException {
		FileReader FR = new FileReader(path + "features.txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			String newNode = Ids[0];
			stringFeaturesForKaggle.put(newNode, new ArrayList<String>());
			for (int i = 1; i < Ids.length; i++)
				stringFeaturesForKaggle.get(newNode).add(Ids[i]);
		}
		BR.close();
	}

	private static void setSeeds() {
		seeds = new long[runningTimes];
		Random rnd = new Random(rootSeed);
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = rnd.nextLong();
		}
	}

	private static void findNodeIds() {
		nodeIds = new ArrayList<String>();
		File folder = new File(path);
		for (final File fileEntry : folder.listFiles()) {
			String nodeString = fileEntry.getName();
			if (nodeString.toLowerCase().contains("edges")
					|| (nodeString.toLowerCase().contains("circles") && (dataSet == 3 || dataSet == 5))) {
				if (!nodeString.substring(0, nodeString.indexOf(".")).equals(
						"101560853443212199687")
						&& !nodeString.substring(0, nodeString.indexOf("."))
								.equals("116899029375914044550")
						&& !nodeString.substring(0, nodeString.indexOf("."))
								.equals("111278293763545982455") )
					nodeIds.add(nodeString.substring(0, nodeString.indexOf(".")));
			}
		}
	}

	static int nodeFinderCounter = 0;

	public static void runTests() throws Exception {
		nodeFinderCounter++;
		for (long seed : seeds) {
			System.out.println(nodeFinderCounter + "\t" + "centroid: "
					+ centroid.getID());
			// weka(centroid.getID());
			// snap = centroid.getSnapshot(percentage, seed);
			if (testKNN)
				knnAns += test("knn");
			if (testLM)
				lmAns += test("lm");
			if (testRocchio)
				roAns += test("ro");
			if (testNaiiveBayes)
				nbAns += test("nb");
			if (testLMKNN)
				lmknnAns += test("lmknn");
			if (testTFIDF)
				tfidfAns += test("tfidf");
			if (testAYESAM)
				ayesamAns += test("AYESAM");
			if (testRANDOMWALK)
				randomWalkAns += test("rw");
			if (testNaiiveBayes2)
				nb2Ans += test("nb2");
			if (testNaiiveBayes3)
				nb3Ans += test("nb3");
			if (SVM)
				svmAns += test("svm");
			if (testNaiiveBayes4)
				nb4Ans += test("nb4");
			if (testNaiiveBayes5)
				test("nb5");
			if (testNaiiveBayes7)
				test("nb7");
			if (testCoreFinderKCore)
				test("CoreFinderKCore");
			if (testNaiiveBayesClustering)
				test("nbc");
			if (testClope)
				test("clope");
			if (testHadi1)
				test("hadi1");
			if (testHadi2)
				test("hadi2");
			if (testAlish1)
				test("alish1");
			if (testAlish2)
				test("alish2");
			if (testAlish3)
				test("alish3");
			if (testAlish4)
				test("alish4");
			if (testAlish5)
				test("alish5");
			if (testAlish6)
				test("alish6");
			if (testAlish7)
				test("alish7");
			if (circlize1)
				test("circlize");
			if (circlize2)
				test("circlize2");
			if (circlize3)
				test("circlize3");
			if (circlize4)
				test("circlize4");
			if (evaluator)
				test("evaluator");
			if (dense)
				test("dense");
			if (testKMeans)
				test("kmeans");
			if (testCoreFeature)
				test("coreFeature");
			if (testBIC)
				test("bic");

		}
	}

	public static double test(String methodName) throws Exception {
		int TP = 0;
		int FP = 0;
		int TN = 0;
		int FN = 0;
		int nullCircleID;
		if (virtualCircle)
			nullCircleID = snap.getCircles().size() - 1;
		else
			nullCircleID = -1;
		// KNN
		if (methodName.equalsIgnoreCase("knn")) {
			KNN knn = new KNN();
			for (Integer value : snap.getDeleted()) {
				int x = knn.test(snap, centroid.getFeat().get(value), k,
						KNNDistanceMethod);
				if (centroid.getFeat().get(value).getCircles().contains(x)) {
					if (x == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			System.out.println(ans);
			return (Double.isNaN(ans)) ? 0 : ans;
		}

		// Language Model
		else if (methodName.equalsIgnoreCase("lm")) {
			LanguageModel lm = new LanguageModel();
			for (Integer value : snap.getDeleted()) {
				Pair x = lm.test(snap, centroid.getFeat().get(value), landa);
				if (centroid.getFeat().get(value).getCircles()
						.contains(x.getFirstCircle())) {
					if (x.getFirstCircle() == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x.getFirstCircle() == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			return (Double.isNaN(ans)) ? 0 : ans;
		}

		// Rocchio
		else if (methodName.equalsIgnoreCase("ro")) {
			Rocchio ro = new Rocchio();
			for (Integer value : snap.getDeleted()) {
				int x = ro.test(snap, centroid.getFeat().get(value),
						rocchioDistanceMethod);
				if (centroid.getFeat().get(value).getCircles().contains(x)) {
					if (x == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			return (Double.isNaN(ans)) ? 0 : ans;
		}

		// Naiive Bayes
		else if (methodName.equalsIgnoreCase("nb")) {
			NaiiveBayes nb = new NaiiveBayes();
			for (Integer value : snap.getDeleted()) {
				Pair pair = nb.test(snap, centroid.getFeat().get(value));
				int x = pair.getFirstCircle();
				if (centroid.getFeat().get(value).getCircles().contains(x)) {
					if (x == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			return (Double.isNaN(ans)) ? 0 : ans;
		}

		// LMKNN
		else if (methodName.equalsIgnoreCase("lmknn")) {
			LMKNN lmk = new LMKNN();
			for (Integer value : snap.getDeleted()) {
				int x = lmk.test(snap, centroid.getFeat().get(value), kPrime,
						landaPrime, LMKNNDistanceMethod);
				if (centroid.getFeat().get(value).getCircles().contains(x)) {
					if (x == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			return (Double.isNaN(ans)) ? 0 : ans;
		}
		// TF-IDF
		else if (methodName.equalsIgnoreCase("tfidf")) {
			TFIDF tfidf = new TFIDF();
			for (Integer value : snap.getDeleted()) {
				int x = tfidf.test(snap, centroid.getFeat().get(value));
				if (centroid.getFeat().get(value).getCircles().contains(x)) {
					if (x == nullCircleID)
						TN++;
					else
						TP++;
				} else {
					if (x == nullCircleID)
						FN++;
					else
						FP++;
				}
			}
			double precision = (double) TP / (double) (TP + FP);
			double recall = (double) TP / (double) (TP + TN);
			double ans = F(precision, recall);
			return (Double.isNaN(ans)) ? 0 : ans;
		}
		// AYESAM
		else if (methodName.equalsIgnoreCase("ayesam")) {
			AYESAM ayesam = new AYESAM();
			ayesam.test(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("nb2")) {
			NaiiveBayes2 nb2 = new NaiiveBayes2();
			return nb2.test(centroid);
		} else if (methodName.equalsIgnoreCase("nb3")) {
			NaiiveBayes3 nb3 = new NaiiveBayes3();
			ResultPair res = nb3.test(centroid);
			berAns += res.getBER();
			return res.getF1();
		} else if (methodName.equalsIgnoreCase("svm")) {
			SVMCircleFinder svm = new SVMCircleFinder();
			ResultPair res = svm.test(centroid);
			berAns += res.getBER();
			return res.getF1();
		} else if (methodName.equalsIgnoreCase("nb4")) {
			NaiiveBayes4 nb4 = new NaiiveBayes4();
			return nb4.test(centroid);
		} else if (methodName.equalsIgnoreCase("nb5")) {
			if (testNaiiveBayes5RemoveOption) {
				NaiiveBayes5ForClusteringForRemove nb5 = new NaiiveBayes5ForClusteringForRemove();
				nb5.test(centroid);
			}
			if (testNaiiveBayes5AddOption) {
				for (int i = 1; i <= addingTimesNaiive; i++) {
					NaiiveBayes6ForClusteringForAdd nb6 = new NaiiveBayes6ForClusteringForAdd();
					nb6.test(centroid);
				}
			}
			if (testNaiiveBayes5PruneOption) {
				Prune prn = new Prune();
				prn.pruner(centroid, pruningPercentage);
			}
			return 0;
		} else if (methodName.equalsIgnoreCase("nb7")) {
			for (int i = 1; i <= 5; i++) {
				NaiiveBayes7ForClusteringForAddOnline nb7 = new NaiiveBayes7ForClusteringForAddOnline();
				nb7.test(centroid);
			}
			return 0;
		} else if (methodName.equalsIgnoreCase("CoreFinderKCore")) {
			CoreFinder_KCore CFKC = new CoreFinder_KCore();
			CFKC.findCore(centroid, k_CoreFinder);
			return 0;
		} else if (methodName.equalsIgnoreCase("nbc")) {
			NaiiveBayesClustering nbc = new NaiiveBayesClustering();
			nbc.test(centroid, k_NumberOfClusters, numberOfIterationsForSeed);
			return 0;
		} else if (methodName.equalsIgnoreCase("clope")) {
			Clope clp = new Clope();
			clp.cluster(centroid, k_NumberOfClusters, r_clope);
			return 0;
		} else if (methodName.equalsIgnoreCase("hadi1")) {
			Hadi1 hadi = new Hadi1();
			hadi.findEdges(centroid, hadiPercentage);
			return 0;
		} else if (methodName.equalsIgnoreCase("hadi2")) {
			Hadi2 hadi = new Hadi2();
			hadi.findEdges(centroid, hadiPercentage2);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish1")) {
			Alish1 alish = new Alish1();
			alish.findCluster(centroid, Alish1K);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish2")) {
			Alish2 alish = new Alish2();
			alish.findCluster(centroid, Alish2K);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish3")) {
			Alish3 alish = new Alish3();
			alish.findCluster(centroid, Alish3K);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish4")) {
			Alish4 alish = new Alish4();
			alish.findCluster(centroid, Alish4K);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish5")) {
			Alish5 alish = new Alish5();
			alish.findCluster(centroid, Alish5K);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish6")) {
			Alish6 alish = new Alish6();
			alish.findCluster(centroid, Alish6K, dataSet);
			return 0;
		} else if (methodName.equalsIgnoreCase("alish7")) {
			Alish7 alish = new Alish7();
			alish.findCluster(centroid, Alish6K, dataSet);
			return 0;
		} else if (methodName.equalsIgnoreCase("rw")) {
			RandomWalk randomWalk = new RandomWalk();
			randomWalk.test(centroid, alpha);
			return 0;
		} else if (methodName.equalsIgnoreCase("circlize")) {
			circlize(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("circlize2")) {
			circlize2(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("circlize3")) {
			circlize3(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("circlize4")) {
			circlize4(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("evaluator")) {
			evaluate(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("dense")) {
			DenseClusterFinder dcf = new DenseClusterFinder(centroid);
			dcf.find();
			return 0;
		} else if (methodName.equalsIgnoreCase("coreFeature")) {
			CoreFinderByFeatures coref = new CoreFinderByFeatures();
			coref.find(centroid);
			return 0;
		} else if (methodName.equalsIgnoreCase("bic")) {
			BIC bic = new BIC(centroid);
			bicRes += bic.find();
			System.out.println(bicRes / 60);
			return 0;
		} else if (methodName.equalsIgnoreCase("kmeans")) {
			ArrayList<Container> ans = new ArrayList<Container>();
			ArrayList<Container> ans1 = new ArrayList<Container>();
			KMeans km1 = new KMeans(centroid, 5, 7, seeds[0]);
			ans1 = km1.find();
			for (Container c : ans1)
				ans.add(c);
			KMeans km2 = new KMeans(centroid, 10, 7, seeds[0]);
			ans1 = km2.find();
			for (Container c : ans1)
				ans.add(c);
			KMeans km3 = new KMeans(centroid, 15, 7, seeds[0]);
			ans1 = km3.find();
			for (Container c : ans1)
				ans.add(c);
			KMeans km4 = new KMeans(centroid, 20, 7, seeds[0]);
			ans1 = km4.find();
			for (Container c : ans1)
				ans.add(c);
			KMeans km5 = new KMeans(centroid, 25, 7, seeds[0]);
			ans1 = km5.find();
			for (Container c : ans1)
				ans.add(c);
			printToFile(ans, centroid);
			return 0;
		} else
			throw new Exception("The method name is not correct!");
	}

	static double bicRes = 0.0;
	
	static double evaluateClusterPrecsion = 0.0;
	static double evaluateClusterRecall = 0.0;
	static double evaluateClusterF1 = 0.0;
	static double centroidCounter = 0.0;

	private static void evaluate(Centroid c) throws IOException {
		LinkedHashSet<Container> chat = findChat(c);
		LinkedHashSet<Container> clusters = findGroundTruthCircles(c);
		Evaluator evaluator = new Evaluator();
		double fScore = evaluator.totalLoss(clusters, chat, c.getFriendsSize(),
				EVALUATION_TYPE.FSCORE);
		evaluateClusterF1 += fScore;
		double precision = evaluator.totalLoss(clusters, chat, c.getFriendsSize(),
				EVALUATION_TYPE.PRECISION);
		evaluateClusterPrecsion += precision;
		double recall = evaluator.totalLoss(clusters, chat, c.getFriendsSize(),
				EVALUATION_TYPE.RECALL);
		evaluateClusterRecall += recall;
		centroidCounter += 1.0;
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F1: " + (1.0 - fScore));
		System.out.println(1.0 - evaluateClusterF1 / centroidCounter);
		System.out.println();
		System.out.println();
		// System.out.println(1.0 - BER);
		// System.out.println(1.0 - evaluateClusterBER / centroidCounter);
		// System.out.println(evaluator.totalLoss(clusters, chat,
		// c.getFriendsSize(), EVALUATION_TYPE.ZEROONE));
	}

	private static LinkedHashSet<Container> findChat(Centroid cent)
			throws IOException {
		String chatPath = "D:\\Research_KDD2015\\WEKA\\";
		LinkedHashSet<Container> res = new LinkedHashSet<Container>();
		FileReader FR = new FileReader(chatPath + cent.getID() + ".circles");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			Container c = new Container();
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int i = 1; i < Ids.length; i++) {
				c.arr.add(cent.getMappedIDsRealToVirtual().get(Ids[i]));
			}
			res.add(c);
		}
		BR.close();
		return res;
	}

	private static LinkedHashSet<Container> findGroundTruthCircles(Centroid cent)
			throws IOException {
		String groundTruthCirclePath = null;
		if (dataSet == 2)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\fb_dataset\\ground truth circles\\";
		else if (dataSet == 1)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\g+ dataset\\gplus.tar\\ground truth circles\\";
		else if (dataSet == 4)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\tw dataset\\ground truth circles\\";
		else if (dataSet == 3)
			groundTruthCirclePath = "C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\Training\\";
		LinkedHashSet<Container> res = new LinkedHashSet<Container>();
		FileReader FR = new FileReader(groundTruthCirclePath + cent.getID()
				+ ".circles");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			Container c = new Container();
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			for (int i = 1; i < Ids.length; i++) {
				c.arr.add(cent.getMappedIDsRealToVirtual().get(Ids[i]));
			}
			res.add(c);
		}
		BR.close();
		return res;
	}

	private static void circlize2(Centroid c) throws IOException {
		ArrayList<Container> output;
		output = new ArrayList<Container>();
		for (Circle circle : c.getCircles().values()) {
			Container container = new Container();
			container.arr = new ArrayList<Integer>();
			for (int v : circle.getCircleMembers())
				container.arr.add(v);
			output.add(container);
		}
		LinkedHashSet<Integer> choose = new LinkedHashSet<Integer>();
		Circlizer2 ccc = new Circlizer2(centroid, output);
		choose = (LinkedHashSet<Integer>) ccc.circlize();
		ArrayList<Container> output1 = new ArrayList<Container>();
		for (Integer circle : choose)
			output1.add(output.get(circle));
		output = output1;
		printToFile(output, c);
	}

	private static void circlize3(Centroid c) throws IOException {
		ArrayList<Container> output;
		output = new ArrayList<Container>();
		for (Circle circle : c.getCircles().values()) {
			Container container = new Container();
			container.arr = new ArrayList<Integer>();
			for (int v : circle.getCircleMembers())
				container.arr.add(v);
			output.add(container);
		}
		LinkedHashSet<Integer> choose = new LinkedHashSet<Integer>();
		Circlizer3 ccc = new Circlizer3(centroid, output);
		choose = (LinkedHashSet<Integer>) ccc.circlize();
		ArrayList<Container> output1 = new ArrayList<Container>();
		for (Integer circle : choose)
			output1.add(output.get(circle));
		output = output1;
		printToFile(output, c);
	}

	private static void circlize4(Centroid c) throws IOException {
		ArrayList<Container> output;
		output = new ArrayList<Container>();
		for (Circle circle : c.getCircles().values()) {
			Container container = new Container();
			container.arr = new ArrayList<Integer>();
			for (int v : circle.getCircleMembers())
				container.arr.add(v);
			output.add(container);
		}
		LinkedHashSet<Integer> choose = new LinkedHashSet<Integer>();
		Circlizer4 ccc = new Circlizer4(centroid, output);
		choose = (LinkedHashSet<Integer>) ccc.circlize();
		ArrayList<Container> output1 = new ArrayList<Container>();
		for (Integer circle : choose)
			output1.add(output.get(circle));
		output = output1;
		printToFile(output, c);
	}

	private static void circlize(Centroid c) throws IOException {
		ArrayList<Container> output;
		output = new ArrayList<Container>();
		for (Circle circle : c.getCircles().values()) {
			Container container = new Container();
			container.arr = new ArrayList<Integer>();
			for (int v : circle.getCircleMembers())
				container.arr.add(v);
			output.add(container);
		}
		LinkedHashSet<Integer> choose = new LinkedHashSet<Integer>();
		Circlizer ccc = new Circlizer();
		choose = (LinkedHashSet<Integer>) ccc.Circlize(output, c);
		ArrayList<Container> output1 = new ArrayList<Container>();
		for (Integer circle : choose)
			output1.add(output.get(circle));
		output = output1;
		printToFile(output, c);
	}

	private static void printToFile(ArrayList<Container> output, Centroid c) {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + c.getID()
					+ ".circles"));
			StringBuilder sb = new StringBuilder();
			//sb.append("\n");
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

	private static double F(double precision, double recall) {
		double answer = (2 * precision * recall) / (precision + recall);
		return answer;
	}

	public static void weka(String id) {
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + id + ".weka"));
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < centroid.getFeat().size(); i++) {
				for (int j = 0; j < centroid.getFeat().get(0).getFeatures().length; j++)
					sb.append((int) centroid.getFeat().get(i).getFeatures()[j]
							+ " ");
				sb.append(System.getProperty("line.separator"));
				for (int j = 0; j < centroid.getFeat().get(i).getCircles()
						.size(); j++)
					sb.append(centroid.getFeat().get(i).getCircles().get(j)
							+ " ");
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
			}
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printResults() {
		if (testKNN)
			System.out.println((knnAns / (runningTimes * nodeIds.size()))
					+ " % ~ " + k + "NN on " + runningTimes
					+ " times of runnig");
		if (testLM)
			System.out.println((lmAns / (runningTimes * nodeIds.size()))
					+ " % ~ Language Model on " + runningTimes
					+ " times of runnig");
		if (testRocchio)
			System.out.println((roAns / (runningTimes * nodeIds.size()))
					+ " % ~ Rocchio on " + runningTimes + " times of runnig");
		if (testNaiiveBayes)
			System.out.println((nbAns / (runningTimes * nodeIds.size()))
					+ " % ~ Naiive Bayes on " + runningTimes
					+ " times of runnig");
		if (testLMKNN)
			System.out.println((lmknnAns / (runningTimes * nodeIds.size()))
					+ " % ~ LM" + kPrime + "NN on " + runningTimes
					+ " times of runnig");
		if (testTFIDF)
			System.out.println((tfidfAns / (runningTimes * nodeIds.size()))
					+ " % ~ TFIDF on " + runningTimes + " times of runnig");
		if (testNaiiveBayes2)
			System.out.println((nb2Ans / (runningTimes * nodeIds.size()))
					+ " % ~ nb2 on " + runningTimes + " times of runnig");
		if (testNaiiveBayes3) {
			System.out.println(nb3Ans);
			System.out.println(runningTimes * nodeIds.size());
			System.out.println((nb3Ans / (runningTimes * nodeIds.size()))
					+ " % ~ nb3 on " + runningTimes + " times of runnig");
			System.out.println("Final BER: " + berAns
					/ (runningTimes * nodeIds.size()));
		}
		if (SVM) {
			System.out.println((svmAns / (runningTimes * nodeIds.size()))
					+ " % ~ svm on " + runningTimes + " times of runnig");
			System.out.println("Final BER: " + berAns
					/ (runningTimes * nodeIds.size()));
		}
		if (testNaiiveBayes4)
			System.out.println((nb4Ans / (runningTimes * nodeIds.size()))
					+ " % ~ nb4 on " + runningTimes + " times of runnig");

	}

	static String output = "";

	public static void makeCircleFiles(boolean last) {
		if (!last) {
			output += centroid.getID() + ",";
			for (int i = 0; i < centroid.getCircles().size(); i++) {
				if (i != 0)
					output += ";";
				for (int j = 0; j < centroid.getCircles().get(i)
						.getCircleMembers().size(); j++) {
					if (centroid
							.getMappedIDsVirtualToReal()
							.get(centroid.getCircles().get(i)
									.getCircleMembers().get(j)).equals(null))
						continue;
					output += centroid.getMappedIDsVirtualToReal().get(
							centroid.getCircles().get(i).getCircleMembers()
									.get(j));
					if (j != centroid.getCircles().get(i).getCircleMembers()
							.size() - 1)
						output += " ";
				}
			}
			output += "\n";
			return;
		}
		try {
			FileWriter fw = new FileWriter(new File("./WEKA/" + "submit"
					+ ".csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("UserId,Predicted\n");
			sb.append(output);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void followers() throws IOException {
		double following = 0;
		FileReader FR = new FileReader(path + centroid.getID() + ".followers");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			String follower = Ids[0];
			// System.out.println(follower);
			if (centroid.getMappedIDsRealToVirtual().containsKey(follower))
				following++;
		}
		System.out.println(following / (double) (centroid.getFriendsSize()));
		BR.close();
	}
	
	public static void plot2() throws IOException {
		String p[][] = new String[60][5];
		
		FileReader FR = new FileReader(
				"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\dense.txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		int i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[1].equals("centroid:"))
				p[i][0] = Ids[2];
			else if(Ids[0].equals("F1:"))
				p[i++][1] = Ids[1];		
		}
		BR.close();
		
		FR = new FileReader(
		"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\dense + clas(5).txt");
		BR = new BufferedReader(FR);
		i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[0].equals("F1:"))
				p[i++][2] = Ids[1];		
		}
		BR.close();

		FR = new FileReader(
		"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\dense + clas(5) + circlize3(1,1,1).txt");
		BR = new BufferedReader(FR);
		i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[0].equals("F1:"))
				p[i++][3] = Ids[1];		
		}
		BR.close();

		FR = new FileReader(
		"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\dense + clas(5) + circlize4(1,1,1).txt");
		BR = new BufferedReader(FR);
		i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[0].equals("F1:"))
				p[i++][4] = Ids[1];		
		}
		BR.close();
		
		for(i = 0;i < 60;i++)
			for(int j = 0;j < 5;j++)
				System.out.print(p[i][j] + (j != 4 ? ',' : '\n'));
	}
	
	public static void plot1() throws IOException {
		String p[][] = new String[60][7];
		FileReader FR = new FileReader(
				"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\dense + clas(5) + circlize3(1,1,1).txt");
		BufferedReader BR = new BufferedReader(FR);
		String Text;
		int i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[1].equals("centroid:"))
				p[i][0] = Ids[2];
			else if(Ids[0].equals("Precision:"))
				p[i][1] = Ids[1];
			else if(Ids[0].equals("Recall:"))
				p[i][2] = Ids[1];
			else if(Ids[0].equals("F1:"))
				p[i++][3] = Ids[1];		
		}
		BR.close();
		FR = new FileReader(
		"C:\\Users\\Hamed Yami\\workspace\\Circle_Prediction\\Data_Sets\\kaggle circle suggstion\\lesko.txt");
		BR = new BufferedReader(FR);
		i = 0;
		while ((Text = BR.readLine()) != null) {
			String Separator = "\\s+";
			String[] Ids = Text.split(Separator);
			if(Ids.length < 2)
				continue;
			if(Ids[0].equals("Precision:"))
				p[i][4] = Ids[1];
			else if(Ids[0].equals("Recall:"))
				p[i][5] = Ids[1];
			else if(Ids[0].equals("F1:"))
				p[i++][6] = Ids[1];		
		}
		for(i = 0;i < 60;i++)
			for(int j = 0;j < 7;j++)
				System.out.print(p[i][j] + (j != 6 ? ',' : '\n'));
	}
	
}
