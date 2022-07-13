package userInterface;





import java.awt.Frame;

import java.io.IOException;

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

import Main.ModelLoader;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;




public class MainWindow {

	
	private Display display;
	private Shell shell;
	
	private ModelLoader modelLoader;
	
	private int shellSizeX;
	private int shellSizeY;
	
	private Frame frameSrc;
	private Frame frameTrg;
	
	private Rectangle rectangleSrc;
	private Rectangle rectangleTrg;
	
	public MainWindow (ModelLoader modelLoader) {
		
		
		this.modelLoader = modelLoader;
		
		//init display and shell
		InitUI();
		
		//open first window
		createResourcLoaderWindow();	
		//createMainWindow();
	}
	
	
	private void InitUI() {
		/* init main window */
		display = new Display();
		shell = new Shell(display);
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				System.out.println("close");
		    }
		});
		
	}
	
	public void run() {
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();

	}

	public void exit() {
		shell.dispose();
	}
	
	private void resetShell() {
		for (Control kid : shell.getChildren()) {
	          kid.dispose();
	    }
	}
	
	public Shell getShell() {
		return shell;
	}
	
	public ModelLoader getModelLoader() {
		return modelLoader;
	}
	
	public void createMainWindow() {
		
		/* BASIC LAYOUT
		 * 
		 * 				shellSizeX
		 * 	_________________________________
		 *	|		//area for buttons		|	
		 * 	|								|	ShellSizeY * 0.1
		 * 	|_______________________________|
		 * 	|				|				|
		 * 	|				|				|
		 * 	|				|				|	
		 * 	|	//graph		|	//graph		|	
		 * 	|	//sourc		|	//target	|	ShellSizeY * 0.9
		 * 	|				|				|
		 * 	|				|				|
		 * 	|				|				|
		 * 	|_______________|_______________|
		 * 
		 * 
		 */
		
		//initialize shell layout		
		//shellSizeX = shell.getDisplay().getClientArea().width;
		//shellSizeY = shell.getDisplay().getClientArea().height;
		shellSizeX = display.getClientArea().width;
		shellSizeY = display.getClientArea().height;
		
		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		grid.makeColumnsEqualWidth = true;
		
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		
		shell.setLayout(grid);
		
		Composite comp = new Composite(shell, SWT.TOP);
		//comp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = (int)(shellSizeY * 0.1);
		gridData.horizontalSpan = 2;
		
		comp.setLayoutData(gridData);
		
		Composite compSrc = new Composite(shell, SWT.BOTTOM | SWT.EMBEDDED);
		compSrc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		GridData gridDataSrc = new GridData(GridData.FILL_HORIZONTAL);
		gridDataSrc.heightHint = (int)(shellSizeY * 0.9);
		//gridData.widthHint = shellSizeX;
		gridDataSrc.horizontalSpan = 1;
		
		compSrc.setLayoutData(gridDataSrc);
		
		Composite compTrg = new Composite(shell, SWT.BOTTOM |  SWT.EMBEDDED);
		compTrg.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		GridData gridDataTrg = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTrg.heightHint = (int)(shellSizeY * 0.9);
		//gridData.widthHint = shellSizeX;
		gridDataTrg.horizontalSpan = 1;
		
		compTrg.setLayoutData(gridDataTrg);
		
		frameSrc = SWT_AWT.new_Frame(compSrc);
		
		
		//System.out.println("src1: " + frameSrc.getBounds());
		
		frameTrg = SWT_AWT.new_Frame(compTrg);
		
		
		shell.setSize(shellSizeX, shellSizeY);
		
		rectangleSrc = compSrc.getBounds();
		rectangleTrg = compTrg.getBounds();
		
	
        System.out.println("compTrg: " + compTrg.getBounds());
        System.out.println("compSrc: " + compSrc.getBounds());
	}
	
	public void createResourcLoaderWindow () {
		
		shellSizeX = 600;
		shellSizeY = 180;
		
		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
		composite.setLayout(new GridLayout());
		
		
		Group modelGroup = new Group(composite, SWT.None);
		modelGroup.setText("Select your model location:");
		modelGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		modelGroup.setLayout(new GridLayout());
		//modelGroup.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		Button defaultBT = new Button(modelGroup, SWT.RADIO);
		defaultBT.setText("Default");
		
		Button newModeltBT = new Button(modelGroup, SWT.RADIO);
		newModeltBT.setText("New Model");
		
		Button modelLocationBT = new Button(modelGroup, SWT.RADIO);
		modelLocationBT.setText("Select Model");
		
		Button nextBT = new Button(composite, SWT.PUSH);
		nextBT.setText("Next");
		nextBT.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		

		nextBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				for (Control bt : modelGroup.getChildren()) {
					if (((Button)bt).getSelection()) {
						modelLocationSelection(((Button)bt).getText());
						break;
					}
				}
			}
		});
		
		shell.setSize(shellSizeX, shellSizeY);
	}
	
	
	public void CreateDirectorySelectionWindow() {
		shellSizeX = 450;
		shellSizeY = 280;
		
		
		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);
		
		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		//gridData1.horizontalSpan = 3;
		
		composite.setLayoutData(gridData1);
		composite.setLayout(new GridLayout());
		
		
		
		Group srcGroup = new Group(composite, SWT.None);
		srcGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		srcGroup.setLayout(new GridLayout(2, false));
		
		Group trgGroup = new Group(composite, SWT.None);
		trgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		trgGroup.setLayout(new GridLayout(2, false));
		
		//create widgets for source location
		Label srcLabel = new Label(srcGroup, SWT.NONE);
		srcLabel.setText("Select your source xmi:");
		
		Button srcBT = new Button(srcGroup, SWT.PUSH);
		srcBT.setText("Source xmi");
		srcBT.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text srcTxt = new Text(srcGroup, SWT.NONE);
		
		GridData gridDataSrc = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataSrc.widthHint = 300;
				
		srcTxt.setLayoutData(gridDataSrc);
		
		//create widgets for target location		
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
		
		
		
		//control buttons / composite 
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
		
		Button backBT = new Button(compositeCtrl, SWT.PUSH);
		backBT.setText("Back");
		backBT.setAlignment(SWT.CENTER);
		
		//go back to previous window
		backBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				resetShell();
				createResourcLoaderWindow();
			}
		});
		
		//set size of shell
		shell.setSize(shellSizeX, shellSizeY);	
		
		/*if ModelLoader is initialized by Inital_BWD_App or Inital_FWD_App
		 * source or target model are generated from tgg*/
		if (modelLoader.getTypeOf() == "FWD")
		{
			trgBT.setEnabled(false);
			trgLabel.setEnabled(false);
			trgTxt.setEnabled(false);
			
		}else if(modelLoader.getTypeOf() == "BWD"){
			srcBT.setEnabled(false);
			srcLabel.setEnabled(false);
			srcTxt.setEnabled(false);
		}
	}
	
	private void modelLocationSelection(String text) {
		switch (text) {
		case "Default":
			System.out.println("option: " + text );
			resetShell();
			createMainWindow();
			break;
		case "New Model":
			System.out.println("option: " + text );
			
			
			resetShell();
			createMainWindow();
			
			//generate a new model
			modelLoader.generateNewModel();
				
			InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(modelLoader.getResourceHandler().getSourceResource(), true);
			InstanceDiagrammLoader dataTrg = new InstanceDiagrammLoader(modelLoader.getResourceHandler().getTargetResource(), true);
			
			Visualizer visSrc = new Visualizer(dataSrc, frameSrc, rectangleSrc);
			Visualizer visTrg = new Visualizer(dataTrg, frameTrg, rectangleTrg);
			
			break;
		case "Select Model":
			System.out.println("option: " + text );
			resetShell();
			CreateDirectorySelectionWindow();
			break;
		}
	}
	
	
	private void loadModelFromPath (String src, String trg) {
		
		try {
			modelLoader.CreateResourcesFromPath(src, trg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Resource could'nt be loaded from path: " + e);
			return;
		}
		
		resetShell();
		createMainWindow();
		
		Resource srcRs = modelLoader.getSource();
		Resource trgRs = modelLoader.getTarget();
		
		InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(srcRs, true);
		InstanceDiagrammLoader dataTrg = new InstanceDiagrammLoader(trgRs, true);
		
		Visualizer visSrc = new Visualizer(dataSrc, frameSrc, rectangleSrc);
		Visualizer visTrg = new Visualizer(dataTrg, frameTrg, rectangleTrg);
		
	}
	
	public String openDirectoryDialog() {
		
		String selectedDir = "";
		
		FileDialog directoryDialog = new FileDialog(shell, SWT.OPEN);
	    
		String filterExt[] = new String[1];
		filterExt[0]	= ".xmi";
		
		directoryDialog.setFilterPath(selectedDir);
		directoryDialog.setFilterExtensions(filterExt);
        
        if(directoryDialog.open() != null) {
        	
        	String dir = directoryDialog.getFilterPath() + System.getProperty( "file.separator" ) + directoryDialog.getFileName();
            selectedDir = dir;
            
            return selectedDir;
         }
        
        return selectedDir;
	}
}


