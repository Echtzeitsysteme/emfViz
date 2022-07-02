package Main;

import java.awt.Panel;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Main.ModelLoader.ResourceType;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Visualizer;
import userInterface.MainWindow;

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
		
		
		Visualizer visSrc = new Visualizer(shell, dataSrc);
		
		/*EList<EObject> objects = srcResource.getContents();
		
		int i = 0;
		for (EObject eObject : objects) {
			if (i==0) EcoreUtil.remove(eObject);
			i++;
			
		}*/
		
		//visSrc.getGraph();
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch()) {
	            display.sleep();
	        }
	    }
	    display.dispose();

	}
	
}
