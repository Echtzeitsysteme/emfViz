package visualisation;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
	private Map<EAttribute,Text> txtMap = new HashMap<EAttribute, Text>();
	private Map<EAttribute,Combo> enumMap = new HashMap<EAttribute, Combo>();
	private EObject newObj;
	private EAttribute errorAttr;

	public GraphManipulator(Visualizer vis, Resource resource, InstanceDiagrammLoader loader) {

		this.vis = vis;
		this.resource = resource;
		this.loader = loader;
		graph = vis.getGraph();
		//PopupFrame popup = new PopupFrame(vis);

	}

	private void iterateModel() {
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
		}
		//System.out.println(obj.toString());
		//System.out.println(c.getId());
	}
	
	public void deleteSelected() {
		iterateModel();
		removeNode();
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
		if(nodeInModel != null) {
			vis.getGraph().getModel().remove(nodeInGraph);
			//((mxCell) nodeInGraph).removeFromParent();
			EcoreUtil.remove(nodeInModel); // delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
			//EmfUtil -> kein delete gefunden
			//EmoflonUtil -> komplett nicht gefunden
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
		
	}
	
	private void addEdge() {
		
	}
	
	public void addNode(EClass cl) {
		newObj = EcoreUtil.create(cl);
		resource.getContents().add(newObj);
		System.out.println("new obj created in model");
		Node newNode = new Node(newObj.toString(),newObj.eClass().getName(), "defaultNode");
		loader.nodes.add(newNode);
		System.out.println("added to list");
		graph.insertVertex(graph.getDefaultParent(),newNode.id, newNode.name,100,100,80,40);
		System.out.println("added in graph");
		
		
		//cl.getEAllStructuralFeatures(); //alle Attribute + Kanten?
		//EReference Kanten! eindeutig oder null bis n (Liste), isMany 
		//Tutorial vogella
		//mxGraph repaint?
	}
	
	
	public void setAttributes(Display display) {
		
		iterateModel();
		if(nodeInModel != null) {
			Shell shellAttr = new Shell(display);
			
			shellAttr.setText("Set Attributes");
			
			/*shellAttr.addListener(SWT.Close, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("close");
			    }
			});*/
			
			
			shellAttr.setLayout(new GridLayout());
			shellAttr.setBackground(shellAttr.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			
			Composite composite = new Composite(shellAttr, SWT.EMBEDDED);
			composite.setVisible(true);
			
			GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true);
			//gridData1.horizontalSpan = 3;
			
			composite.setLayoutData(gridData1);
			composite.setLayout(new GridLayout(3, true));
			
			
			EList<EAttribute> attributes = nodeInModel.eClass().getEAttributes();
			/////LÖSCHEN////
			for(EAttribute attr : attributes) {
				System.out.println(attr.getName() + " , " + attr.getEAttributeType().getInstanceTypeName());
				//cl.eSet(attr, (Integer) 3);
			}
			for(EAttribute attr: attributes) {
				//Group group = new Group(composite, SWT.None);
				//group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				//group.setLayout(new GridLayout(3, true));
				Label labelName = new Label(composite, SWT.None);
				labelName.setText(attr.getName());
				Label labelValue = new Label(composite, SWT.None);
				labelValue.setText(attr.getEAttributeType().getInstanceTypeName());
				
				if(attr.getEType() instanceof EEnum) {
					EEnum eenum = (EEnum) attr.getEType();
					int len = eenum.getELiterals().size();
					String[] literals = new String[len];
					int i = 0;
					for(EEnumLiteral literal : eenum.getELiterals()) {
						//System.out.println("	" + literal);
						literals[i] = literal.getName();
						i++;
					}
					
					Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
					combo.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
				    combo.setItems(literals);
				    combo.setText(nodeInModel.eGet(attr).toString());
				    enumMap.put(attr,combo);
				}
				else {
					Text txt = new Text(composite, SWT.BORDER | SWT.TRAIL);
					txt.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
					if(attr.getDefaultValueLiteral() != null) txt.setText(nodeInModel.eGet(attr).toString());
					txtMap.put(attr, txt);
				}
				//verify that input is of correct type for the attribute
				//passiert das schon? was passiert im Fehlerfall?
				/*txt.addVerifyListener(new VerifyListener(){
				      public void verifyText(VerifyEvent arg0) {
				          System.out.println("verifying");
				          
				        }});*/
				
			}
			
			//control buttons / composite 
			Composite compositeCtrl = new Composite(shellAttr, SWT.EMBEDDED);
			compositeCtrl.setVisible(true);
			compositeCtrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compositeCtrl.setLayout(new RowLayout());
			
			Button nextBT = new Button(compositeCtrl, SWT.PUSH |SWT.RIGHT);
			nextBT.setText("Done");
			nextBT.setAlignment(SWT.CENTER);
			
			nextBT.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent pSelectionEvent) {
					
					try {
						setAttributesInModel();
						shellAttr.close();
					}
					catch(Exception e) {
						for (EAttribute attr : txtMap.keySet()) {
							if (attr.equals(errorAttr)) {
								txtMap.get(attr).setBackground(display.getSystemColor(SWT.COLOR_RED));
							}
							else {
								txtMap.get(attr).setBackground(display.getSystemColor(SWT.COLOR_WHITE));
							}
						}
						
						
					}
					
				}
			});
			
			
			//set size of shell
			shellAttr.setSize(800,400);	
			shellAttr.open();
		}
		
		
	}
	
	private void setAttributesInModel() throws Exception {
		for (EAttribute attr : txtMap.keySet()) {
			System.out.println(txtMap.get(attr).getText());
			errorAttr = attr;
			EDataType type = attr.getEAttributeType();
			String input = txtMap.get(attr).getText();
			//Wird nicht gesetzt??
			nodeInModel.eSet(attr, createFromString(type,input));
		}
		for (EAttribute attr : enumMap.keySet()) {
			System.out.println(enumMap.get(attr).getText());
			EDataType type = attr.getEAttributeType();
			String input = enumMap.get(attr).getText();
			//Wird nicht gesetzt?? //get instance?
			nodeInModel.eSet(attr, createFromString(type,input));
		}
	}
	
	
	private Object createFromString(EDataType eDataType, String literal) throws Exception
	{
	 return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, literal);
	}
}

