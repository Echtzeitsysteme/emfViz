package userInterface;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.mxgraph.model.mxCell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;

public class GraphManipulator {
	
	private Visualizer vis;
	private Resource resource;
	private InstanceDiagrammLoader loader;
	
	public GraphManipulator(Visualizer vis, Resource resource, InstanceDiagrammLoader loader) {
		
			this.vis = vis;
			this.resource = resource;
			this.loader = loader;

	}
	public void removeSelected() {
		EList<EObject> objects = resource.getContents();
		Object[] selectedCells = vis.getGraph().getSelectionCells();
		
		if(selectedCells.length > 0) {
			System.out.println("selected");
			for (Object selected : selectedCells) {
				vis.getGraph().getModel().remove(selected);
				for (EObject eObject : objects) {
					
					/*EList<EStructuralFeature> allEStructFeats = eObject.eClass().getEAllStructuralFeatures();
					
					for(EStructuralFeature esf : allEStructFeats)
					{
						System.out.println("blub");
					    Object o = eObject.eGet(esf);

					    if(true)
					    {
					    	Integer i = 0;
					        eObject.eSet(esf, i);
					        
					    }
					}*/
					iterateModelHierarchical(eObject, selected, loader);
				}
			}
		}

	}
	
	private void iterateModelHierarchical(EObject obj, Object comp, InstanceDiagrammLoader loader) {
		for (EObject eobj : obj.eContents()) {
			iterateModelHierarchical(eobj, comp, loader);
		}
		
		mxCell c = (mxCell) comp;
		if(obj.toString().equals(c.getId())) {
			System.out.println("equal found");
			EcoreUtil.remove(obj); //delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
			System.out.println("removed");
			/*for (Node nodeElement : loader.nodes) {
				if(nodeElement.id.equals(obj.toString())) {
					loader.nodes.remove(nodeElement);
				}
			}*/
		}
		System.out.println(obj.toString());
		System.out.println(c.getId());
	}
}
