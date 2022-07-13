package Example;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
//Imports require org.eclipse.swt
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import HospitalExample.HospitalExamplePackage;
import graphVisualization.InstanceDiagrammLoader;
// First add emfViz project to your's project build path via 'Properties' -> 'Java Build Path' -> 'Projects' -> 'Classpath' -> 'Add' 
import graphVisualization.Visualizer;

public class Example {
	
	public static void main(String[] args) {
		

		
        Display display = new Display();
 
        Shell shell = new Shell(display);
      
        shell.setLayout(new FillLayout());
        
        //Visualization is optimized for this shell size
        shell.setSize(shell.getMonitor().getClientArea().width, shell.getMonitor().getClientArea().height);
        
        //Example for loading an instance model diagramm
        
        URI base = URI.createPlatformResourceURI("/", true);
        
        //Assuming your instance model is contained in an .xmi file, path can be adjusted accordingly
		URI uri =  URI.createURI("hospital.xmi");
		System.out.println(uri.devicePath());
		
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
		
		rs.getPackageRegistry().put(HospitalExamplePackage.eINSTANCE.getNsURI(), HospitalExamplePackage.eINSTANCE);
		

		
		Resource instanceModel = rs.createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
		try {
		instanceModel.load(null);
		}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
        
		//Create an instance of the InstaceDiagrammLoader class with the loaded resource
		InstanceDiagrammLoader data = new InstanceDiagrammLoader(instanceModel);

        //Create instance of the graph visualizer
		//Visualization will be loaded by its constructor
        Visualizer vis = new Visualizer(shell, data);
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        

	}

}
