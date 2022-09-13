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
	
	private final int shellSizeX;
	private final int shellSizeY;
	
	
	public TggLoadModelDisplay(DisplayHandler handler, TGGDemonstrator modelLoader, Display display, Shell shell) {
		this.handler = handler;
		this.modelLoader = modelLoader;
		this.display = display;
		this.shell = shell;
		
		shellSizeX = 600;
		shellSizeY = 200;
		
		createLoadModelDisplay();
	}
	
	/*
	 * Window to select between different model loading options.
	 * This is the start window
	 */
	private void createLoadModelDisplay () {
		
		shell.setLayout(new GridLayout());
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setVisible(true);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true));
		composite.setLayout(new GridLayout());	
		
		Group modelGroup = new Group(composite, SWT.None);
		modelGroup.setText("Select model location:");
		modelGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		modelGroup.setLayout(new GridLayout());
		
		Button defaultButton = new Button(modelGroup, SWT.RADIO);
		defaultButton.setText("Default Models");
		
		Button newModeltButton = new Button(modelGroup, SWT.RADIO);
		newModeltButton.setText("New Models");
		
		Button modelLocationButton = new Button(modelGroup, SWT.RADIO);
		modelLocationButton.setText("Select Models");
		
		Button nextButton = new Button(composite, SWT.PUSH);
		nextButton.setText("Next");
		nextButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		nextButton.addSelectionListener(new SelectionAdapter() {
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
	 * Choose between different model loading options and execute selected choice
	 * Hint - IbexOptions are available after the specific loading model function from ModelLoader is called
	*/
	private void modelLoadingOptionSelection(String text) {
		switch (text) {
		case "Default Models":
			modelLoader.loadFromDefault();
		
			handler.openTggVisualizerDisplay();
			
			break;
			
		case "New Models":
			modelLoader.generateNewModel();	
			
			handler.openTggVisualizerDisplay();;
			
			break;
			
		case "Select Models":
			handler.openTggResourceSelectionDisplay();
			
			break;
		}
	}
}
