package userInterface;

import java.awt.*;
import java.awt.event.*; 

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;

public class GraphManipulator {

	private Visualizer vis;
	private Resource resource;
	private InstanceDiagrammLoader loader;
	private EObject nodeInModel;
	private Object nodeInGraph;
	private mxGraph graph;

	public GraphManipulator(Visualizer vis, Resource resource, InstanceDiagrammLoader loader) {

		this.vis = vis;
		this.resource = resource;
		this.loader = loader;
		graph = vis.getGraph();
		//PopupFrame popup = new PopupFrame(vis);

	}

	public void iterateModel() {
		EList<EObject> objects = resource.getContents();
		Object[] selectedCells = graph.getSelectionCells();

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
		//((mxCell) nodeInGraph).removeFromParent();
		EcoreUtil.remove(nodeInModel); // delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
		//EmfUtil -> kein delte gefunden
		//emf listener weiß, welche änderungen vorgenommen wurden
		System.out.println("removed from model");
		
		//vis.getGraph().repaint();
		
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
		vis.getGraph().refresh();
	}
	
	private void addEdge() {
		
	}
	
	public void addNode(EClass cl) {
		EObject newObj = EcoreUtil.create(cl);
		resource.getContents().add(newObj);
		System.out.println("new obj created in model");
		Node newNode = new Node(newObj.toString(),newObj.eClass().getName(), "defaultNode");
		loader.nodes.add(newNode);
		System.out.println("added to list");
		graph.insertVertex(graph.getDefaultParent(),newNode.id, newNode.name,100,100,80,40);
		System.out.println("added in graph");
		for(EAttribute attr : cl.getEAttributes()) {
			System.out.println(attr.getName() + " , " + attr.getEAttributeType().getInstanceTypeName());
		}
		//cl.getEAllStructuralFeatures(); //alle Attribute + Kanten?
		//EReference Kanten! eindeutig oder null bis n (Liste), isMany 
		//Tutorial vogella
		//mxGraph repaint?
	}
}

