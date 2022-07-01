package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		this.shellSizeX = shell.getSize().x;
		this.shellSizeY = shell.getSize().y;
		
		
		//layout for user control widgets
		compositeUserCtrl = new Composite(shell, SWT.EMBEDDED | SWT.BACKGROUND);
		compositeUserCtrl.setVisible(true);
		compositeUserCtrl.setLayoutData(new RowData(shellSizeX, (int) (shellSizeY * 0.1)));
		
		userCtrlFrame = SWT_AWT.new_Frame(compositeUserCtrl);
		userCtrlFrame.setLayout(new BorderLayout());
		
		
		panelUserCtrl = new Panel();
		panelUserCtrl.setBackground(Color.WHITE);
		
		/*add panel to frame*/
		userCtrlFrame.add(panelUserCtrl);

	
		//graph visualization window
		compositeGraph = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		compositeGraph.setVisible(true);
		compositeGraph.setLayoutData(new RowData(shellSizeX, (int) (shellSizeY  * 0.9 - 30)));
		
		graphFrame = SWT_AWT.new_Frame(compositeGraph);
		graphFrame.setLayout(new GridLayout());
		
		/* generate two panels to display the graphs*/
		panelSrc = new Panel();
		panelSrc.setLayout(new BorderLayout());
		panelSrc.setBackground(Color.GRAY);
		panelSrc.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  * 0.9-30));
		
		panelTrg = new Panel();
		panelTrg.setLayout(new BorderLayout());
		panelTrg.setBackground(Color.GRAY);
		panelTrg.setSize((int) (shellSizeX * 0.5), (int) (shellSizeY  * 0.9-30));
		
		
		/*add panels to frame*/
		graphFrame.add(panelSrc);
		graphFrame.add(panelTrg);
		
		panelSrc.addMouseListener(new generateElementMouseListener());
		
		//only for debugging
		/* 
		System.out.println("Shell bounds:" + shell.getBounds().toString());
		System.out.println("Default Position:" + defaultNodePosition.toString());
		System.out.println("Panel Src bounds:" + panelSrc.getBounds().toString());
		System.out.println("Panel Trg bounds:" + panelTrg.getBounds().toString());
		*/
		
	}
	
	class generateElementMouseListener extends MouseAdapter 
	{
	  @Override
	  public void mouseClicked(MouseEvent e) 
	  {
	    System.out.println("Test MouseListener");
	  }
	}
}
