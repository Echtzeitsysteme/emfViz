package graphVisualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.emoflon.smartemf.runtime.SmartPackage;
import com.mxgraph.view.mxGraph;


public class ClassDiagrammLoader extends DataLoader {
	
	SmartPackage metaModel;

	public ClassDiagrammLoader(SmartPackage pkg) {
		super();
		metaModel = pkg;
	}

	
	
	@Override
	protected void loadData() {
		//ArrayList<EClassImpl> nodes = new ArrayList<EClassImpl>();
		//HashMap<String, ArrayList<EReference>> edges = new HashMap<String, ArrayList<EReference>>();
		
		for(EObject o : metaModel.eContents()) {
			
			if(o instanceof EClassImpl) {	
				EClassImpl node = (EClassImpl) o;
				
				//nodes.add(node);
				nodes.add(new Node(node.getName(), node.getName(), "defaultNode"));
						
				List<EStructuralFeature> features = node.getEAllStructuralFeatures();
				
				//ArrayList<EReference> refs = new ArrayList<EReference>();
				
				ArrayList<Edge> outgoingEdges = new ArrayList<Edge>();
				
				for(EStructuralFeature f : features) {
					
					if(f instanceof EReference) {
						
						EReference edge = (EReference) f;
						
						Edge visEdge = new Edge(f.getName(), "defaultEdge", node.getName(), f.getEType().getName());
						
						EReference opp = ((EReference) f).getEOpposite();
						
						if(opp != null) {
							visEdge.setOppositeId(f.getEType().getName()+opp.getName()+node.getName());
						}
						
						outgoingEdges.add(visEdge);
					}
					
				}
				
				edges.put(node.getName(), outgoingEdges);
			}
		}
		

	}

}
