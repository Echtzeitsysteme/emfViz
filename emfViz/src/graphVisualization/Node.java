package graphVisualization;

import java.io.Serializable;

import org.eclipse.emf.ecore.EObject;

public class Node implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NODE_STYLE = "defaultNode";
	
	public String id;
	public String name;
	public String styleCategory;
	public transient EObject eobj;
	public boolean ignored;

	public Node(String styleCategory, EObject eobj) {
		this.name = eobj.eClass().getName();
		this.styleCategory = styleCategory;
		this.eobj = eobj;
		this.ignored = false;
	}
	
	public Node(EObject eobj) {
		this(DEFAULT_NODE_STYLE, eobj);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return eobj.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node n) {
			return n.eobj.equals(eobj);
		}
		
		return false;
	}

}
