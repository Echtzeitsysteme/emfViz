package visualisation;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
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
	
	
	
	public TggVisualizerDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		//super(modelLoader);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
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
		gridData.heightHint = (int)(shellSizeY * 0.1);
		gridData.horizontalSpan = 2;
		
		comp.setLayoutData(gridData);
		comp.setVisible(true);
		comp.setLayout(new GridLayout(4,true));
		
		Composite compSrc = new Composite(shell, SWT.BOTTOM | SWT.EMBEDDED);
		compSrc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		GridData gridDataSrc = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		//gridDataSrc.heightHint = (int)(shellSizeY * 0.9);
		gridDataSrc.horizontalSpan = 1;
		gridDataSrc.verticalAlignment = SWT.FILL;


		compSrc.setLayoutData(gridDataSrc);
		
		
		
		Composite compTrg = new Composite(shell, SWT.BOTTOM |  SWT.EMBEDDED);
		compTrg.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		GridData gridDataTrg = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		//gridDataTrg.heightHint = (int)(shellSizeY * 0.9);
		gridDataTrg.horizontalSpan = 1;
		gridDataTrg.verticalAlignment = SWT.FILL;
		
		compTrg.setLayoutData(gridDataTrg);
		
		
		
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
		
		buttonGroupStandard.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
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
		
		// buttons for manipulating the model
		Group buttonGroupManip = new Group(comp, SWT.None);

		buttonGroupManip.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
		
		Button attrButton = new Button(buttonGroupManip, SWT.PUSH);
		attrButton.setText("Attributes");
		attrButton.setLayoutData(new GridData());

		attrButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				manipSrc.setAttributes(display);
				manipTrg.setAttributes(display);
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
		
		/*sync, initial_fwd and initial_bwd functionalities*/
		Group buttonGroupSync = new Group(comp, SWT.None);
		
		buttonGroupSync.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
		buttonGroupSync.setText("Sync Functionalities");
		buttonGroupSync.setLayout(new GridLayout(3, true));
		
		Button syncForward = new Button(buttonGroupSync, SWT.PUSH);
		syncForward.setText("Sync Forward");
		syncForward.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent evt) {
				System.out.println("Start forward translating");
				forwardTranslation();
			}
		});
		
		Button syncBackward = new Button(buttonGroupSync, SWT.PUSH);
		syncBackward.setText("Sync Backward");
		syncBackward.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent evt) {
				System.out.println("Start backward translating");
				backwardTranslation();
			}
		});
		
		/*new Model functionalities*/
		Group buttonGroupNM = new Group(comp, SWT.None);
		
		buttonGroupNM.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
		buttonGroupNM.setText("New Model Functionalities");
		buttonGroupNM.setLayout(new GridLayout(3, true));
		
		/*Create a button to execute the next rule*/
		Button nextRule = new Button(buttonGroupNM, SWT.PUSH);
		nextRule.setText("Next Rule");
		
		/*Dropdown menu to select the next rule*/
		Combo combo = new Combo(buttonGroupNM, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		GridData comboGridData = new GridData(GridData.FILL_HORIZONTAL);
		comboGridData.horizontalSpan = 2;
		
		combo.setLayoutData(comboGridData);
		
		if (modelLoader instanceof ModelLoader_MODELGEN && modelLoader.getLoadingOption() == modelLoader.getLoadingOption().NewModel) {
			nextRule.setEnabled(true);
			
			combo.setItems(((ModelLoader_MODELGEN)modelLoader).getRuleNames());
			combo.setEnabled(true);
			combo.select(0);
		}else {
			nextRule.setEnabled(false);
			
			combo.setItems(new String[]{ });
			combo.setEnabled(false);
		}
		
		
		nextRule.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent evt) {
				
				if (modelLoader instanceof ModelLoader_MODELGEN) {
				
					System.out.println("Button Next Rule is clicked...");
					
					((ModelLoader_MODELGEN)modelLoader).setSelectedRuleIndex(combo.getSelectionIndex());
					
					
					((ModelLoader_MODELGEN)modelLoader).wakeUpThread();	
					
					//update Graph
					Resource trgRs = modelLoader.getResourceHandler().getTargetResource();	
					Resource srcRs = modelLoader.getResourceHandler().getSourceResource();
					
					dataTrg.setInstanceModel(trgRs);
					dataSrc.setInstanceModel(srcRs);
					
//					visTrg.updateGraph();
//					visSrc.updateGraph();
					
					frameSrc.revalidate();
					frameSrc.repaint();
					
					frameTrg.revalidate();
					frameTrg.repaint();
					
					combo.setItems(((ModelLoader_MODELGEN)modelLoader).getRuleNames());
					combo.select(0);
				}else {
					//do nothing
				}
			}
		});
	}
	
	
	/*
	 * translating forward, only call-able for INITIAL_FWD
	 */
	private void forwardTranslation() {
		if (modelLoader instanceof ModelLoader_INITIAL_FWD){
			
			System.out.println("Translating forward...");
			
			((ModelLoader_INITIAL_FWD) modelLoader).forward();
			
			Resource trgRs = modelLoader.getTarget();			
			
			dataTrg.setInstanceModel(trgRs);
			
//			visTrg.updateGraph();
					
		}
	}
	
	private void backwardTranslation() {
		if (modelLoader instanceof ModelLoader_INITIAL_BWD){
			
			System.out.println("Translating backward...");
			
			((ModelLoader_INITIAL_BWD) modelLoader).backward();
			
			Resource srcRs = modelLoader.getTarget();			
			
			dataSrc.setInstanceModel(srcRs);
			
//			visSrc.updateGraph();
					
		}
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
