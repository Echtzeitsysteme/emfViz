package userInterface;


import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import Main.ModelLoader;
import Main.ModelLoader.ResourceType;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;




public class MainWindow {

	private Shell shell;
	
	private ModelLoader modelLoader;
	
	private int shellSizeX;
	private int shellSizeY;
	
	public Panel panelUserCtrl;
	public Panel panelSrc;
	public Panel panelTrg;
	
	public MainWindow (Shell shell) {
		
		
		this.modelLoader = null;
		this.shell = shell;
		
		
		resourcLoaderWindow();
		
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
		shellSizeX = shell.getDisplay().getClientArea().width;
		shellSizeY = shell.getDisplay().getClientArea().height;
		
        shell.setSize(shellSizeX, shellSizeY);      
        
        Composite composite = new Composite(this.shell, SWT.EMBEDDED | SWT.BACKGROUND);
        composite.setSize(shellSizeX, shellSizeY);
        composite.setVisible(true);
        
        Frame frame = SWT_AWT.new_Frame(composite);
        frame.setLayout(null);
        //frame.setBounds(0, 0, shellSizeX, (int) (shellSizeY * 0.1));
		
		Panel panel = new Panel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0,0,shellSizeX, (int) (shellSizeY * 0.1));
		
		/*add panel to frame*/
		frame.add(panel);
	
		
		/* generate two panels to display the graphs*/
		panelSrc = new Panel();
		panelSrc.setBackground(Color.LIGHT_GRAY);
		panelSrc.setBounds(0, (int) (shellSizeY * 0.1) + 3, (int) (shellSizeX * 0.5)-3, (int) (shellSizeY * 0.9)-6);
		//panelSrc.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		panelTrg = new Panel();
		panelTrg.setBackground(Color.WHITE);
		panelTrg.setBounds((int) (shellSizeX * 0.5) + 3, (int) (shellSizeY * 0.1)+3, (int) (shellSizeX * 0.5)-3, (int) (shellSizeY * 0.9)-6);
		//panelTrg.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		
		/*add panels to frame*/
		frame.add(panelSrc);
		frame.add(panelTrg);
		
		
		frame.validate();
		frame.repaint();
	}
	
	public void resourcLoaderWindow () {
		
		shellSizeX = 600;
		shellSizeY = 280;
		
		shell.setSize(shellSizeX, shellSizeY);
		
		Composite composite = new Composite(this.shell, SWT.EMBEDDED | SWT.BACKGROUND);
		composite.setVisible(true);
		composite.setSize(shellSizeX, shellSizeY);
		
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.setLayout(null);
		frame.setTitle("Choose your model loading option");
		
	
		CheckboxGroup bgroup = new CheckboxGroup();
		Checkbox defaultButton = new Checkbox("Default",bgroup, true);
		defaultButton.setForeground(Color.BLACK);
		defaultButton.setBounds(20, 30, 160, 20);
		Checkbox emptyModelButton = new Checkbox("Create new model",bgroup,false);
		emptyModelButton.setForeground(Color.BLACK);
		emptyModelButton.setBounds(20, 80, 160, 20);
		Checkbox chooseModelButton = new Checkbox("Select model",bgroup,false);
		chooseModelButton.setForeground(Color.BLACK);
		chooseModelButton.setBounds(20, 130, 160, 20);
		
		
		
		
        
        Button nextBt	= new UIButton("next");       
        nextBt.setBounds(510,220,80,20);
        nextBt.addActionListener(new NextActionListener(bgroup, this));
        
        frame.add(defaultButton);
        frame.add(emptyModelButton);   
        frame.add(chooseModelButton);  
        frame.add(nextBt);
		
	}
	
	public void chooseLocationWindow() {
		shellSizeX = 600;
		shellSizeY = 280;
		
		shell.setSize(shellSizeX, shellSizeY);
		
		Composite composite = new Composite(this.shell, SWT.EMBEDDED | SWT.BACKGROUND);
		composite.setVisible(true);
		composite.setSize(shellSizeX, shellSizeY);
		
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.setLayout(null);
		frame.setTitle("Select your models");
		
		Label labelSource = new UILabel("Please select your source model.");
		Label labelTarget = new UILabel("Please select your target model.");
		
		labelSource.setBounds(20, 30, 300, 20);
		labelTarget.setBounds(20, 130, 300, 20);
		
		Label sourceLoc	= new UILabel ("Source dir:");
		Label targetLoc	= new UILabel ("Target dir:");
		
		sourceLoc.setBounds(20, 60, 150, 20);
		targetLoc.setBounds(20, 160, 150, 20);
		
		TextField sourceTxt	= new UITextField();
		TextField targetTxt	= new UITextField();
		
		sourceTxt.setBounds(170, 60, 420, 20);
		targetTxt.setBounds(170, 160, 420, 20);
		
		Button selectSrc	= new UIButton("select");
		Button selectTrg	= new UIButton("select");
		
		selectSrc.addActionListener(new SelectDirListener(shell, sourceTxt));
		selectTrg.addActionListener(new SelectDirListener(shell, targetTxt));
		
		selectSrc.setBounds(510,30,80,20);
		selectTrg.setBounds(510,130,80,20);
		
		Button next	= new UIButton("next");
		next.setBounds(510,220,80,20);
		next.addActionListener(new LoadActionListener(this, sourceTxt, targetTxt));
		
		frame.add(labelSource);
		frame.add(labelTarget);
		frame.add(sourceLoc);
		frame.add(targetLoc);
		frame.add(selectSrc);
		frame.add(selectTrg);
		frame.add(sourceTxt);
		frame.add(targetTxt);
		frame.add(next);
		
	}
	
	public void resetShell() {
		for (Control kid : shell.getChildren()) {
	          kid.dispose();
	    }
	}
	
	public Shell getShell() {
		return shell;
	}
}

class SelectDirListener implements ActionListener{
	
	TextField textField;
	Shell shell;
	String selectedDir;
	
	public SelectDirListener(Shell s, TextField txt) {
		
		shell = s;
		textField = txt;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				FileDialog directoryDialog = new FileDialog(shell, SWT.OPEN);
			    
				String filterExt[] = new String[1];
				filterExt[0]	= ".xmi";
				
				directoryDialog.setFilterPath(selectedDir);
				directoryDialog.setFilterExtensions(filterExt);
		        
		        if(directoryDialog.open() != null) {
		        	
		        	String dir = directoryDialog.getFilterPath() + System.getProperty( "file.separator" ) + directoryDialog.getFileName();
		        	textField.setText(dir);
		            selectedDir = dir;
		            
		            
		         }
			}
			
		});
	}
	
}

class LoadActionListener implements ActionListener{

	MainWindow mw;
	ModelLoader modelLoader;
	TextField src;
	TextField trg;
	
	public LoadActionListener(MainWindow mw, TextField src, TextField trg) {
		this.mw = mw;
		modelLoader = new ModelLoader();
		this.src = src;
		this.trg = trg; 
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(src.getText() + " // " + trg.getText());
		
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				mw.resetShell();
				
				mw.createMainWindow();
				
				
				modelLoader.CreateResourcesFromPath(src.getText(), trg.getText());
				Resource src = modelLoader.getSource();
				Resource trg = modelLoader.getTarget();
				
				InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(src, true);
				InstanceDiagrammLoader dataTrg = new InstanceDiagrammLoader(trg, true);
				
				Visualizer visSrc = new Visualizer(dataSrc, mw.panelSrc );
				Visualizer visTrg = new Visualizer(dataTrg, mw.panelTrg);
			}
		});
		
	}
}

class NextActionListener implements ActionListener{
	
	private CheckboxGroup bgroup;
	private MainWindow mw;
	
	public NextActionListener(CheckboxGroup bgroup, MainWindow mw) {
		this.bgroup = bgroup;	
		this.mw = mw;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Checkbox checkBox = bgroup.getSelectedCheckbox();
		String selection = checkBox.getLabel();
		switch (selection){
		case "Default":
			
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
				
					mw.resetShell();
					
					mw.createMainWindow();
					
					String workingDirectory = "/Users/jordanlischka/Documents/runtime-Test_Workspace_2022-05-26/git/emoflon-ibex-tutorial/Hospital2Administration/";
					
					InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(ModelLoader.loadModelWithURI(ResourceType.Source, workingDirectory), true);
					InstanceDiagrammLoader dataTrg = new InstanceDiagrammLoader(ModelLoader.loadModelWithURI(ResourceType.Target, workingDirectory), true);
					
					Visualizer visSrc = new Visualizer(dataSrc, mw.panelSrc );
					Visualizer visTrg = new Visualizer(dataTrg, mw.panelTrg);
				}
			});
			
			break;
		case "Create new model":
			//close this shell and create new one
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
				
					mw.resetShell();
					
					mw.createMainWindow();
					
					ModelLoader modelLoader = new ModelLoader();
					modelLoader.generateNewModel();
					
					InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(modelLoader.loadModelWithResourceHandler(ResourceType.Source), true);
					InstanceDiagrammLoader dataTrg = new InstanceDiagrammLoader(modelLoader.loadModelWithResourceHandler(ResourceType.Target), true);
					
					Visualizer visSrc = new Visualizer(dataSrc, mw.panelSrc );
					Visualizer visTrg = new Visualizer(dataTrg, mw.panelTrg);	
				}
				
			});
			 
			break;
			
		case "Select model":
			
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					
					mw.resetShell();
					
					mw.chooseLocationWindow();
					
				}
			});
			
			break;
		
		}
	}
}

/*general button style*/
class UIButton extends Button{
	
	public UIButton (String text) {
		super(text);

		setForeground(Color.BLACK);
		
		
	}
}

class UILabel extends Label{
	public UILabel(String text) {
		super(text);
		
		setForeground(Color.BLACK);
		
		
	}
}

class UITextField extends TextField{
	public UITextField() {
		super();
		
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
	}
}
