package Main;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import userInterface.MainWindow;

import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.run.hospital2administration.MODELGEN_App;

import Main.ModelLoader.ResourceType;

import java.awt.Panel;
import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;

public class Main_withResourceHandler {

	public static void main(String[] args) {
	
		Display display = new Display();
		 
		//initialize shell layout
        Shell shell = new Shell(display);
        shell.setFullScreen(true);
        
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.marginHeight	= 0;
        rowLayout.marginBottom = 0;
        rowLayout.marginTop = 0;
        rowLayout.marginLeft = 0;
        rowLayout.marginRight = 0;
        rowLayout.marginWidth = 0;
        
        shell.setLayout(rowLayout);
      
		
  		MainWindow graphVisualizer = new MainWindow(null, shell);
  		graphVisualizer.createMainWindow();
  		
  		Panel panelSrc = graphVisualizer.panelSrc;
		Panel panelTrg = graphVisualizer.panelTrg;
        
     
        InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(ModelLoader.loadModelWithResourceHandler(ResourceType.Source), true);
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(ModelLoader.loadModelWithResourceHandler(ResourceType.Target), true);
		
		
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
