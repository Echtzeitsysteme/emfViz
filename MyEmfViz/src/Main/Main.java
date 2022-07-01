package Main;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Hospital2Administration.Hospital2AdministrationPackage;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;

import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.run.hospital2administration.MODELGEN_App;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Display display = new Display();
		 
        Shell shell = new Shell(display);
      
        
        //shell.setLayout(new FillLayout(SWT.VERTICAL));
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.wrap = true;
        rowLayout.pack = true;
        rowLayout.justify = true;
        
        shell.setLayout(rowLayout);
       
        //Visualization is optimized for this shell size
        //shell.setSize(shell.getMonitor().getClientArea().width, shell.getMonitor().getClientArea().height);
        shell.setSize(1800, 1000);
        // load models
        Resource instanceModelSrc = null;
        Resource instanceModelTrg = null;
        
        try {
        	
        	MODELGEN_App generator = new MODELGEN_App();
        	MODELGENStopCriterion stop = new MODELGENStopCriterion(generator.getTGG());
        	stop.setMaxElementCount(10);
        	generator.setStopCriterion(stop);
        	generator.run();
        	
        	instanceModelSrc = generator.getResourceHandler().getSourceResource();
        	instanceModelTrg = generator.getResourceHandler().getTargetResource();
        	
        } catch(IOException e) {
        	System.out.println(e.getMessage());
        }
        
        
        		
        InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(instanceModelSrc, true);
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(instanceModelTrg, true);
		
		Visualizer vis = new Visualizer(shell, dataSrc, dataTarget);
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();

	}

}
