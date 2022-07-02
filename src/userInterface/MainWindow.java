package userInterface;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Main.ModelLoader;
import Main.ModelLoader.ResourceType;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;




public class MainWindow {

	private ModelLoader modelLoader;
	
	private Shell shell;
	private Composite compositeGraph;
	private Composite compositeUserCtrl;
	private Frame graphFrame;
	private Frame userCtrlFrame; 	
	
	final int shellSizeX;
	final int shellSizeY;
	
	public Panel panelUserCtrl;
	public Panel panelSrc;
	public Panel panelTrg;
	
	public MainWindow (Shell shell, ModelLoader modelLoader) {
		
		
		this.modelLoader = modelLoader;
		//this.shellSizeX = this.shell.getSize().x;
		//this.shellSizeY = this.shell.getSize().y;
		
		this.shell = shell;
		
		this.shellSizeX = shell.getMonitor().getClientArea().width;
		this.shellSizeY = shell.getMonitor().getClientArea().height;
		shell.setSize(shellSizeX, shellSizeY);
		
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
		
		
		//layout for user control widgets
		compositeUserCtrl = new Composite(this.shell, SWT.EMBEDDED | SWT.BACKGROUND);
		compositeUserCtrl.setVisible(true);
		compositeUserCtrl.setLayoutData(new RowData(shellSizeX, (int) (shellSizeY * 0.1)));
		
		userCtrlFrame = SWT_AWT.new_Frame(compositeUserCtrl);
		userCtrlFrame.setLayout(new BorderLayout());
		
		
		panelUserCtrl = new Panel();
		panelUserCtrl.setBackground(Color.WHITE);
		panelUserCtrl.setSize(shellSizeX, (int) (shellSizeY * 0.1));
		
		/*add panel to frame*/
		userCtrlFrame.add(panelUserCtrl);
	
		//graph visualization window
		compositeGraph = new Composite(this.shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		compositeGraph.setVisible(true);
		compositeGraph.setLayoutData(new RowData(shellSizeX, (int) (shellSizeY  - panelUserCtrl.getSize().height)));
		
		graphFrame = SWT_AWT.new_Frame(compositeGraph);
		graphFrame.setLayout(new GridLayout());
		
		/* generate two panels to display the graphs*/
		panelSrc = new Panel();
		panelSrc.setLayout(new BorderLayout());
		panelSrc.setBackground(Color.WHITE);
		panelSrc.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		panelTrg = new Panel();
		panelTrg.setLayout(new BorderLayout());
		panelTrg.setBackground(Color.WHITE);
		panelTrg.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		
		/*add panels to frame*/
		graphFrame.add(panelSrc);
		graphFrame.add(panelTrg);
		
		
		
		/* now add buttons and other user control stuff*/	
		Button btLoadTarget = new UIButton("Load target Model");
		panelUserCtrl.add(btLoadTarget);
			
		btLoadTarget.addActionListener(new LoadTargetModelActionListener(this));
		
	}
	
	public void setModelLoader(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
	}
	
	public ModelLoader getModelLoader () {
		return modelLoader;
	}
}


class LoadTargetModelActionListener implements ActionListener{
	
	
	private Panel panel;
	private ModelLoader modelLoader;
	private MainWindow window;
	
	public LoadTargetModelActionListener(MainWindow window) {
		this.window = window;
		this.panel = window.panelTrg;
		this.modelLoader = null;
		//TODO handle panel = null
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		/*	Display.getDefault().asyncExec(new Runnable() {
		    public void run() {}});*/
		
		// add code to load target model
		System.out.println("Start loading target model...");
		
		//delete all components from this panel
		
    	panel.removeAll();
    	
    	modelLoader = window.getModelLoader();
		
		InstanceDiagrammLoader data = new InstanceDiagrammLoader(modelLoader.loadModelWithResourceHandler(ResourceType.Target), true);
		Visualizer vis = new Visualizer(data, panel);
		
		
    	
		panel.revalidate();
    	panel.repaint();
	}
	
}
class generateElementMouseListener extends MouseAdapter 
{
  @Override
  public void mouseClicked(MouseEvent e) 
  {
    System.out.println("Test MouseListener");
  }
}


/*general button style*/
class UIButton extends Button{
	
	public UIButton (String text) {
		super(text);

		setForeground(Color.BLACK);
		
	}
}
