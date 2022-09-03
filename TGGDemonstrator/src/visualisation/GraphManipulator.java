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
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;

import graphVisualization.Edge;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;
import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.ModelLoader_MODELGEN;
import tggDemonstrator.ModelLoader_SYNC;
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
	private EReference edgeInModel;
	private EObject srcNode;
	private EObject trgNode;
	private mxGraph graph;
	private Map<EAttribute,Text> txtMap = new HashMap<EAttribute, Text>();
	private Map<EAttribute,Combo> enumMap = new HashMap<EAttribute, Combo>();
	private EObject newObj;
	private EAttribute errorAttr;
	private mxGraphComponent graphComponent;
	private mxCell cellAtPos;

	public GraphManipulator(TggVisualizer vis, Display display, InstanceDiagrammLoader loader, TGGDemonstrator modelLoader, boolean isSource) {

		this.vis = vis;
		this.display = display; //aus vis?
		this.resource = loader.getInstanceModel();
		this.loader = loader;
		this.modelLoader = modelLoader; //aus vis?
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
							if(modelLoader instanceof ModelLoader_INITIAL_FWD && isSource ||
									modelLoader instanceof ModelLoader_INITIAL_BWD && !isSource ||
									modelLoader instanceof ModelLoader_SYNC) {
								
								actionOnEdge(e.getX(), e.getY());
							}
							
						}
					}
					else {
						if(modelLoader instanceof ModelLoader_INITIAL_FWD && isSource ||
								modelLoader instanceof ModelLoader_INITIAL_BWD && !isSource ||
								modelLoader instanceof ModelLoader_SYNC) {
							
							actionOnFrame(e.getX(), e.getY());
							callback.setPositionForNewNode(e.getX(), e.getY());
						}
					}
					
					
				}
				
				
			}

			
		});
		/*graph.addListener("", (sender, evt) -> System.out.println(sender + " ---- " + evt));
		graph.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);				
			}
		});
		
		graph.getView().addListener("", (sender, evt) -> System.out.println(sender + " ---- " + evt));*/
		
		graph.getModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
			System.out.println(evt.getName() + " ---- " + evt);
			var changes = evt.getProperty("changes");
			if(changes instanceof Iterable<?> it) {
				for(var change : it) {
					if(change instanceof mxChildChange childChange) {
						var child = childChange.getChild();
						if(child instanceof mxCell cell) {
							if(cell.isEdge()) {
								if(!(cell.getValue() instanceof Edge)) {
									System.out.println("Detected new edge without value: " + cell);
									cellAtPos = cell;
									addEdge();
									
								}
								else {
									System.out.println("Detected new edge: " + cell);
								}
							}
						}
					}
				}				
			}
		});
	}

	private void findMatchInModel(mxCell comp) {
		if(comp.isVertex()) {
			for (Node node : loader.nodes) {
				if(String.valueOf(node.hashCode()).equals(comp.getId())) {
					System.out.println("**node in map found");
					nodeInModel = node.eobj;
				}
			}
		}
		if(comp.isEdge()) {
			edgeInModel = loader.edges.get(Integer.parseInt(comp.getId())).ref;
			srcNode = loader.edges.get(Integer.parseInt(comp.getId())).source;
			trgNode = loader.edges.get(Integer.parseInt(comp.getId())).target;
			System.out.println("**edge in model found");
		}
		
	}
	

	private void actionOnNode(int x, int y) {
		// open menu on node or on background
		final PopupMenu popupmenu = new PopupMenu("On Node");    
		MenuItem attr = new MenuItem("Show Attributes"); 
        attr.setActionCommand("ATTR");
          
        popupmenu.add(attr);
        
        if(modelLoader instanceof ModelLoader_INITIAL_FWD && isSource ||
        		modelLoader instanceof ModelLoader_INITIAL_BWD && !isSource ||
        		modelLoader instanceof ModelLoader_SYNC) {
        	
        	MenuItem delete = new MenuItem("Delete");
        	delete.setActionCommand("DEL");
        	popupmenu.add(delete);
        	delete.addActionListener(new ActionListener() {
            	@Override
    		    public void actionPerformed(ActionEvent e) {
    				removeNode();
    				//x und y weitergeben an automatische Visualisierung
            		//System.out.println("Delete clicked");
            	}
            });
        }
       
        graphComponent.add(popupmenu);
        popupmenu.show(graphComponent , x, y);
 
        attr.addActionListener(new ActionListener() {
        	@Override
		    public void actionPerformed(ActionEvent e) {
				setAttributesExec();
        		//System.out.println("Attributes clicked");
        	}
        });
        
	}
	private void actionOnEdge(int x, int y) {
		// open menu on node or on background
		final PopupMenu popupmenu = new PopupMenu("On Edge");   
        
        if(modelLoader instanceof ModelLoader_INITIAL_FWD && isSource ||
        		modelLoader instanceof ModelLoader_INITIAL_BWD && !isSource ||
        		modelLoader instanceof ModelLoader_SYNC) {
        	
        	MenuItem delete = new MenuItem("Delete");
        	delete.setActionCommand("DEL");
        	popupmenu.add(delete);
        	delete.addActionListener(new ActionListener() {
            	@Override
    		    public void actionPerformed(ActionEvent e) {
    				removeEdge();
    				//x und y weitergeben an automatische Visualisierung
            		System.out.println("Delete clicked");
            	}
            });
        }
       
        graphComponent.add(popupmenu);
        popupmenu.show(graphComponent , x, y);
        
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
	
	
	private void removeNode() {
		findMatchInModel(cellAtPos);
		if(nodeInModel != null) {
			//graph.getModel().remove(nodeInGraph);
			EMFManipulationUtils.delete(nodeInModel);
			//((mxCell) nodeInGraph).removeFromParent();
			System.out.println("removed from model");

			callback.updateGraph(UpdateGraphType.ALL);
		}
		
		
	}
	
	private void removeEdge() {
		findMatchInModel(cellAtPos);
		if(edgeInModel != null) {
			EMFManipulationUtils.deleteEdge(srcNode, trgNode, edgeInModel);
			callback.updateGraph(UpdateGraphType.ALL);
		}
		
	}
	
	private void addEdge() {
		
		if(cellAtPos != null) {
			//add export com.mxgraph.view to emfViz? was heißt das?
			mxCell source = (mxCell) graph.getModel().getTerminal(cellAtPos, true);
			mxCell target = (mxCell) graph.getModel().getTerminal(cellAtPos, false);
			
			findMatchInModel(source);
			EObject src = nodeInModel;
			
			findMatchInModel(target);
			EObject trg = nodeInModel;

			EList<EReference> edges = src.eClass().getEAllReferences();
			
			PopupMenu popupMenu = new PopupMenu("Edge type");
			
			for (EReference eRef : edges) {

				MenuItem edgeItem = new MenuItem(eRef.getEType().getName());
				popupMenu.add(edgeItem);
				edgeItem.addActionListener(new ActionListener() {
		        	@Override
				    public void actionPerformed(ActionEvent e) {
		        		EMFManipulationUtils.createEdge(src, trg, eRef);
		        	}
		        });

			}
	        graphComponent.add(popupMenu);
	        mxPoint pos = graph.getView().getPoint(graph.getView().getState(cellAtPos));
	        popupMenu.show(graphComponent , (int)pos.getX(), (int)pos.getY());
	        
	        graph.getModel().remove(cellAtPos);
			callback.updateGraph(UpdateGraphType.ALL);
		}
		
	}
	
	/*not needed if only one edge type available*/
	/*private void createEReferenceSelectionWindow(String[] literals, EObject src) {
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
	}*/
	
	private void addNode(EClass cl) {
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
	
	private void setAttributesExec() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				System.out.println("syncexec");
			    setAttributes();
			    callback.updateGraph(UpdateGraphType.ALL);
			}
			});
		
	}
	
	private void setAttributes() {
		
		findMatchInModel(cellAtPos);
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

