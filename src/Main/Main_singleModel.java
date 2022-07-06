package Main;

import java.awt.Panel;


import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


import Main.ModelLoader.ResourceType;
import graphVisualization.DataLoader;
import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.Visualizer;
import userInterface.GraphManipulator;
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
	    
	    
	    //EList<EObject> objects = srcResource.getContents();
	    
	    
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
	    	manipulator.removeSelected();
	    	//visSrc.getGraph().getModel().beginUpdate();
	    	/*try {
	    		Object[] selectedCells = visSrc.getGraph().getSelectionCells();
	    		
	    		if(selectedCells.length > 0) {
	    			System.out.println("selected");
	    			for (Object selected : selectedCells) {
	    				visSrc.getGraph().getModel().remove(selected);
	    				for (EObject eObject : objects) {
	    					
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
	    					iterateModelHierarchical(eObject, selected, dataSrc);
	    				}
					}
	    			
	    			
	    			shell.redraw();
	    			System.out.println("redrawn");
	    		}
	    	}
	    	finally {
	    		//visSrc.getGraph().getModel().endUpdate();
	    	}*/
	    	/*visSrc.getGraph().addListener(mxEvent.SELECT,function (sender, evt) {
	    		mxEvent.isRightMouseButton(evt);
	    	
	    	}*/
	    	
	        if (!display.readAndDispatch()) {
	            display.sleep();
	        	
	        }
	        
	    }
	    display.dispose();

	}
	
	/*private static void iterateModelHierarchical(EObject obj, Object comp, InstanceDiagrammLoader loader) {
		for (EObject eobj : obj.eContents()) {
			iterateModelHierarchical(eobj, comp, loader);
		}
		
		mxCell c = (mxCell) comp;
		if(obj.toString().equals(c.getId())) {
			System.out.println("equal found");
			EcoreUtil.remove(obj); //delete wirft Nullpointerexception, aber so wird Kante nicht gelöscht
			System.out.println("removed");
			for (Node nodeElement : loader.nodes) {
				if(nodeElement.id.equals(obj.toString())) {
					loader.nodes.remove(nodeElement);
				}
			}
		}
		System.out.println(obj.toString());
		System.out.println(c.getId());
	}*/
	
}
