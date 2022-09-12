package visualisation;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.TGGDemonstrator;

public class TggSelectResourceDisplay {

	private DisplayHandler handler;
	private TGGDemonstrator modelLoader;
	private Display display;
	private Shell shell;
	
	private final int shellSizeX;
	private final int shellSizeY;
	
	/*
	 * Constructor class
	 *  - initalize display handeler, modelloader, display and shell
	 *  - set up shell size
	 *  - create window
	 */
	public TggSelectResourceDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
		shellSizeX = 460;
		shellSizeY = 500;
		
		CreateDirectorySelectionWindow();
	}
	
	/*
	 * This window will open when option "Select Model" is chosen.
	 * Window to select location of source, target, corr and protocol file or project directory
	 * MODELGEN - source and target model must be selected
	 * INITIAL_FWD - source model must be selected, a project folder can be selected to store the created files
	 * INITIAL_BWD - target model must be selected, a project folder can be selected to store the created files
	*/
	private void CreateDirectorySelectionWindow() {
			
		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		//create composite
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);
		
		GridData gridData1 = new GridData(GridData.FILL_BOTH);	
		composite.setLayoutData(gridData1);
		composite.setLayout(new GridLayout());
		
		//create groups
		Group projectGroup = new Group(composite, SWT.None);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		projectGroup.setLayout(new GridLayout(2, false));
		
		Group srcGroup = new Group(composite, SWT.None);
		srcGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		srcGroup.setLayout(new GridLayout(2, false));
		
		Group trgGroup = new Group(composite, SWT.None);
		trgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		trgGroup.setLayout(new GridLayout(2, false));
		
		Group corrGroup = new Group(composite, SWT.None);
		corrGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		corrGroup.setLayout(new GridLayout(2, false));
		
		Group protocolGroup = new Group(composite, SWT.None);
		protocolGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		protocolGroup.setLayout(new GridLayout(2, false));
		
		// create widgets for project location
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setText("Select project location (optional):");
		
		Button projectButton = new Button(projectGroup, SWT.PUSH);
		projectButton.setText("Location");
		projectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text projectTxt = new Text(projectGroup, SWT.NONE);
		
		GridData gridDataProject = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataProject.widthHint = 300;
		
		projectTxt.setLayoutData(gridDataProject);
		
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				projectTxt.setText(openDirectoryDialog());
			}
		});
		
		
		//create widgets for source location
		Label srcLabel = new Label(srcGroup, SWT.NONE);
		srcLabel.setText("Select your source xmi:");
		
		Button srcButton = new Button(srcGroup, SWT.PUSH);
		srcButton.setText("Source xmi");
		srcButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text srcTxt = new Text(srcGroup, SWT.NONE);
		
		GridData gridDataSrc = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataSrc.widthHint = 300;
				
		srcTxt.setLayoutData(gridDataSrc);
		
		srcButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				srcTxt.setText(openFileDialog());
			}
		});
		
		//create widgets for target location		
		Label trgLabel = new Label(trgGroup, SWT.NONE);
		trgLabel.setText("Select your target xmi:");
		
		Button trgButton = new Button(trgGroup, SWT.PUSH);
		trgButton.setText("Target xmi");
		trgButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text trgTxt = new Text(trgGroup, SWT.NONE);
		
		GridData gridDataTrg = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataTrg.widthHint = 300;
		
		trgTxt.setLayoutData(gridDataTrg);
		
		trgButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				trgTxt.setText(openFileDialog());
			}
		});
		
		//create widgets for corr location
		Label corrLabel = new Label(corrGroup, SWT.NONE);
		corrLabel.setText("Select your corr xmi (optional):");
		
		Button corrButton = new Button(corrGroup, SWT.PUSH);
		corrButton.setText("Corr xmi");
		corrButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text corrTxt = new Text(corrGroup, SWT.NONE);
		
		GridData gridDataCorr = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataCorr.widthHint = 300;
		
		corrTxt.setLayoutData(gridDataCorr);
		
		corrButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				corrTxt.setText(openFileDialog());
			}
		});
		
		// create widgets for protocol location
		Label protocolLabel = new Label(protocolGroup, SWT.NONE);
		protocolLabel.setText("Select your protocol xmi (optional):");
		
		Button protocolButton = new Button(protocolGroup, SWT.PUSH);
		protocolButton.setText("Protocol xmi");
		protocolButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Text protocolTxt = new Text(protocolGroup, SWT.NONE);
		
		GridData gridDataProtocol = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDataProtocol.widthHint = 300;
		
		protocolTxt.setLayoutData(gridDataProtocol);
		
		protocolButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				protocolTxt.setText(openFileDialog());
			}
		});
		
		//control buttons / composite - back and forward(next)
		Composite compositeCtrl = new Composite(shell, SWT.EMBEDDED);
		compositeCtrl.setVisible(true);
		compositeCtrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeCtrl.setLayout(new RowLayout());
		
		Button nextButton = new Button(compositeCtrl, SWT.PUSH);
		nextButton.setText("Next");
		nextButton.setAlignment(SWT.CENTER);
		
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				loadModelFromPath(srcTxt.getText(), trgTxt.getText(), corrTxt.getText(), protocolTxt.getText(), projectTxt.getText());
			}
		});
		
		// button to go back to previous window
		Button backButton = new Button(compositeCtrl, SWT.PUSH);
		backButton.setText("Back");
		backButton.setAlignment(SWT.CENTER);
		
		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				handler.openTggLoadModelDisplay();
			}
		});
		
		if (modelLoader instanceof ModelLoader_INITIAL_BWD)
			srcLabel.setText("Select your source xmi (optional):");
		else if (modelLoader instanceof ModelLoader_INITIAL_FWD)
			trgLabel.setText("Select your target xmi (optional):");
			
		
		//set size of shell
		shell.setSize(shellSizeX, shellSizeY);	
	}
	
	
	
	/*
	 * start loading source and target model from selected path
	 */
	private void loadModelFromPath (String srcPath, String trgPath, String corrPath, String protocolPath, String projectPath) {
		
		modelLoader.createResourcesFromPath(srcPath, trgPath, corrPath, protocolPath, projectPath);
		
		handler.openTggVisualizerDisplay();
	}
	
	/*
	 *  open a directory dialog window
	*/
	public String openFileDialog() {
		
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
	
	public String openDirectoryDialog() {
		
		String selectedDir = "";
		
		DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.OPEN);
	    
		
		
		directoryDialog.setFilterPath(selectedDir);
	
        
        if(directoryDialog.open() != null) {
        	
        	String dir = directoryDialog.getFilterPath() + System.getProperty( "file.separator" );
            selectedDir = dir;
            
            return selectedDir;
         }
        
        return selectedDir;
	}
}
