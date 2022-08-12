package visualisation;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
	
	private int shellSizeX;
	private int shellSizeY;
	
	public TggSelectResourceDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		//super(modelLoader);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
		CreateDirectorySelectionWindow();
	}
	
	/*
	 * This window will open when option "Select Model" is chosen.
	 * Window to determine location of source and target model
	*/
	private void CreateDirectorySelectionWindow() {
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
		
		// button to go back to initial window
		Button backBT = new Button(compositeCtrl, SWT.PUSH);
		backBT.setText("Back");
		backBT.setAlignment(SWT.CENTER);
		
		//go back to previous window
		backBT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				handler.openTggLoadModelDisplay();
			}
		});
		
		//set size of shell
		shell.setSize(shellSizeX, shellSizeY);	
		
		/*
		 * if ModelLoader is initialized by Inital_BWD_App or Inital_FWD_App
		 * source or target model are generated from tgg.
		 */
		if (modelLoader instanceof ModelLoader_INITIAL_FWD)
		{
			trgBT.setEnabled(false);
			trgLabel.setEnabled(false);
			trgTxt.setEnabled(false);
			
		}else if(modelLoader instanceof ModelLoader_INITIAL_BWD){
			srcBT.setEnabled(false);
			srcLabel.setEnabled(false);
			srcTxt.setEnabled(false);
		}
	}
	
	
	
	/*
	 * start loading source and target model from selected path
	 */
	private void loadModelFromPath (String src, String trg) {
		
		modelLoader.createResourcesFromPath(src, trg);
		
		handler.openTggVisualizerDisplay();
	}
	
	/*
	 *  open a directory dialog window
	*/
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
