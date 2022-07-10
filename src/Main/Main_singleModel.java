package Main;

import java.awt.Panel;


import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

import Main.ModelLoader.ResourceType;
import graphVisualization.DataLoader;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;
import userInterface.GraphManipulator;
import userInterface.MainWindow;
import userInterface.PopupFrame;

public class Main_singleModel {
	
	public static void main(String[] args) {
		
		Display display = new Display();
		 
        Shell shell = new Shell(display);
      
        shell.setLayout(new FillLayout());
        
        //Visualization is optimized for this shell size
        shell.setSize(shell.getMonitor().getClientArea().width, shell.getMonitor().getClientArea().height);
	    
		ModelLoader modelLoader = new ModelLoader();
		
		
		
		Resource srcResource = modelLoader.loadModelWithResourceHandler(ResourceType.Source);
		
	    InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(srcResource, true);
	    
		/*for (EObject eObject : objects) {
			
			/*EList<EStructuralFeature> allEStructFeats = eObject.eClass().getEAllStructuralFeatures();
			
			for(EStructuralFeature esf : allEStructFeats)
			{
				System.out.println("blub");
			    Object o = eObject.eGet(esf);

			    if(true)
			    {
			    	Integer i = 0;
			        eObject.eSet(esf, i);
			        
			    }
			}
			iterateModelHierarchical(eObject);
			
		}*/
		
		Visualizer visSrc = new Visualizer(shell, dataSrc);
		
		GraphManipulator manipulator = new GraphManipulator(visSrc, srcResource, dataSrc);
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	    	//manipulator.iterateModel();
	    	PopupFrame popup = new PopupFrame(visSrc);
	    	//IbexOptions ibxopt = new IbexOptions();
	    	//ibxopt.tgg.tgg().getSrc()
	    	
	        if (!display.readAndDispatch()) {
	            display.sleep();
	        	
	        }
	        
	    }
	    display.dispose();

	}
}
