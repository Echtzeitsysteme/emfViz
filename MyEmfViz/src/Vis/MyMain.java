package Vis;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
//Imports require org.eclipse.swt
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.run.hospital2administration.debug.MODELGEN_App;
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
      
        
        //shell.setLayout(new FillLayout(SWT.VERTICAL));
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.wrap = true;
        rowLayout.pack = true;
        rowLayout.justify = true;
        
        shell.setLayout(rowLayout);
       
        //Visualization is optimized for this shell size
        shell.setSize(shell.getMonitor().getClientArea().width, shell.getMonitor().getClientArea().height);
        
        
        Resource sourceInstance = null;
        Resource targetInstance = null;
        
        /*
        
        Resource sourceInstance = null;
        Resource targetInstance = null;
        
        try {
			MODELGEN_App app = new MODELGEN_App();
			
			TGGResourceHandler myResourceHandler = app.getResourceHandler();
			
			myResourceHandler.loadModels();
			
			sourceInstance = myResourceHandler.getSourceResource();
			targetInstance = myResourceHandler.getTargetResource();
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        */
         
       
        
        
        //Example for loading an instance model diagram
        URI base = URI.createPlatformResourceURI("/", true);
        
        //Assuming your instance model is contained in a .xmi file, path can be adjusted accordingly
		URI uri =  URI.createURI("/Users/Elena/emoflon-workspaces/runtime-06-21-tggViz-workspace/git/emoflon-ibex-tutorial/Hospital2Administration/instances/trg.xmi");
		
		
		
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
		
		
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(instanceModel);
		
		
        
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
