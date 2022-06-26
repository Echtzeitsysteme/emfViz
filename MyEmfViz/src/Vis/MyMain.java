package Vis;

import java.io.File;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
//Imports require org.eclipse.swt

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
//import CompanyLanguage.CompanyLanguagePackage;

import Hospital2Administration.Hospital2AdministrationPackage;

import graphVisualization.InstanceDiagrammLoader;
// First add emfViz project to your's project build path via 'Properties' -> 'Java Build Path' -> 'Projects' -> 'Classpath' -> 'Add' 
import graphVisualization.Visualizer;

public class MyMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World - Ordner H2A");
		
		Display display = new Display();
		 
        Shell shell = new Shell(display);
        shell.setFullScreen(true);
        
        //shell.setLayout(new FillLayout(SWT.VERTICAL));
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.marginHeight	= 0;
        rowLayout.marginBottom = 0;
        rowLayout.marginTop = 0;
        rowLayout.marginLeft = 0;
        rowLayout.marginRight = 0;
        rowLayout.marginWidth = 0;
        
        shell.setLayout(rowLayout);
       
        //Visualization is optimized for this shell size
        //shell.setSize(shell.getMonitor().getClientArea().width, shell.getMonitor().getClientArea().height);
        
        
        Resource sourceInstance = null;
        Resource targetInstance = null;       
        
        
        //Example for loading an instance model diagram
       // URI base = URI.createPlatformResourceURI("/", true);
       
        
        //Assuming your instance model is contained in a .xmi file, path can be adjusted accordingly
		//URI uri =  URI.createURI("/Hospital2Administration/instances/trg.xmi");
        URI uri =  URI.createURI("/Users/jordanlischka/Documents/runtime-Test_Workspace_2022-05-26/git/emoflon-ibex-tutorial/Hospital2Administration/instances/src.xmi");
		
		
		
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new SmartEMFResourceFactoryImpl("../"));
		
		try {
			rs.getURIConverter().getURIMap().put(URI.createPlatformResourceURI("/", true), URI.createFileURI(new File("../").getCanonicalPath() + File.separator));
			}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		
		rs.getPackageRegistry().put(Hospital2AdministrationPackage.eINSTANCE.getNsURI(), Hospital2AdministrationPackage.eINSTANCE);
		

		
		Resource instanceModel = rs.createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
		try {
		instanceModel.load(null);
		}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
		
		
        
        
		//Create an instace of the InstaceDiagrammLoader class with the loaded resource
		InstanceDiagrammLoader data = new InstanceDiagrammLoader(instanceModel);
		
		
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(null);
		
		
        
        /*
		
        if (sourceInstance == null && targetInstance == null) {
        	return;
        }
        		
        InstanceDiagrammLoader data = new InstanceDiagrammLoader(sourceInstance);
		
		
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(targetInstance);
		
		*/
        
        
        //Create instance of the graph visualizer
		//Visualization will be loaded by its constructor
        
		//Visualizer vis = new Visualizer(shell, data);
		Visualizer vis = new Visualizer(shell, data, dataTarget);
		
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
		
	}

}
