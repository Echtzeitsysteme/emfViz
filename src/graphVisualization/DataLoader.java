package graphVisualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public abstract class DataLoader {
	
	protected DataLoader() {
		
		nodes = new HashSet<Node>();
		edges = new HashMap<Integer, Edge>();
		//modelToGraphEdges = new HashMap<Integer,Edge>();
	}

	//public HashMap<String, String> edgeOpposites;
	public Collection<Node> nodes;
	public Map<Integer,Edge> edges;
	
	//public Map<Integer,Edge> modelToGraphEdges;
	
	// unique mapping from eObject to ID of associated visualization
	public Map<EObject, Integer> eobjIdmap;
	
	protected abstract void loadData();
}
