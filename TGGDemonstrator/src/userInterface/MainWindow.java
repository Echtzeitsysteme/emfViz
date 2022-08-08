package userInterface;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Scrollbar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.ModelLoader_SYNC;
import tggDemonstrator.TGGDemonstrator;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import graphVisualization.Visualizer_TGGDemonstrator;

public class MainWindow {

	private Display display;
	private Shell shell;

	private TGGDemonstrator modelLoader;
	// private IbexOptions options;

	private int shellSizeX;
	private int shellSizeY;

	private Frame frameSrc;
	private Frame frameTrg;

	private Rectangle rectangleSrc;
	private Rectangle rectangleTrg;

	private GraphManipulator manipSrc;
	private GraphManipulator manipTrg;

	private InstanceDiagrammLoader dataSrc;
	private InstanceDiagrammLoader dataTrg;

	private Visualizer visSrc;
	private Visualizer visTrg;

	/*
	 * Constructor - needs a ModelLoader instance
	 */
	public MainWindow(TGGDemonstrator modelLoader) {

		this.modelLoader = modelLoader;

		// init display and shell
		InitUI();

		// open start window
		createResourcLoaderWindow();
	}

	/*
	 * Init display and shell
	 */
	private void InitUI() {
		/* init main window */
		display = new Display();
		shell = new Shell(display);

		shell.setText("TGG Demonstrator");

		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				System.out.println("close");
			}
		});
	}

	/*
	 * start the visualization and open the shell
	 */
	public void run() {
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	/*
	 * Removes all widgets from the shell. This method is called every time before a
	 * new window is opened.
	 */
	private void resetShell() {
		for (Control kid : shell.getChildren()) {
			kid.dispose();
		}
	}

	/*
	 * Return shell instance
	 */
	public Shell getShell() {
		return shell;
	}
	/*
	 * Return model loader instance
	 */
	/*
	 * public TGGDemonstrator getModelLoader() { return modelLoader; }
	 */

	/*
	 * This is the main window where source and target graph are visualized
	 */
	public void createMainWindow() {

		/*
		 * BASIC LAYOUT
		 * 
		 * shellSizeX _________________________________ | //area for buttons | | |
		 * shellSizeY * 0.1 |_______________________________| | | | | | | | | | |
		 * //graph | //graph | | //source | //target | shellSizeY * 0.9 | | | | | | | |
		 * | |_______________|_______________|
		 * 
		 * 
		 */

		// initialize shell layout
		shellSizeX = display.getClientArea().width;
		shellSizeY = display.getClientArea().height - 100;

		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		grid.makeColumnsEqualWidth = true;

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		shell.setLayout(grid);

		Composite comp = new Composite(shell, SWT.TOP);
		// comp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = (int) (shellSizeY * 0.1);
		gridData.horizontalSpan = 2;

		comp.setLayoutData(gridData);
		comp.setVisible(true);
		comp.setLayout(new GridLayout(4, true)); // number of columns = number of button groups

		Composite compSrc = new Composite(shell, SWT.BOTTOM | SWT.EMBEDDED);
		compSrc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GridData gridDataSrc = new GridData(GridData.FILL_HORIZONTAL);
		gridDataSrc.heightHint = (int) (shellSizeY * 0.9);
		// gridData.widthHint = shellSizeX;
		gridDataSrc.horizontalSpan = 1;

		compSrc.setLayoutData(gridDataSrc);

		Composite compTrg = new Composite(shell, SWT.BOTTOM | SWT.EMBEDDED);
		compTrg.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		GridData gridDataTrg = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTrg.heightHint = (int) (shellSizeY * 0.9);
		// gridData.widthHint = shellSizeX;
		gridDataTrg.horizontalSpan = 1;

		compTrg.setLayoutData(gridDataTrg);

		frameSrc = SWT_AWT.new_Frame(compSrc);

		// System.out.println("src1: " + frameSrc.getBounds());

		frameTrg = SWT_AWT.new_Frame(compTrg);


		initializeButtons(comp);

		shell.setSize(shellSizeX, shellSizeY);

		rectangleSrc = compSrc.getBounds();
		rectangleTrg = compTrg.getBounds();

		System.out.println("compTrg: " + compTrg.getBounds());
		System.out.println("compSrc: " + compSrc.getBounds());
	}

	/*
	 * Window to select between different model loading options. It is the initial
	 * window (start window)
	 */
	public void createResourcLoaderWindow() {

		shellSizeX = 600;
		shellSizeY = 180;

		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout());

		Group modelGroup = new Group(composite, SWT.None);
		modelGroup.setText("Select your model location:");
		modelGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		modelGroup.setLayout(new GridLayout());
		// modelGroup.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		Button defaultBT = new Button(modelGroup, SWT.RADIO);
		defaultBT.setText("Default");

		Button newModeltBT = new Button(modelGroup, SWT.RADIO);
		newModeltBT.setText("New Model");

		Button modelLocationBT = new Button(modelGroup, SWT.RADIO);
		modelLocationBT.setText("Select Model");

		Button nextBT = new Button(composite, SWT.PUSH);
		nextBT.setText("Next");
		nextBT.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		// generate new model is only for MODELGEN available
		if (modelLoader instanceof ModelLoader_INITIAL_BWD || modelLoader instanceof ModelLoader_INITIAL_FWD
				|| modelLoader instanceof ModelLoader_SYNC) {
			newModeltBT.setEnabled(false);
		}

		nextBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				for (Control bt : modelGroup.getChildren()) {
					if (((Button) bt).getSelection()) {
						modelLocationSelection(((Button) bt).getText());
						break;
					}
				}
			}
		});

		shell.setSize(shellSizeX, shellSizeY);
	}

	/*
	 * This window will open when option "Select Model" is chosen. Window to
	 * determine location of source and target model
	 */
	public void CreateDirectorySelectionWindow() {
		shellSizeX = 450;
		shellSizeY = 280;

		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);

		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		// gridData1.horizontalSpan = 3;

		composite.setLayoutData(gridData1);
		composite.setLayout(new GridLayout());

		Group srcGroup = new Group(composite, SWT.None);
		srcGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		srcGroup.setLayout(new GridLayout(2, false));

		Group trgGroup = new Group(composite, SWT.None);
		trgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		trgGroup.setLayout(new GridLayout(2, false));

		// create widgets for source location
		Label srcLabel = new Label(srcGroup, SWT.NONE);
		srcLabel.setText("Select your source xmi:");

		Button srcBT = new Button(srcGroup, SWT.PUSH);
		srcBT.setText("Source xmi");
		srcBT.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Text srcTxt = new Text(srcGroup, SWT.NONE);

		GridData gridDataSrc = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataSrc.widthHint = 300;

		srcTxt.setLayoutData(gridDataSrc);

		// create widgets for target location
		Label trgLabel = new Label(trgGroup, SWT.NONE);
		trgLabel.setText("Select your target xmi:");

		Button trgBT = new Button(trgGroup, SWT.PUSH);
		trgBT.setText("Target xmi");
		trgBT.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Text trgTxt = new Text(trgGroup, SWT.NONE);

		GridData gridDataTrg = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataTrg.widthHint = 300;

		trgTxt.setLayoutData(gridDataTrg);

		// buttons directory selection listener

		srcBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				srcTxt.setText(openDirectoryDialog());
			}
		});

		trgBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				trgTxt.setText(openDirectoryDialog());
			}
		});

		// control buttons / composite
		Composite compositeCtrl = new Composite(shell, SWT.EMBEDDED);
		compositeCtrl.setVisible(true);
		compositeCtrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeCtrl.setLayout(new RowLayout());

		Button nextBT = new Button(compositeCtrl, SWT.PUSH);
		nextBT.setText("Next");
		nextBT.setAlignment(SWT.CENTER);

		nextBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				loadModelFromPath(srcTxt.getText(), trgTxt.getText());
			}
		});

		// button to go back to initial window
		Button backBT = new Button(compositeCtrl, SWT.PUSH);
		backBT.setText("Back");
		backBT.setAlignment(SWT.CENTER);

		// go back to previous window
		backBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				resetShell();
				createResourcLoaderWindow();
			}
		});

		// set size of shell
		shell.setSize(shellSizeX, shellSizeY);

		/*
		 * if ModelLoader is initialized by Inital_BWD_App or Inital_FWD_App source or
		 * target model are generated from tgg.
		 */
		if (modelLoader instanceof ModelLoader_INITIAL_FWD) {
			trgBT.setEnabled(false);
			trgLabel.setEnabled(false);
			trgTxt.setEnabled(false);

		} else if (modelLoader instanceof ModelLoader_INITIAL_BWD) {
			srcBT.setEnabled(false);
			srcLabel.setEnabled(false);
			srcTxt.setEnabled(false);
		}
	}

	/*
	 * choose between different model loading options and execute selected choice
	 */
	private void modelLocationSelection(String text) {
		switch (text) {
		case "Default":
			System.out.println("selected option: " + text);
			resetShell();

			modelLoader.loadFromDefault();
			createMainWindow();
			initialiseRelevantInstances();

			break;

		case "New Model":
			System.out.println("selected option: " + text);

			resetShell();

			// generate a new model
			modelLoader.generateNewModel();
			createMainWindow();
			initialiseRelevantInstances();

			break;

		case "Select Model":
			System.out.println("selected option: " + text);
			resetShell();
			CreateDirectorySelectionWindow();
			break;
		}
	}

	/*
	 * start loading source and target model from selected path
	 */
	private void loadModelFromPath(String src, String trg) {

		modelLoader.createResourcesFromPath(src, trg);

		resetShell();
		createMainWindow();

		initialiseRelevantInstances();
	}

	/*
	 * open a directory dialog window
	 */
	public String openDirectoryDialog() {

		String selectedDir = "";

		FileDialog directoryDialog = new FileDialog(shell, SWT.OPEN);

		String filterExt[] = new String[1];
		filterExt[0] = ".xmi";

		directoryDialog.setFilterPath(selectedDir);
		directoryDialog.setFilterExtensions(filterExt);

		if (directoryDialog.open() != null) {

			String dir = directoryDialog.getFilterPath() + System.getProperty("file.separator")
					+ directoryDialog.getFileName();
			selectedDir = dir;

			return selectedDir;
		}

		return selectedDir;
	}

	public GraphManipulator getSrcManipulator() {
		return manipSrc;
	}

	public GraphManipulator getTrgManipulator() {
		return manipTrg;
	}

	private void initialiseRelevantInstances() {
		Resource srcRs = modelLoader.getSource();
		Resource trgRs = modelLoader.getTarget();

		dataSrc = new InstanceDiagrammLoader(srcRs, true);
		dataTrg = new InstanceDiagrammLoader(trgRs, true);

		visSrc = new Visualizer_TGGDemonstrator(dataSrc, frameSrc, rectangleSrc);
		visTrg = new Visualizer_TGGDemonstrator(dataTrg, frameTrg, rectangleTrg);

		visSrc.init();
		visTrg.init();

		// options = modelLoader.getOptions();

		GraphManipulator manipSrc = new GraphManipulator(visSrc, dataSrc.getInstanceModel(), dataSrc);
		this.manipSrc = manipSrc;
		GraphManipulator manipTrg = new GraphManipulator(visTrg, dataTrg.getInstanceModel(), dataTrg);
		this.manipTrg = manipTrg;
	}

	/*
	 * translating forward, only call-able for INITIAL_FWD
	 */
	private void forwardTranslation() {
		if (modelLoader instanceof ModelLoader_INITIAL_FWD) {

			System.out.println("Translating forward...");

			((ModelLoader_INITIAL_FWD) modelLoader).forward();

			Resource trgRs = modelLoader.getTarget();

			dataTrg.setInstanceModel(trgRs);

			visTrg.updateGraph();

		}
	}

	private void backwardTranslation() {
		if (modelLoader instanceof ModelLoader_INITIAL_BWD) {

			System.out.println("Translating backward...");

			((ModelLoader_INITIAL_BWD) modelLoader).backward();

			Resource srcRs = modelLoader.getTarget();

			dataSrc.setInstanceModel(srcRs);

			visSrc.updateGraph();

		}
	}

	private void initializeButtons(Composite comp) {
		// button to go back to inital window (start window)
		Group buttonGroupStd = new Group(comp, SWT.None);

		buttonGroupStd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttonGroupStd.setText("Standard Functionality");
		buttonGroupStd.setLayout(new GridLayout(3, true));
		Button backButton = new Button(buttonGroupStd, SWT.PUSH);
		backButton.setText("Back");

		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				resetShell();
				createResourcLoaderWindow();
			}
		});

		// buttons for manipulating the model
		Group buttonGroupManip = new Group(comp, SWT.None);

		buttonGroupManip.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttonGroupManip.setText("Manipulate Models");
		buttonGroupManip.setLayout(new GridLayout(3, false));

		Button deleteButton = new Button(buttonGroupManip, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData());

		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("delete selected");
				manipSrc.iterateModel();
				manipTrg.iterateModel();
				// modelLocationSelection("Default");
				// update graph by loading modified resource
				// updateVisualizer();
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
					manipSrc.createAttributeWindow(display);
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
					manipTrg.createAttributeWindow(display);
				}
			});

		}

		popupButton.setMenu(popupMenu);

		/* sync, initial_fwd and initial_bwd functionalities */
		Group buttonGroupSync = new Group(comp, SWT.None);

		buttonGroupSync.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttonGroupSync.setText("Synchronize Models");
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

		/* new Model functionalities */
		Group buttonGroupNM = new Group(comp, SWT.None);

		buttonGroupNM.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttonGroupNM.setText("New Model Functionality");
		buttonGroupNM.setLayout(new GridLayout(3, true));

		Button next = new Button(buttonGroupNM, SWT.PUSH);
		next.setText("Next Rule");

	}

}
