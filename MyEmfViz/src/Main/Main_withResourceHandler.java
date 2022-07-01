package Main;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import userInterface.MainWindow;

import Main.ModelLoader.ResourceType;

import java.awt.Panel;

public class Main_withResourceHandler {
	
	

	public static void main(String[] args) {
	
		Display display = new Display();
		
  		MainWindow graphVisualizer = new MainWindow(display, null);
  		
  		Panel panelSrc = graphVisualizer.panelSrc;
		Panel panelTrg = graphVisualizer.panelTrg;
        
		ModelLoader modelLoader = new ModelLoader();
		
		graphVisualizer.setModelLoader(modelLoader);
		
		Shell shell = graphVisualizer.getShell();
		
        InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(modelLoader.loadModelWithResourceHandler(ResourceType.Source), true);
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(modelLoader.loadModelWithResourceHandler(ResourceType.Target), true);
		
		
		Visualizer visSrc = new Visualizer(dataSrc, panelSrc);
		Visualizer visTrg = new Visualizer(dataTarget, panelTrg);
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();

	}

}
