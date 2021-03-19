package project2;

import java.io.*;
import java.util.*;

public class HospitalText {
	
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
		System.out.print("Enter name of text file with hospital information (with .txt extension): ");
		String inputFile = sc.nextLine();
		System.out.print("Enter the number of hospitals: ");
		int numHosp = sc.nextInt();
		int[] h = new int[numHosp];
		
		
		try {
			FileReader frStream = new FileReader(inputFile);
			BufferedReader brStream = new BufferedReader(frStream);
			
			String inputLine="";
			int indexH=0;
			
			// store hospitals in an array
			inputLine = brStream.readLine();
			while (inputLine != null) {
				if (inputLine.startsWith("#")) 
					inputLine = brStream.readLine();
				else {
					h[indexH] = Integer.parseInt(inputLine);
					indexH++;
					inputLine = brStream.readLine();
				}
			}
			brStream.close();
			
			
		}
		catch (FileNotFoundException e) {
			System.out.println("Error opening the input file!" + e.getMessage());
			System.exit(0);
		}
		
		
		
	}
}
