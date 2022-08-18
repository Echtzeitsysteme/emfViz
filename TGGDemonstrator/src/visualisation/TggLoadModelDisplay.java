package visualisation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.ModelLoader_MODELGEN;
import tggDemonstrator.ModelLoader_SYNC;
import tggDemonstrator.TGGDemonstrator;

public class TggLoadModelDisplay {

	
	
	private DisplayHandler handler;
	private TGGDemonstrator modelLoader;
	private Display display;
	private Shell shell;
	
	private int shellSizeX;
	private int shellSizeY;
	
	public TggLoadModelDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		//super(modelLoader);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
		createLoadModelDisplay();
	}
	
	/*
	 * Window to select between different model loading options.
	 * It is the initial window (start window)
	 */
	private void createLoadModelDisplay () {
		
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
						modelLoadingOptionSelection(((Button)bt).getText());
						break;
					}
				}
			}
		});
		
		shell.setSize(shellSizeX, shellSizeY);
	}
	
	/*
	 * choose between different model loading options and execute selected choice
	 * hint - IbexOptions are available after the specific loading model function from ModelLoader is called
	*/
	private void modelLoadingOptionSelection(String text) {
		switch (text) {
		case "Default":
			System.out.println("selected loading option: " + text );
			
			modelLoader.loadFromDefault();
		
			handler.openTggVisualizerDisplay();
			
			break;
			
		case "New Model":
			System.out.println("selected loading option: " + text );
			
			//generate a new model
			modelLoader.generateNewModel();	
			
			handler.openTggVisualizerDisplay();;
			
			break;
			
		case "Select Model":
			System.out.println("selected loading option: " + text );
			
			handler.openTggResourceSelectionDisplay();
			
			break;
		}
	}
}
