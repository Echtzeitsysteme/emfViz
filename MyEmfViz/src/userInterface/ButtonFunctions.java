/**
 * 
 */
package userInterface;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import Hospital2Administration.Hospital2AdministrationPackage;
import graphVisualization.InstanceDiagrammLoader;

/**
 * @author jordanlischka
 *
 */
public class ButtonFunctions {
	
	public ButtonFunctions() {
		
	}
	
	public static InstanceDiagrammLoader loadTargetModelWithURI(URI uri) {
		
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
		
		
		return data;
	}

}
