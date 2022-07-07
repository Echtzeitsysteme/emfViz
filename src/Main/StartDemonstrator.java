package Main;



import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import userInterface.MainWindow;

import java.awt.Panel;

public class StartDemonstrator {
	
	

	public static void main(String[] args) {
	
		Display display = new Display();
		Shell shell = new Shell(display);
		
  		MainWindow graphVisualizer = new MainWindow(shell);
  		
  		Panel panelSrc = graphVisualizer.panelSrc;
		Panel panelTrg = graphVisualizer.panelTrg;
        
		
		/*Visualizer visSrc = new Visualizer(dataSrc, panelSrc);
		Visualizer visTrg = new Visualizer(dataTarget, panelTrg);*/
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
	}
}
