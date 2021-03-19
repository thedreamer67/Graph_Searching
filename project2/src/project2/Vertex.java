package project2;

//import java.util.ArrayList;

public class Vertex {
	private final int label;
	private boolean marked;
	private int nVisited;
	private int[] sources;
	private int nRemoved;
	
	public Vertex(int num, int s_length){
		this.label = num;          
		this.marked = false;
		this.nVisited = 0;
		this.nRemoved = -1;
		sources = new int[s_length];
	}

	public void mark(){ this.marked = true; }	
	public boolean isMarked() {
		return marked;
	}
	
	public int getnVisited() {
		return nVisited;
	}

	public void incVisited() {
		this.nVisited = this.nVisited + 1;
	}
	
	public void addSource(int s) {
		sources[s] = 1;
	}
	
	public boolean checkSource(int s) {
		if (sources[s]==1) {return true;}
		return false;
	}
	
	public void incRemoved() {
		this.nRemoved = this.nRemoved + 1;
	}
	
	public int getnRemoved() {
		return this.nRemoved;
	}

	

	public int getLabel() {
		return label;
	}

}
