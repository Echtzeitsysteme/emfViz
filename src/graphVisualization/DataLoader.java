package graphVisualization;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class DataLoader {
	
	protected DataLoader() {
		
		nodes = new ArrayList<Node>();
		edges = new HashMap<String, ArrayList<Edge>>();
	}

	//public HashMap<String, String> edgeOpposites;
	public ArrayList<Node> nodes;
	public HashMap<String, ArrayList<Edge>> edges;
	
	protected abstract void loadData();
}
