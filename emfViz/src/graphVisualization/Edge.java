package graphVisualization;

public class Edge {
	
	public String id;
	public String label;
	
	public String styleCategory;
	
	public String oppositeId;
	
	public String sourceNID;
	public String targetNID;

	public Edge(String label, String styleCategory, String sourceNID, String targetNID) {
		this.id = sourceNID + label + targetNID;
		this.label = label;
		this.styleCategory = styleCategory;
		this.oppositeId = null;
		
		this.sourceNID = sourceNID;
		this.targetNID = targetNID;
	}
	
	public void setOppositeId(String oppId) {
		oppositeId = oppId;
	}
	
	public boolean hasOpposite() {
		return oppositeId != null;
	}

}
