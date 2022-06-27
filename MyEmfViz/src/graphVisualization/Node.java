package graphVisualization;

public class Node {
	
	public String id;
	public String name;
	
	public String styleCategory;

	public Node(String id, String name, String styleCategory) {
		this.id = id;
		this.name = name;
		this.styleCategory = styleCategory;
	}
	
	@Override
	public String toString() {
		return "id: "+ id + " , name: " + name + " , styleCategory: " + styleCategory;
	}


}
