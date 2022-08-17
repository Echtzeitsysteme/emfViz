package graphVisualization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.emoflon.smartemf.runtime.SmartPackage;



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
				nodes.add(new Node("defaultNode", node));
						
				List<EStructuralFeature> features = node.getEAllStructuralFeatures();
				
				//ArrayList<EReference> refs = new ArrayList<EReference>();
				
				
				for(EStructuralFeature f : features ) {
					
					if(f instanceof EReference ref) {
						
						
						Edge visEdge = new Edge("defaultEdge", node, ((EObject) node.eGet(ref)),  ref);
						
						EReference opp = ((EReference) f).getEOpposite();
						
						if(opp != null) {
							visEdge.setOppositeId(f.getEType().getName()+opp.getName()+node.getName());
						}
						
					
	
						edges.put(visEdge.hashCode(), visEdge);
					}
					
				}
				
				
			}
		}
		

	}

}
