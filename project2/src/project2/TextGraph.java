package project2;

import java.io.*;
import java.util.*;

public class TextGraph {

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter name of input graph text file (with .txt extension): ");
		String inputFile = sc.nextLine();
		ArrayList<LinkedList<Integer>> adjacencyList = new ArrayList<LinkedList<Integer>>();
		
		
		try {
			FileReader frStream = new FileReader(inputFile);
			BufferedReader brStream = new BufferedReader(frStream);
			
			String inputLine, fromNode, toNode;
			int spaceIndex, node, neighbour;
			int max=-1;
			
			// storing id #num at index #num (id and index number of the array it is stored at are the same)
			inputLine = brStream.readLine();
			while (inputLine != null) {
				if (inputLine.startsWith("#"))
					inputLine = brStream.readLine();
				else {
					//System.out.println(inputLine);
					spaceIndex = inputLine.indexOf('\t');
					fromNode = inputLine.substring(0, spaceIndex);
					toNode = inputLine.substring(spaceIndex+1);
					node = Integer.parseInt(fromNode);
					neighbour = Integer.parseInt(toNode);
					if (node>max) {
						for (int j=max; j<node; j++) {
							adjacencyList.add(new LinkedList<Integer>());
						}
						max = node;
					}
					adjacencyList.get(node).add(neighbour);
					
					inputLine = brStream.readLine();
				}
			}
			brStream.close();
			
			/* id and index number of the array it's stored at are diff (store in consecutive array index as you go down the input text)
			int currIndex=0;
			System.out.println("The file contains:");
			inputLine = brStream.readLine();
			while (inputLine != null) {
				if (inputLine.startsWith("#"))
					inputLine = brStream.readLine();
				else {
					//System.out.println(inputLine);
					spaceIndex = inputLine.indexOf('\t');
					fromNode = inputLine.substring(0, spaceIndex);
					toNode = inputLine.substring(spaceIndex+1);
					node = Integer.parseInt(fromNode);
					neighbour = Integer.parseInt(toNode);
					if (adjacencyList[currIndex]==null) {
						adjacencyList[currIndex] = new LinkedList<Integer>();	// initialise each element in the array as a linked list
				    	adjacencyList[currIndex].add(node);
				    	adjacencyList[currIndex].add(neighbour);
				    	inputLine = brStream.readLine();
					}
					else if (node==adjacencyList[currIndex].getFirst()) {
						adjacencyList[currIndex].add(neighbour);
						inputLine = brStream.readLine();
					}
					else {
						currIndex++;
					}
				}
			}
			brStream.close(); */
		}
		catch (FileNotFoundException e) {
			System.out.println("Error opening the input file!" + e.getMessage());
			System.exit(0);
		}
		
		
		// output adjacency list to a text file
		try {
			FileWriter fwStream = new FileWriter("adjacency list.txt");
			BufferedWriter bwStream = new BufferedWriter(fwStream);
			PrintWriter pwStream = new PrintWriter(bwStream);			
			for (LinkedList<Integer> ll : adjacencyList) {
				if (!ll.isEmpty()) {	
					for (Integer v : ll) {
						pwStream.print(v);
						pwStream.print('\t');
					}
				pwStream.println();
				}
				
			}
			pwStream.close();
		}
		catch (IOException e) {
			System.out.print("IO Error!" + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}
