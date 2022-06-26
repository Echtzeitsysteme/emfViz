package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;




public class MainWindow {


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
	
	public MainWindow (Shell shell) {
		
		this.shell = shell;
		//this.shellSizeX = this.shell.getSize().x;
		//this.shellSizeY = this.shell.getSize().y;
		
		this.shellSizeX = shell.getMonitor().getClientArea().width;
		this.shellSizeY = shell.getMonitor().getClientArea().height;
		
		/* BASIC LAYOUT
		 * 
		 * 				shellSizeX
		 * 	_________________________________
		 *	|		//area for buttons		|	
		 * 	|								|	ShellSizeY * 0.1
		 * 	|_______________________________|
		 * 	|				|				|
		 * 	|				|				|
		 * 	|				|				|	ShellSizeY
		 * 	|	//graph		|	//graph		|	
		 * 	|	//sourc		|	//target	|	
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
		panelSrc.setBackground(Color.GRAY);
		panelSrc.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		panelTrg = new Panel();
		panelTrg.setLayout(new BorderLayout());
		panelTrg.setBackground(Color.GRAY);
		panelTrg.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  - panelUserCtrl.getSize().height));
		
		
		/*add panels to frame*/
		graphFrame.add(panelSrc);
		graphFrame.add(panelTrg);
		
		//only for debugging
		/* 
		System.out.println("Shell bounds:" + shell.getBounds().toString());
		System.out.println("Default Position:" + defaultNodePosition.toString());
		System.out.println("Panel Src bounds:" + panelSrc.getBounds().toString());
		System.out.println("Panel Trg bounds:" + panelTrg.getBounds().toString());
		*/
		
	}
}
