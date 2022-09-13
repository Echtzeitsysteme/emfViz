package visualisation;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emoflon.ibex.common.emf.EMFManipulationUtils;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;

import graphVisualization.Edge;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.ModelLoader_SYNC;
import tggDemonstrator.TGGDemonstrator;

/**
 * Implements all changes a user can make in a graph, like adding and deleting elements.
 *
 */
public class GraphManipulator {

	public enum GraphType {
		SRC, TRG
	}

	private final GraphType type;

	private CallbackHandler callback;
	private TGGDemonstrator modelLoader;
	private InstanceDiagrammLoader loader;
	private Display display;

	private Resource resource;
	private EObject nodeInModel;
	private EReference edgeInModel;
	private EObject srcNode;
	private EObject trgNode;
	private EObject newObj;
	private Map<EAttribute, Text> txtMap = new HashMap<EAttribute, Text>();
	private Map<EAttribute, Combo> enumMap = new HashMap<EAttribute, Combo>();
	private EAttribute errorAttr;
	private mxGraphComponent graphComponent;
	private mxCell cellAtPos;
	private mxGraph graph;

	private boolean preventRemovingEdge = false;

	/**
	 * Implements all changes a user can make in a graph, like adding and deleting elements.
	 */
	public GraphManipulator(TggVisualizer vis, Display display, InstanceDiagrammLoader loader,
			TGGDemonstrator modelLoader, GraphType type) {

		this.display = display;
		this.resource = loader.getInstanceModel();
		this.loader = loader;
		this.modelLoader = modelLoader;
		this.type = type;

		graph = vis.getGraph();
		graphComponent = vis.getGraphComponent();
		callback = CallbackHandler.getInstance();

	}
	
	/**
	 * Activates functionalities. Right click on background or element for interaction.
	 */
	public void initialize() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				//cell that was clicked
				cellAtPos = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
				
				//BUTTON3 = right button
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (cellAtPos != null) {
						if (cellAtPos.isVertex()) {
							//select delete or show attributes
							actionOnNode(e.getX(), e.getY());

						} else {
							if (modelLoader instanceof ModelLoader_INITIAL_FWD && type.equals(GraphType.SRC)
									|| modelLoader instanceof ModelLoader_INITIAL_BWD && type.equals(GraphType.TRG)
									|| modelLoader instanceof ModelLoader_SYNC) {
								//delete edge but only if allowed on the frame
								actionOnEdge(e.getX(), e.getY());
							}

						}
					} else {
						if (modelLoader instanceof ModelLoader_INITIAL_FWD && type.equals(GraphType.SRC)
								|| modelLoader instanceof ModelLoader_INITIAL_BWD && type.equals(GraphType.TRG)
								|| modelLoader instanceof ModelLoader_SYNC) {
							//add node but only if allowed on the frame
							actionOnFrame(e.getX(), e.getY());
							callback.setPositionForNewNode(e.getX(), e.getY());
						}
					}

				}

			}

		});
		
		/*check for newly inserted edges*/
		graph.getModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
			// System.out.println(evt.getName() + " ---- " + evt);
			var changes = evt.getProperty("changes");
			if (changes instanceof Iterable<?> it) {
				for (var change : it) {
					if (change instanceof mxChildChange childChange) {
						var child = childChange.getChild();
						if (child instanceof mxCell cell) {
							if (cell.isEdge()) {
								if (!(cell.getValue() instanceof Edge)) {
									//System.out.println("Detected new edge without value: " + cell);
									cellAtPos = cell;
									if(!preventRemovingEdge) {
										addEdge();
									}
									preventRemovingEdge = !preventRemovingEdge;

								} else {
									//System.out.println("Detected new edge: " + cell);
								}
							}
						}
					}
				}
			}
		});
	}

	/*find match of a cell between graph and resource*/
	private void findMatchInModel(mxCell comp) {
		if (comp.isVertex()) {
			for (Node node : loader.nodes) {
				if (String.valueOf(node.hashCode()).equals(comp.getId())) {

					nodeInModel = node.eobj;
				}
			}
		}
		if (comp.isEdge()) {
			edgeInModel = loader.edges.get(Integer.parseInt(comp.getId())).ref;
			srcNode = loader.edges.get(Integer.parseInt(comp.getId())).source;
			trgNode = loader.edges.get(Integer.parseInt(comp.getId())).target;

		}

	}
	
	/*select delete edge or show attributes*/
	private void actionOnNode(int x, int y) {
		final PopupMenu popupmenu = new PopupMenu("On Node");
		MenuItem attr = new MenuItem("Show Attributes");
		attr.setActionCommand("ATTR");

		popupmenu.add(attr);

		if (modelLoader instanceof ModelLoader_INITIAL_FWD && type.equals(GraphType.SRC)
				|| modelLoader instanceof ModelLoader_INITIAL_BWD && type.equals(GraphType.TRG)
				|| modelLoader instanceof ModelLoader_SYNC) {

			MenuItem delete = new MenuItem("Delete");
			delete.setActionCommand("DEL");
			popupmenu.add(delete);
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeNode();
				}
			});
		}

		graphComponent.add(popupmenu);
		popupmenu.show(graphComponent, x, y);

		attr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAttributesExec();
			}
		});

	}

	/*select delete edge*/
	private void actionOnEdge(int x, int y) {
		final PopupMenu popupmenu = new PopupMenu("On Edge");

		if (modelLoader instanceof ModelLoader_INITIAL_FWD && type.equals(GraphType.SRC)
				|| modelLoader instanceof ModelLoader_INITIAL_BWD && type.equals(GraphType.TRG)
				|| modelLoader instanceof ModelLoader_SYNC) {

			MenuItem delete = new MenuItem("Delete");
			delete.setActionCommand("DEL");
			popupmenu.add(delete);
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeEdge();

				}
			});
		}

		graphComponent.add(popupmenu);
		popupmenu.show(graphComponent, x, y);

	}
	
	/*select type and add new node*/
	private void actionOnFrame(int x, int y) {
		PopupMenu popupMenu = new PopupMenu("On Frame");

		List<EClassImpl> classes = new ArrayList<EClassImpl>();

		//find all possible classes of source or target model
		if (type.equals(GraphType.SRC)) {
			for (EObject obj : modelLoader.getOptions().tgg.tgg().getSrc().get(0).eContents()) {
				if (obj instanceof EClassImpl) {
					EClassImpl node = (EClassImpl) obj;
					if (!((EClass) node).isAbstract()) {
						classes.add(node);
					}
				}
			}
		} else {
			for (EObject obj : modelLoader.getOptions().tgg.tgg().getTrg().get(0).eContents()) {
				if (obj instanceof EClassImpl) {
					EClassImpl node = (EClassImpl) obj;
					if (!((EClass) node).isAbstract()) {
						classes.add(node);
					}
				}
			}
		}

		//show classes for selection
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
		popupMenu.show(graphComponent, x, y);

	}

	/*remove node in resource*/
	private void removeNode() {
		findMatchInModel(cellAtPos);
		if (nodeInModel != null) {
			EMFManipulationUtils.delete(nodeInModel, true);

			callback.updateGraph();
			callback.setLastProcessedGraph(getGraphTypeName());
		}

	}

	/*remove edge in resource*/
	private void removeEdge() {
		findMatchInModel(cellAtPos);
		if (edgeInModel != null) {
			EMFManipulationUtils.deleteEdge(srcNode, trgNode, edgeInModel);
			callback.updateGraph();
			callback.setLastProcessedGraph(getGraphTypeName());
		}

	}

	/*add edge to resource*/
	private void addEdge() {

		if (cellAtPos != null) {

			mxCell source = (mxCell) graph.getModel().getTerminal(cellAtPos, true);
			mxCell target = (mxCell) graph.getModel().getTerminal(cellAtPos, false);
			
			//remove edge in graph because it will be added with the visualization update 
			graph.getModel().remove(cellAtPos);

			findMatchInModel(source);
			EObject src = nodeInModel;

			findMatchInModel(target);
			EObject trg = nodeInModel;

			EList<EReference> edges = src.eClass().getEAllReferences();
			EList<EClass> superclasses = trg.eClass().getEAllSuperTypes();
			PopupMenu popupMenu = new PopupMenu("Edge type");

			//search for possible types of edges between the source and target node
			for (EReference eRef : edges) {

				// type of edge could belong to an abstract superclass
				for (EClass eClass : superclasses) {
					if (eClass.getName().equals(eRef.getEType().getName())) {
						MenuItem edgeItem = new MenuItem(eRef.getName());
						popupMenu.add(edgeItem);
						edgeItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								
								createSpecificEdge(src, trg, eRef);
							}
						});
					}
				}
				if (trg.eClass().getName().equals(eRef.getEType().getName())) {
					MenuItem edgeItem = new MenuItem(eRef.getName());
					popupMenu.add(edgeItem);
					edgeItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							
							createSpecificEdge(src, trg, eRef);
						}
					});
				}
			}
			graphComponent.add(popupMenu);
			mxPoint pos = graph.getView().getPoint(graph.getView().getState(source));
			popupMenu.show(graphComponent, (int) pos.getX(), (int) pos.getY());
			
			
		}

	}

	/*distinguish different cases of inserted edges*/
	private void createSpecificEdge(EObject src, EObject trg, EReference eRef) {
		if (eRef.getUpperBound() == 1 && src.eIsSet(eRef)) {
			// delete edge from src to old trg
			EMFManipulationUtils.deleteEdge(src, (EObject) src.eGet(eRef), eRef);
			EMFManipulationUtils.createEdge(src, trg, eRef);
		} else if (eRef.isContainment() && trg.eContainmentFeature() != null) {
			//delete edge from old source to target; NOT FUNCTIONAL YET
			EMFManipulationUtils.deleteEdge(trg.eContainmentFeature().eContainer(), trg, eRef);
			EMFManipulationUtils.createEdge(src, trg, eRef);
		} else {
			EMFManipulationUtils.createEdge(src, trg, eRef);
		}

		callback.updateGraph();
		callback.setLastProcessedGraph(getGraphTypeName());
	}
	
	/*add a node to the resource*/
	private void addNode(EClass cl) {
		newObj = EcoreUtil.create(cl);
		resource.getContents().add(newObj);
		callback.updateGraph();
		callback.setLastProcessedGraph(getGraphTypeName());
	}

	/*switch thread for showing attribute window*/
	private void setAttributesExec() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				setAttributes();
				callback.updateGraph();
				callback.setLastProcessedGraph(getGraphTypeName());
			}
		});

	}

	/*show current values of attributes and save changes to them*/
	private void setAttributes() {

		findMatchInModel(cellAtPos);
		if (nodeInModel != null) {

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

			for (EAttribute attr : attributes) {
				Label labelName = new Label(composite, SWT.None);
				labelName.setText(attr.getName());
				Label labelValue = new Label(composite, SWT.None);
				labelValue.setText(attr.getEAttributeType().getInstanceTypeName());

				//enums need a dropdown menu with possible values
				if (attr.getEType() instanceof EEnum) {
					EEnum eenum = (EEnum) attr.getEType();
					int len = eenum.getELiterals().size();
					String[] literals = new String[len];
					int i = 0;
					for (EEnumLiteral literal : eenum.getELiterals()) {
						literals[i] = literal.getName();
						i++;
					}

					Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
					combo.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
					combo.setItems(literals);
					combo.setText(nodeInModel.eGet(attr).toString());
					enumMap.put(attr, combo);
				} else {
					Text txt = new Text(composite, SWT.BORDER | SWT.TRAIL);
					txt.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
					if (nodeInModel.eGet(attr) != null)
						txt.setText(nodeInModel.eGet(attr).toString());
					txtMap.put(attr, txt);
				}

			}

			//create button for closing the window and saving the attributes
			Composite compositeCtrl = new Composite(shellAttr, SWT.EMBEDDED);
			compositeCtrl.setVisible(true);
			compositeCtrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compositeCtrl.setLayout(new RowLayout());

			Button nextBT = new Button(compositeCtrl, SWT.PUSH | SWT.RIGHT);
			nextBT.setText("Done");
			nextBT.setAlignment(SWT.CENTER);

			//changes are set in resource when button "Done" is clicked
			nextBT.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent pSelectionEvent) {

					try {
						setAttributesInModel();
						shellAttr.close();
					} catch (Exception e) {
						//error occurs when type of input is incorrect for the attribute
						//text box turns red
						for (EAttribute attr : txtMap.keySet()) {
							if (attr.equals(errorAttr)) {
								txtMap.get(attr).setBackground(display.getSystemColor(SWT.COLOR_RED));
							} else {
								txtMap.get(attr).setBackground(display.getSystemColor(SWT.COLOR_WHITE));
							}
						}

					}

				}
			});

			shellAttr.setSize(800, 400);
			shellAttr.open();
		}

	}

	/*set new values of attributes in the resource*/
	private void setAttributesInModel() throws Exception {
		for (EAttribute attr : txtMap.keySet()) {
			errorAttr = attr;
			EDataType type = attr.getEAttributeType();
			String input = txtMap.get(attr).getText();
			nodeInModel.eSet(attr, createFromString(type, input));
		}
		for (EAttribute attr : enumMap.keySet()) {
			EDataType type = attr.getEAttributeType();
			String input = enumMap.get(attr).getText();
			nodeInModel.eSet(attr, createFromString(type, input));
		}
	}

	/*converts user input in instance of the given EDataType*/
	private Object createFromString(EDataType eDataType, String literal) throws Exception {
		return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, literal);
	}

	/**
	 * 
	 * @return the type of resource the graph visualizes (source or target)
	 */
	public GraphType getGraphType() {
		return type;
	}

	/**
	 * 
	 * @return the name of the graph type (SRC or TRG)
	 */
	public String getGraphTypeName() {
		return type.name();
	}
}
