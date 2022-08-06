package graphVisualization;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class Edge {
	
	public static final String DEFAULT_EDGE_STYLE = "defaultEdge";

	
	//public String id;
	public String label;
	
	public String styleCategory;
	
	public String oppositeId;
	
	//public String sourceNID;
	//public String targetNID;
	public EReference ref;
	public EObject source;
	public EObject target;
	
	public boolean ignored;


	public Edge(String styleCategory, EObject source, EObject target, EReference ref) {
		//this.id = ref;//sourceNID + label + targetNID;
		this.label = ref.getName();
		this.styleCategory = styleCategory;
		this.oppositeId = null;
		//this.sourceNID = sourceNID;
		//this.targetNID = targetNID;
		this.ref = ref;
		this.source = source;
		this.target = target;
		this.ignored = false;

	}
	
	public Edge(EObject source, EObject target, EReference ref) {
		this(DEFAULT_EDGE_STYLE, source, target, ref);
	}
	
	public void setOppositeId(String oppId) {
		oppositeId = oppId;
	}
	
	public boolean hasOpposite() {
		return oppositeId != null;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(source, ref , target);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Edge e) {
			return (source.equals(e.source)) && (target.equals(e.target)) && (ref.equals(e.ref));	
		}
		return false;
		
	}
	
	public String getSourceID() {
		return String.valueOf(source.hashCode());
	}
	
	public String getTargetID() {
		return String.valueOf(target.hashCode());
	}

}
