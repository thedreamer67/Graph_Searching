package project2;

import java.io.*;
import java.util.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;

public class Experiment {
	
	// used in finding nearest hospital
	public static int[] dist; // stores the distance to the nearest hospital for each of the node. 0 if node doesn't exist
	public static int[] parent; // stores the predecessor to each of the node, -1 if its the hospital and -99 if node doesn't exist
	
	// used in computing nearest k hospitals
	public static int[][] distances;// nth row has the distance to the nth nearest hospital
	public static int[][][] parents;//nth row has the [predecessor, row of the predecessor to look for the next predecessor] for the nth nearest hospital from each node
	public static int v_max; // largest nodeID
	public static int h_size; // total number of hospitals

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		
		
		ArrayList<LinkedList<Vertex>> adjacencyList = new ArrayList<LinkedList<Vertex>>(); // stores graph in adjacency list
		ArrayList<Vertex> hospitals = new ArrayList<Vertex>(); // array of hospitals with vertex objects
		LinkedHashMap<Integer, Vertex> vertexHash = new LinkedHashMap<Integer, Vertex>(); //keeps track of the vertex objects created
		

		//get the hospital file and change to desired format (array as seen below)
		
		// hospital array used to test
		int[] h = {3, 751, 120, 812, 181, 549, 172, 1000, 781, 961, 275, 988, 286, 388, 343, 155, 978, 47, 107, 334, 602, 640, 631, 641, 922, 240, 289, 875, 957, 604, 195, 109, 525, 8, 220, 475, 612, 193, 37, 247, 650, 803, 647, 137, 411, 153, 536, 828, 870, 136, 277, 865, 555, 228, 238, 101, 972, 848, 127, 735, 644, 613, 480, 728, 307, 138, 342, 232, 705, 819, 747, 55, 694, 734, 433, 540, 231, 405, 580, 600, 171, 838, 885, 888, 599, 88, 931, 574, 932, 915, 425, 440, 364, 752, 163, 414, 775, 454, 740, 99};
//		System.out.print("Enter name of text file with hospital information (with .txt extension): ");
//		String inputHospitals = sc.nextLine();
//		System.out.print("Enter the number of hospitals: ");
//		int numHosp = sc.nextInt();
//		sc.nextLine();
//		int[] h = new int[numHosp];
		
//		try {
//			FileReader frStream1 = new FileReader(inputHospitals);
//			BufferedReader brStream1 = new BufferedReader(frStream1);
//			
//			String inputLine="";
//			int indexH=0;
//			
//			// store hospitals in an array
//			inputLine = brStream1.readLine();
//			while (inputLine != null) {
//				if (inputLine.startsWith("#")) 
//					inputLine = brStream1.readLine();
//				else {
//					h[indexH] = Integer.parseInt(inputLine);
//					indexH++;
//					inputLine = brStream1.readLine();
//				}
//			}
//			brStream1.close();
//		}
//		catch (FileNotFoundException e) {
//			System.out.println("Error opening the hopsital input file!" + e.getMessage());
//			System.exit(0);
//		}
		
		// sort hospital array to be in ascending order
		Arrays.sort(h);
		
		// convert array of hospital indexes to hospital vertex objects
		h_size = h.length;
		Vertex x;
		for (int i : h) {
			x = new Vertex(i, h[h_size-1]+1);
			hospitals.add(x);
			vertexHash.put(x.getLabel(), x);
		}
				
		System.out.println("Choose to use a random graph or a graph stored in a text file as the input graph");
		System.out.print("Choose 1 for random graph, or 2 for graph text file: ");
		int graphChoice = sc.nextInt();
		sc.nextLine();
		switch (graphChoice) {
			// using random graph from random graph generator as the input graph
			case 1:
				System.setProperty("org.graphstream.ui", "swing"); 
				
				System.out.print("Enter the number of nodes the graph should have: ");
				int n = sc.nextInt();
				
				// create a random graph with n nodes and average degree of vertex = 3
				Graph randomGraph = new SingleGraph("Random");
			    Generator gen = new RandomGenerator(5);
			    gen.addSink(randomGraph);
			    gen.begin();
			    for(int i=0; i<n-6; i++)
			        gen.nextEvents();
			    gen.end();
			    randomGraph.display();
			    
			    //System.out.println("node count: " + randomGraph.getNodeCount());	// check the number of nodes in graph
			    
			    // create the adjacency list
			    Vertex v1 = new Vertex(-1,  h[h_size-1]+1);
			    for (int i=0; i<n; i++) {
			    	adjacencyList.add(new LinkedList<Vertex>());
			    	for (int j=0; j<n; j++) {
			    		if (randomGraph.getNode(i).hasEdgeBetween(j)) {
				    		if (vertexHash.get(j) == null) {
								v1 = new Vertex(j,  h[h_size-1]+1);
								vertexHash.put(v1.getLabel(), v1);
							}
				    		adjacencyList.get(i).add(vertexHash.get(j));
			    		}
			    	}	
			    }
			    
			    break;
			// using a text file as the input graph
			case 2:
				// convert input graph text file to adjacency list
				System.out.print("Enter name of input graph text file (with .txt extension): ");
				String inputFile = sc.nextLine();
				
				try {
					FileReader frStream = new FileReader(inputFile);
					BufferedReader brStream = new BufferedReader(frStream);
					
					String inputLine, fromNode, toNode;
					int tabIndex, node, neighbour;
					int max=-1;
					
					// storing neighbours of id #num at index #num (id and index number of the arraylist its neighbours are stored at are the same)
					inputLine = brStream.readLine();
					Vertex v = new Vertex(-1,  h[h_size-1]+1);
					while (inputLine != null) {
						if (inputLine.startsWith("#"))
							inputLine = brStream.readLine();
						else {
							tabIndex = inputLine.indexOf('\t');
							fromNode = inputLine.substring(0, tabIndex);
							toNode = inputLine.substring(tabIndex+1);
							node = Integer.parseInt(fromNode);
							neighbour = Integer.parseInt(toNode);
							if (vertexHash.get(neighbour) == null) {
								v = new Vertex(neighbour,  h[h_size-1]+1);
								vertexHash.put(v.getLabel(), v);
							}
							if (node>max) {
								for (int j=max; j<node; j++) {
									adjacencyList.add(new LinkedList<Vertex>());
								}
								max = node;
							}
							adjacencyList.get(node).add(vertexHash.get(neighbour));
							inputLine = brStream.readLine();
						}
						
					}
					brStream.close();		
				}
				catch (FileNotFoundException e) {
					System.out.println("Error opening the graph input file!" + e.getMessage());
					System.exit(0);
				}
				break;			
		}
		
		
		
		v_max = getMaxEntryInMapBasedOnKey(vertexHash) + 1; // get the largest node id
		
		
		int choice;
		int k;
		FileWriter fwStream;
		BufferedWriter bwStream;
		PrintWriter pwStream;
		long startTime;
		long stopTime;
		
		System.out.println("1. Run BFS to find the nearest hospital");
		System.out.println("2. Run BFS to find the nearest k hospitals");
		System.out.println("-------choose one of the options:");
		choice = sc.nextInt();
		
		switch(choice) {
			case 1:
				//initialize the matrices and default values
				dist = new int[v_max];
				parent = new int[v_max];
				Arrays.fill(parent, -99);
				
				startTime = System.currentTimeMillis();
				multibfs(hospitals,adjacencyList);
				stopTime = System.currentTimeMillis();
				System.out.println("\nTime taken for multibfs() is: " + (stopTime-startTime));
				
				
				System.out.println("1. Output results to a file 'output.txt'");
				System.out.println("2. Print all results to console");
				System.out.println("3. Print results at one of the node to console");
				System.out.println("-------choose one of the options:");
				choice = sc.nextInt();
				
				switch(choice) {
					case 1:
						fwStream = new FileWriter("output.txt");
						bwStream = new BufferedWriter(fwStream);
						pwStream = new PrintWriter(bwStream);			
						for (int i = 0; i< v_max; i++) {
							pwStream.println("The distance to the nearest hospital from node "+i+" is: "+ dist[i]);
							printpath_feed(i, pwStream);
							pwStream.println("");
						}
						pwStream.close();
						System.out.println("Results have been stored in 'output.txt'");
						break;
					case 2:
						for (int i = 0; i< v_max; i++) {
							System.out.println("The distance to the nearest hospital from node "+i+" is: "+ dist[i]);
							printpath(i);
							System.out.println("");
						}
						break;
					case 3:
						System.out.println("Enter the node you want to check:");
						choice = sc.nextInt();
						System.out.println("The distance to the nearest hospital from node "+choice+" is: "+ dist[choice]);
						printpath(choice);
						System.out.println("");
						break;
					default:
						System.out.println("Invalid Input!");
				}
				
				
				break;
			case 2:
				System.out.println("Enter k:");
				k = sc.nextInt();
				
				//initialize the matrices and default values
				distances = new int[k][v_max];
				parents = new int[k][v_max][2];
				for (int i = 0; i<k; i++) {
					for (int j = 0; j<v_max; j++) {
						Arrays.fill(parents[i][j], -99);
					}
				}
				
				startTime = System.currentTimeMillis();
				multibfs_k(hospitals,adjacencyList,k);
				stopTime = System.currentTimeMillis();
				System.out.println("\nTime taken for multibfs_k() is:" + (stopTime-startTime));
				
				
				System.out.println("1. Output results to a file 'output.txt'");
				System.out.println("2. Print all results to console");
				System.out.println("3. Print results at one of the node to console");
				System.out.println("-------choose one of the options:");
				choice = sc.nextInt();
				
				switch(choice) {
					case 1:
						fwStream = new FileWriter("output.txt");
						bwStream = new BufferedWriter(fwStream);
						pwStream = new PrintWriter(bwStream);	
						for (int i = 0; i<v_max; i++) {
							pwStream.println("The distance to the nearest "+k+" hospitals from node "+i+" (from nearest to furthest) is: ");
							for (int j= 0; j<k; j++) {
								pwStream.println("distance: "+ distances[j][i]);
								printpath_kfeed(i,j, pwStream);
							}
							pwStream.println("");
						}
						pwStream.close();
						System.out.println("Results have been stored in 'output.txt'");
						break;
					case 2:
						for (int i = 0; i<v_max; i++) {
							System.out.println("The distance to the nearest "+k+" hospitals from node "+i+" (from nearest to furthest) is: ");
							for (int j= 0; j<k; j++) {
								System.out.println("distance: "+ distances[j][i]);
								printpath_k(i,j);
							}
							System.out.println("");
						}
						break;
					case 3:
						System.out.println("Enter the node you want to check:");
						choice = sc.nextInt();
						System.out.println("The distance to the nearest "+k+" hospitals from node "+choice+" (from nearest to furthest) is: ");
						for (int j= 0; j<k; j++) {
							System.out.println("distance: "+ distances[j][choice]);
							printpath_k(choice,j);
						}
						System.out.println("");
						break;
					default:
						System.out.println("Invalid Input!");
				}
				break;
			default:
				System.out.println("Invalid Input!");
		}
		sc.close();	
			
	
		//output adjacency list to a text file to check
//		try {
//			FileWriter fwStream3 = new FileWriter("adjacency list.txt");
//			BufferedWriter bwStream3 = new BufferedWriter(fwStream3);
//			PrintWriter pwStream3 = new PrintWriter(bwStream3);			
//			for (LinkedList<Vertex> ll : adjacencyList) {
//				if (!ll.isEmpty()) {	
//					for (Vertex v : ll) {
//						pwStream3.print(v.getLabel());
//						pwStream3.print('\t');
//					}
//				pwStream3.println();
//				}
//				
//			}
//			pwStream3.close();
//		}
//		catch (IOException e) {
//			System.out.print("IO Error!" + e.getMessage());
//			e.printStackTrace();
//			System.exit(0);
//		}
		
	}
		

	public static void multibfs(ArrayList<Vertex> hospitals, ArrayList<LinkedList<Vertex>> adjacencyList)
	{
		Queue<Vertex> q=new LinkedList<Vertex>();
		
		for (int i=0; i < hospitals.size(); i++ )
		{
			hospitals.get(i).mark();
			dist[hospitals.get(i).getLabel()] = 0;
			parent[hospitals.get(i).getLabel()] = -1;
			
			q.add(hospitals.get(i));
		}
		
		while(!q.isEmpty())
		{
			Vertex n=(Vertex)q.remove();
			LinkedList<Vertex> vertexLinkedList = adjacencyList.get(n.getLabel());
			for (Vertex neighbour : vertexLinkedList) {
				if(!neighbour.isMarked()) {
					neighbour.mark();
					dist[neighbour.getLabel()] = dist[n.getLabel()] +1;
					parent[neighbour.getLabel()]= n.getLabel();
					q.add(neighbour);
				}
			}
		}
	}
	public static void multibfs_k(ArrayList<Vertex> hospitals, ArrayList<LinkedList<Vertex>> adjacencyList, int k)
	{	
		Queue<Vertex[]> q = new LinkedList<Vertex[]>();

		for (Vertex i : hospitals)
		{
			i.incVisited();
			i.addSource(i.getLabel());
			parents[0][i.getLabel()][0] = -1;
			Vertex[] pairs = {i,i};
			q.add(pairs);
		}
		while(!q.isEmpty())
		{
			Vertex[] v=(Vertex[])q.remove();
			Vertex n = v[0];
			Vertex s = v[1];
			n.incRemoved();
			LinkedList<Vertex> vertexLinkedList = adjacencyList.get(n.getLabel());
			for (Vertex neighbour : vertexLinkedList) {
				if(neighbour.getnVisited()<k && !neighbour.checkSource(s.getLabel())) {
					neighbour.incVisited();
					neighbour.addSource(s.getLabel());
					distances[neighbour.getnVisited()-1][neighbour.getLabel()] = distances[n.getnRemoved()][n.getLabel()] +1;
					parents[neighbour.getnVisited()-1][neighbour.getLabel()][0]= n.getLabel();
					parents[neighbour.getnVisited()-1][neighbour.getLabel()][1]= n.getnRemoved();
					Vertex[] pairs = {neighbour, s};
					q.add(pairs);
				}
			}
		}
	}	
	public static void printpath(int vertex) {
		
		if (vertex == -1) {
			System.out.println(" -This is the nearest hospital");
		}
		else if (parent[vertex] == -99) {
			System.out.println("Such a node id does not exist");
		}
		else {
			if (parent[vertex] == -1) {
				System.out.print(vertex);
			}
			else {
				System.out.print(vertex + " --> ");
			}
			printpath(parent[vertex]);
			
		}
	}
	public static void printpath_feed(int vertex, PrintWriter pw) {
			
			if (vertex == -1) {
				pw.println(" -This is the nearest hospital");
			}
			else if (parent[vertex] == -99) {
				pw.println("Such a node id does not exist");
			}
			else {
				if (parent[vertex] == -1) {
					pw.print(vertex);
				}
				else {
					pw.print(vertex + " --> ");
				}
				printpath_feed(parent[vertex], pw);
				
			}
		}
	public static void printpath_k(int vertex, int k) {
			
			if (vertex == -1) {
				System.out.println(" -This is the nearest hospital");
			}
			else if (parents[k][vertex][0] == -99) {
				System.out.println("Such a node id does not exist");
			}
			else {
				if (parents[k][vertex][0] == -1) {
					System.out.print(vertex);
				}
				else {
					System.out.print(vertex + " --> ");
				}
				printpath_k(parents[k][vertex][0], parents[k][vertex][1]);
				
			}
		}
	public static void printpath_kfeed(int vertex, int k, PrintWriter pw) {
		
		if (vertex == -1) {
			pw.println(" -This is the nearest hospital");
		}
		else if (parents[k][vertex][0] == -99) {
			pw.println("Such a node id does not exist");
		}
		else {
			if (parents[k][vertex][0] == -1) {
				pw.print(vertex);
			}
			else {
				pw.print(vertex + " --> ");
			}
			printpath_kfeed(parents[k][vertex][0], parents[k][vertex][1], pw);
			
		}
	}
	
	public static int getMaxEntryInMapBasedOnKey(LinkedHashMap<Integer, Vertex> map) 
    { 
        Map.Entry<Integer, Vertex> entryWithMaxKey = null; 
  
        for (Map.Entry<Integer, Vertex> currentEntry : map.entrySet()) { 
  
            if ( 
                entryWithMaxKey == null
                || currentEntry.getKey() 
                           .compareTo(entryWithMaxKey.getKey()) 
                       > 0) { 
  
                entryWithMaxKey = currentEntry; 
            } 
        }
        return entryWithMaxKey.getKey();
    }
}
