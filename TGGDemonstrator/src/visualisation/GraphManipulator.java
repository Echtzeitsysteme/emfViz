package visualisation;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emoflon.ibex.common.emf.EMFManipulationUtils;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxEvent;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;
import tggDemonstrator.TGGDemonstrator;
import visualisation.CallbackHandler.UpdateGraphType;

public class GraphManipulator {

	private TggVisualizer vis;
	private Resource resource;
	private InstanceDiagrammLoader loader;
	private Display display;
	private TGGDemonstrator modelLoader;
	private boolean isSource;
	private CallbackHandler callback;
	private EObject nodeInModel;
	private Object nodeInGraph;
	private Object edgeInGraph;
	private mxGraph graph;
	private Map<EAttribute,Text> txtMap = new HashMap<EAttribute, Text>();
	private Map<EAttribute,Combo> enumMap = new HashMap<EAttribute, Combo>();
	private EObject newObj;
	private EAttribute errorAttr;
	private EReference eRefSelected;
	private mxGraphComponent graphComponent;
	private mxCell cellAtPos;

	public GraphManipulator(TggVisualizer vis, Display display, InstanceDiagrammLoader loader, TGGDemonstrator modelLoader, boolean isSource) {

		this.vis = vis;
		this.display = display;
		this.resource = loader.getInstanceModel();
		this.loader = loader;
		this.modelLoader = modelLoader;
		this.isSource = isSource;
		graph = vis.getGraph();
		graphComponent = vis.getGraphComponent();
		callback = CallbackHandler.getInstance();


	}
	
	public void initialize() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			public void mouseReleased(MouseEvent e)
			{
				cellAtPos = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
				
				if(e.getButton() == MouseEvent.BUTTON3) {
					if (cellAtPos != null)
					{
						if(cellAtPos.isVertex()) {
							System.out.println("cell="+graph.getLabel(cellAtPos));
							actionOnNode(e.getX(), e.getY());
							
						}
						else {
							addEdge();
						}
					}
					else {
						actionOnFrame(e.getX(), e.getY());
						callback.setPositionForNewNode(e.getX(), e.getY());
					}
					
					
				}
				
				
			}
			//löst nicht aus
			public void mouseDragged(MouseEvent e) 
			{
				System.out.println("Mouse dragged");
				addEdge();
			}

			
		});
		graph.addListener("", (sender, evt) -> System.out.println(sender + " ---- " + evt));
		graph.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);				
			}
		});
		
		graph.getView().addListener("", (sender, evt) -> System.out.println(sender + " ---- " + evt));
	}
/*
	private void iterateModel() {
		EList<EObject> objects = resource.getContents();
		Object[] selectedCells = graph.getSelectionCells();
		if (selectedCells.length > 0) {
			System.out.println("selected");
			for (Object selected : selectedCells) {
				//nodeInGraph = selected;
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
					 
					iterateModelHierarchical(eObject, selected);
				}
			}
		}

	}
*/	
	private void iterateModelMousePosition() {
		EList<EObject> objects = resource.getContents();
		
			for (EObject eObject : objects) {

				iterateModelHierarchical(eObject, cellAtPos);
			}

	}

	private void iterateModelHierarchical(EObject obj, Object comp) {
		for (EObject eobj : obj.eContents()) {
			iterateModelHierarchical(eobj, comp);
		}

		if (obj.eClass().getName().equals(((mxCell)comp).getValue().toString())) {
			System.out.println("equal found");
			nodeInModel = obj;
		}
		
		if(((mxCell)comp).isEdge()) {
			edgeInGraph = comp;
			
		}
	}
	
	public void deleteSelected() {
		iterateModelMousePosition();
		removeNode();
		callback.updateGraph(UpdateGraphType.ALL);
	}

	private void actionOnNode(int x, int y) {
		// open menu on node or on background
		final PopupMenu popupmenu = new PopupMenu("On Node");   
        
		MenuItem delete = new MenuItem("Delete");  
		MenuItem attr = new MenuItem("Show Attributes");
        delete.setActionCommand("DEL");
        attr.setActionCommand("ATTR");
        popupmenu.add(delete);  
        popupmenu.add(attr);
       
        graphComponent.add(popupmenu);
        popupmenu.show(graphComponent , x, y);
        
        delete.addActionListener(new ActionListener() {
        	@Override
		    public void actionPerformed(ActionEvent e) {
				deleteSelected();
				//x und y weitergeben an automatische Visualisierung
        		System.out.println("Delete clicked");
        	}
        });
        
        attr.addActionListener(new ActionListener() {
        	@Override
		    public void actionPerformed(ActionEvent e) {
				setAttributesExec();
        		System.out.println("Attributes clicked");
        	}
        });
        
	}
	
	private void actionOnFrame(int x, int y) {
		PopupMenu popupMenu = new PopupMenu("On Frame");

		List<EClassImpl> classes = new ArrayList<EClassImpl>();
		// modelLoader.getOptions().tgg.tgg().getSrc().get(0).eContents();
		// ibxopt.tgg.tgg().getSrc().get(0).eContents(); //sind die Klassen da drin?
		if(isSource) {
			for (EObject obj : modelLoader.getOptions().tgg.tgg().getSrc().get(0).eContents()) {
				if (obj instanceof EClassImpl) {
					EClassImpl node = (EClassImpl) obj;
					if (!((EClass)node).isAbstract()) {
						classes.add(node);
					}
				}
			}
		}
		else {
			for (EObject obj : modelLoader.getOptions().tgg.tgg().getTrg().get(0).eContents()) {
				if (obj instanceof EClassImpl) {
					EClassImpl node = (EClassImpl) obj;
					if (!((EClass)node).isAbstract()) {
						classes.add(node);
					}
				}
			}
		}
		
		for (EClassImpl cl : classes) {

			MenuItem classItem = new MenuItem(cl.getName());
			popupMenu.add(classItem);
			classItem.addActionListener(new ActionListener() {
	        	@Override
			    public void actionPerformed(ActionEvent e) {
	        		addNode(cl);
	        	}
	        });

		}
        graphComponent.add(popupMenu);
        popupMenu.show(graphComponent , x, y);
        
	}
	
	/*private void actionOnNodeSWT(int x, int y) {
		// open menu on node or on background
		Menu menu = new Menu(vis.getShell());
		
		MenuItem delete = new MenuItem(menu, SWT.PUSH); 
		delete.setText("Delete");
		MenuItem attr = new MenuItem(menu, SWT.PUSH);
		attr.setText("Show Attributes");
		menu.setLocation(x, y);
	}*/
	
	private void removeNode() {
		if(nodeInModel != null) {
			graph.getModel().remove(nodeInGraph);
			EMFManipulationUtils.delete(nodeInModel);
			//((mxCell) nodeInGraph).removeFromParent();
			//EcoreUtil.remove(nodeInModel); // delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
			System.out.println("removed from model");
			
			//vis.getGraph().repaint();
			/*
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
			graph.refresh();*/
		}
		
	}
	
	public void addEdge() {
		iterateModelMousePosition();
		if(edgeInGraph != null) {
			//add export com.mxgraph.view to emfViz? was heißt das?
			Object source = graph.getModel().getTerminal(edgeInGraph, true);
			Object target = graph.getModel().getTerminal(edgeInGraph, false);
			
			EList<EObject> objects = resource.getContents();
			for(EObject obj : objects) {
				iterateModelHierarchical(obj, source);
			}
			EObject src = nodeInModel;
			
			for(EObject obj : objects) {
				iterateModelHierarchical(obj, target);
			}
			EObject trg = nodeInModel;

			EList<EReference> edges = src.eClass().getEAllReferences();
			
			int count = 0;
			for (EReference eRef : edges) {
				if(eRef.getEType().getName().equals(trg.eClass().getName())) {
					System.out.println("edge type: " + eRef.getName());	
					eRefSelected = eRef;
					count++;
				}
				
			}
			String[] literals = new String[count];

			int i = 0;
			for (EReference eRef : edges) {
				if(eRef.getEType().getName().equals(trg.eClass().getName())) {
					literals[i] = eRef.getName();
					i++;
					
				}
			}
			if(literals.length > 1) {
				createEReferenceSelectionWindow(literals, src);
				EMFManipulationUtils.createEdge(src, trg, eRefSelected);
			}
			else if(literals.length == 1) {
				EMFManipulationUtils.createEdge(src, trg, eRefSelected); //schon im Modell oder noch hinzufügen?
			}
			//else no edge should be created
			callback.updateGraph(UpdateGraphType.ALL);
		}
		
	}
	
	/*not needed if only one edge type available*/
	private void createEReferenceSelectionWindow(String[] literals, EObject src) {
		Shell shellEdges = new Shell(display);
		
		shellEdges.setText("Select Edge Type");
		
		shellEdges.setLayout(new GridLayout());
		shellEdges.setBackground(shellEdges.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		Composite composite = new Composite(shellEdges, SWT.EMBEDDED);
		composite.setVisible(true);
		
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		//gridData1.horizontalSpan = 3;
		
		composite.setLayoutData(gridData1);
		composite.setLayout(new GridLayout(2, true));
		
		Label labelName = new Label(composite, SWT.None);
		labelName.setText("Select Edge Type:");

		Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
	    combo.setItems(literals);

		
		//control buttons / composite 
		Composite compositeCtrl = new Composite(shellEdges, SWT.EMBEDDED);
		compositeCtrl.setVisible(true);
		compositeCtrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeCtrl.setLayout(new RowLayout());
		
		Button nextBT = new Button(compositeCtrl, SWT.PUSH |SWT.RIGHT);
		nextBT.setText("Done");
		nextBT.setAlignment(SWT.CENTER);
		
		nextBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				
				System.out.println(combo.getText());
				EList<EReference> edges = src.eClass().getEAllReferences();
				for (EReference eRef : edges) {
					if(eRef.getName().equals(combo.getText())) {
						eRefSelected = eRef;
					}
					
				}
				shellEdges.close();
			}
		});
		
		
		//set size of shell
		shellEdges.setSize(600,200);	
		shellEdges.open();
	}
	
	public void addNode(EClass cl) {
		newObj = EcoreUtil.create(cl);
		resource.getContents().add(newObj);
		System.out.println("new obj created in model");
		/*Node newNode = new Node(newObj.toString(),newObj.eClass().getName(), "defaultNode");
		loader.nodes.add(newNode);
		System.out.println("added to list");
		graph.insertVertex(graph.getDefaultParent(),newNode.id, newNode.name,100,100,80,40);
		System.out.println("added in graph");*/
		callback.updateGraph(UpdateGraphType.ALL);
	}
	
	public void setAttributesExec() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				System.out.println("syncexec");
			    setAttributes();
			    callback.updateGraph(UpdateGraphType.ALL);
			}
			});
		
	}
	
	private void setAttributes() {
		
		iterateModelMousePosition();
		if(nodeInModel != null) {
			
			Shell shellAttr = new Shell(display);
			
			shellAttr.setText("Set Attributes");
					
			shellAttr.setLayout(new GridLayout());
			shellAttr.setBackground(shellAttr.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			
			Composite composite = new Composite(shellAttr, SWT.EMBEDDED);
			composite.setVisible(true);
			
			GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true);
			
			composite.setLayoutData(gridData1);
			composite.setLayout(new GridLayout(3, true));
			
			EList<EAttribute> attributes = nodeInModel.eClass().getEAllAttributes();

			for(EAttribute attr: attributes) {
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
			System.out.println(attr.getName() + " (" + attr.getEAttributeType().getInstanceTypeName() 
					+ ") = "+ txtMap.get(attr).getText());
			errorAttr = attr;
			EDataType type = attr.getEAttributeType();
			String input = txtMap.get(attr).getText();
			//Wird nicht gesetzt??
			nodeInModel.eSet(attr, createFromString(type,input));
		}
		for (EAttribute attr : enumMap.keySet()) {
			System.out.println(attr.getName() + " (" + attr.getEAttributeType().getInstanceTypeName() 
					+ ") = " + enumMap.get(attr).getText());
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

