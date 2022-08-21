package visualisation;

import java.awt.Frame;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;

import org.eclipse.swt.events.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import tggDemonstrator.ModelLoader_MODELGEN;
import tggDemonstrator.TGGDemonstrator;

public class TggVisualizerDisplay {

	private DisplayHandler handler;
	private TGGDemonstrator modelLoader;
	private Display display;
	private Shell shell;
	
	private Frame frameSrc;
	private Frame frameTrg;
	
	private Rectangle rectangleSrc;
	private Rectangle rectangleTrg;
	
	private int shellSizeX;
	private int shellSizeY;
	
	private GraphManipulator manipSrc;
	private GraphManipulator manipTrg;
	
	private InstanceDiagrammLoader dataSrc;
	private InstanceDiagrammLoader dataTrg;
	
	private Visualizer visSrc;
	private Visualizer visTrg;
	
	private CallbackHandler callbackHandler;
	
	
	
	public TggVisualizerDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		//super(modelLoader);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
		callbackHandler = CallbackHandler.getInstance();
		
		createTggVisualizerDisplay();
	}
	
	/*
	 * This is the main window where source and target graph are visualized
	 */
	private void createTggVisualizerDisplay() {
		
		/* BASIC LAYOUT
		 * 
		 * 				shellSizeX
		 * 	_________________________________
		 *	|		//area for buttons		|	
		 * 	|								|	shellSizeY * 0.1
		 * 	|_______________________________|
		 * 	|				|				|
		 * 	|				|				|
		 * 	|				|				|	
		 * 	|	//graph		|	//graph		|	
		 * 	|	//source	|	//target	|	shellSizeY * 0.9
		 * 	|				|				|
		 * 	|				|				|
		 * 	|				|				|
		 * 	|_______________|_______________|
		 * 
		 * 
		 */
		
		//initialize shell layout		
		shellSizeX = 1280; //display.getClientArea().width;
		shellSizeY = 720; //display.getClientArea().height;
		
		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		grid.makeColumnsEqualWidth = true;
		
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		
		shell.setLayout(grid);
		
		Composite comp = new Composite(shell, SWT.TOP);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		//gridData.heightHint = (int)(shellSizeY * 0.1);
		gridData.horizontalSpan = 2;
		
		comp.setLayoutData(gridData);
		comp.setVisible(true);
		comp.setLayout(new GridLayout(3,true));
		
		Composite compSrc = new Composite(shell, SWT.BOTTOM | SWT.EMBEDDED);
		compSrc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		GridData gridDataSrc = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridDataSrc.horizontalSpan = 1;
		gridDataSrc.verticalAlignment = SWT.FILL;


		compSrc.setLayoutData(gridDataSrc);
		
		compSrc.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				System.out.println(event);
			}
			
		});
		
		/*compSrc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				System.out.println(e);
			}
		});*/

		Composite compTrg = new Composite(shell, SWT.BOTTOM |  SWT.EMBEDDED);
		compTrg.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		GridData gridDataTrg = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridDataTrg.horizontalSpan = 1;
		gridDataTrg.verticalAlignment = SWT.FILL;
		
		compTrg.setLayoutData(gridDataTrg);
		
		/*compTrg.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				System.out.println(event);
			}
			
		});*/
		
		
		
		
		
		frameSrc = SWT_AWT.new_Frame(compSrc);
		
		
		frameTrg = SWT_AWT.new_Frame(compTrg);

		
		
		
		//initializeButtons(comp);
		initButtons(comp);

		shell.setSize(shellSizeX, shellSizeY);
		
		rectangleSrc = compSrc.getBounds();
		rectangleTrg = compTrg.getBounds();
		
		/*Only for debugging*/
		System.out.println("compTrg: " + compTrg.getBounds());
        System.out.println("compSrc: " + compSrc.getBounds());
	}
	
	/*initialize all buttons for the main display*/
	private void initButtons(Composite comp) {
		
		Group buttonGroupStandard = new Group(comp, SWT.None);
		
		buttonGroupStandard.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,false));
		buttonGroupStandard.setText("Standard Functionalities");
		buttonGroupStandard.setLayout(new GridLayout(3, true));
		
		//button to go back to start window
		Button backButton = new Button(buttonGroupStandard, SWT.PUSH);
		backButton.setText("Back");
		
		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent evt) {
				handler.openTggLoadModelDisplay();
			}
		});
		
		// TODO: add an reset button?
		
		/* buttons to create a complete new model or modify an existing model*/
		/* buttons are only activated for fwd, bwd or sync strategy*/
		
		/*//DEPRECATED///////////////////////////////////////////////////////////
		Group buttonGroupManip = new Group(comp, SWT.None);

		buttonGroupManip.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonGroupManip.setText("Manipulate Model");
		buttonGroupManip.setLayout(new GridLayout(4, false));

		Button deleteButton = new Button(buttonGroupManip, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData());

		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("delete selected");
				manipSrc.deleteSelected();
				manipTrg.deleteSelected();
				// modelLocationSelection("Default");
				// update graph by loading modified resource
				// updateVisualizer();
			}
		});
		
		Button edgeButton = new Button(buttonGroupManip, SWT.PUSH);
		edgeButton.setText("Test Edge");
		edgeButton.setLayoutData(new GridData());

		edgeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("testing edge features");
				manipSrc.addEdge();
				manipTrg.addEdge();
				// modelLocationSelection("Default");
				// update graph by loading modified resource
				// updateVisualizer();
			}
		});
		
		Button attrButton = new Button(buttonGroupManip, SWT.PUSH);
		attrButton.setText("Attributes");
		attrButton.setLayoutData(new GridData());

		attrButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				manipSrc.setAttributes();
				manipTrg.setAttributes();
			}
		});

		Button popupButton = new Button(buttonGroupManip, SWT.PUSH);
		popupButton.setText("New Node");

		Menu popupMenu = new Menu(popupButton);
		MenuItem srcItem = new MenuItem(popupMenu, SWT.CASCADE);
		srcItem.setText("Source");

		Menu srcMenu = new Menu(popupMenu);
		srcItem.setMenu(srcMenu);

		List<EClassImpl> srcClasses = new ArrayList<EClassImpl>();
		// modelLoader.getOptions().tgg.tgg().getSrc().get(0).eContents();
		// ibxopt.tgg.tgg().getSrc().get(0).eContents(); //sind die Klassen da drin?
		for (EObject obj : modelLoader.getOptions().tgg.tgg().getSrc().get(0).eContents()) {
			if (obj instanceof EClassImpl) {
				EClassImpl node = (EClassImpl) obj;
				if (!((EClass)node).isAbstract()) { // isabstract führt nicht zu Ausschluss von Staff??
					srcClasses.add(node);
				}
				

			}
		}
		for (EClassImpl cl : srcClasses) {

			MenuItem classItem = new MenuItem(srcMenu, SWT.NONE);
			classItem.setText(cl.getName());
			classItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					System.out.println(cl.getName());
					manipSrc.addNode(cl);
				}
			});

		}

		MenuItem trgItem = new MenuItem(popupMenu, SWT.CASCADE);
		trgItem.setText("Target");

		Menu trgMenu = new Menu(popupMenu);
		trgItem.setMenu(trgMenu);
		List<EClassImpl> trgClasses = new ArrayList<EClassImpl>();
		// modelLoader.getOptions().tgg.tgg().getTrg().get(0).eContents();
		// ibxopt.tgg.tgg().getSrc().get(0).eContents(); //sind die Klassen da drin?
		for (EObject obj : modelLoader.getOptions().tgg.tgg().getTrg().get(0).eContents()) {
			if (obj instanceof EClassImpl) {
				EClassImpl node = (EClassImpl) obj;
				trgClasses.add(node);
			}
		}
		for (EClassImpl cl : trgClasses) {

			MenuItem classItem = new MenuItem(trgMenu, SWT.NONE);
			classItem.setText(cl.getName());

			classItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					System.out.println(cl.getName());
					manipTrg.addNode(cl);
				}
			});

		}

		popupButton.setMenu(popupMenu);
		/////////////////////////////////////////////////////////////////////////////////////*/
		
		/*model generation functionalities (depending on strategy)*/
		Group buttonGroupModelGeneration = new Group(comp, SWT.None);
		
		buttonGroupModelGeneration.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
		buttonGroupModelGeneration.setText("Model Generation");
		buttonGroupModelGeneration.setLayout(new GridLayout(3, true));
		
		Button translateButton = new Button(buttonGroupModelGeneration, SWT.PUSH);
		translateButton.setText(modelLoader.buttonTranslateTxt());
		
		
		Combo combo = modelLoader.createComboBox(buttonGroupModelGeneration);
		
		if (combo != null) {
			GridData comboGridData = new GridData(GridData.FILL_HORIZONTAL);
			comboGridData.horizontalSpan = 2;
			
			combo.setLayoutData(comboGridData);
			
			callbackHandler.registerComboBox(combo);
			
			combo.select(0);
			
			combo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					System.out.println(" - Default Selection" + e.widget);
				}
			});
		}
		
		translateButton.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent evt) {
				System.out.println("translate button is pressed!");
				
				if (modelLoader instanceof ModelLoader_MODELGEN && combo != null) {
					callbackHandler.setSelectedMatch(combo.getSelectionIndex());
				}
				
				modelLoader.buttonTranslateFunction();
			}
		});
		
		//disable button they are not needed for this strategy
		/*if (modelLoader instanceof ModelLoader_MODELGEN) {
			popupButton.setEnabled(false);
			deleteButton.setEnabled(false);
			attrButton.setEnabled(false);
		}*/
	}
	
	
	/*------------------------Setter & Getter Methods-------------------------*/
	
	public Frame getSrcFrame() {
		return frameSrc;
	}
	
	public Frame getTrgFrame() {
		return frameTrg;
	}
	
	public Rectangle getSrcRectangle() {
		return rectangleSrc;
	}
	
	public Rectangle getTrgRectangle() {
		return rectangleTrg;
	}
	
	public void setGraphManipulatorSrc ( GraphManipulator manipSrc) {
		this.manipSrc = manipSrc;
	}
	
	public void setGraphManipulatorTrg ( GraphManipulator manipTrg) {
		this.manipTrg = manipTrg;
	}
	
	public void setTggSrcVisualizer(Visualizer visSrc) {
		this.visSrc = visSrc;
	}
	
	public void setTggTrgVisualizer(Visualizer visTrg) {
		this.visTrg = visTrg;
	}
	
	public void setSrcInstanceDiagrammLoader(InstanceDiagrammLoader dataSrc) {
		this.dataSrc = dataSrc;
	}
	
	public void setTrgInstanceDiagrammLoader(InstanceDiagrammLoader dataTrg) {
		this.dataTrg = dataTrg;
	}
}

