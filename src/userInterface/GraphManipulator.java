package userInterface;

import java.awt.*;
import java.awt.event.*; 

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
	private EObject nodeInModel;
	private Object nodeInGraph;

	public GraphManipulator(Visualizer vis, Resource resource, InstanceDiagrammLoader loader) {

		this.vis = vis;
		this.resource = resource;
		this.loader = loader;
		//PopupFrame popup = new PopupFrame(vis);

	}

	public void iterateModel() {
		EList<EObject> objects = resource.getContents();
		Object[] selectedCells = vis.getGraph().getSelectionCells();

		if (selectedCells.length > 0) {
			System.out.println("selected");
			for (Object selected : selectedCells) {
				nodeInGraph = selected;
				for (EObject eObject : objects) {

					/*
					 * EList<EStructuralFeature> allEStructFeats =
					 * eObject.eClass().getEAllStructuralFeatures();
					 * 
					 * for(EStructuralFeature esf : allEStructFeats) { System.out.println("blub");
					 * Object o = eObject.eGet(esf);
					 * 
					 * if(true) { Integer i = 0; eObject.eSet(esf, i);
					 * 
					 * } }
					 */
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
		if (obj.toString().equals(c.getId())) {
			System.out.println("equal found");
			nodeInModel = obj;
			removeNode();
		}
		//System.out.println(obj.toString());
		//System.out.println(c.getId());
	}

	private void actionOnNode() {
		// open menu on node or on background
		/*final PopupMenu popupmenu = new PopupMenu("Edit");   
        
		MenuItem delete = new MenuItem("Delete");  
		MenuItem newEdge = new MenuItem("New edge");  
        delete.setActionCommand("Delete");  
        newEdge.setActionCommand("New edge");   
        delete.setActionCommand("D");
        newEdge.setActionCommand("NE");
        popupmenu.add(delete);  
        popupmenu.add(newEdge);
        //statt frame auf knoten?
        Frame f = vis.getFrame();
		f.addMouseListener(new MouseAdapter() {  
            public void mouseClicked(MouseEvent e) {              
                popupmenu.show(f , e.getX(), e.getY());  
            } 
		});
		
		
		
		f.add(popupmenu);   
        f.setSize(400,400);  
        f.setLayout(null);  
        f.setVisible(true); */
		
		
	}
	
	
	
	private void removeNode() {
		vis.getGraph().getModel().remove(nodeInGraph);
		EcoreUtil.remove(nodeInModel); // delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
		System.out.println("removed from model");
		Node deleteNode = null;
		for (Node nodeElement : loader.nodes) {
			if (nodeElement.id.equals(nodeInModel.toString())) {
				deleteNode = nodeElement;
			}
		}
		if (deleteNode != null) {
			loader.nodes.remove(deleteNode);
			System.out.println("removed from list");
		}
	}
	
	private void addEdge() {
		
	}
}
