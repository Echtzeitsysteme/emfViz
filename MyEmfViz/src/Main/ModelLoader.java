package Main;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.run.hospital2administration.MODELGEN_App;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import Hospital2Administration.Hospital2AdministrationPackage;

public class ModelLoader {
	
	public enum ResourceType {Source, Target}
	private static Resource instanceModel;
	private static URI uri;
	
	
	public static Resource loadModelWithURI(ResourceType resourceType, String wokingDirectory) {
		
		
		//Example for loading an instance model diagram
       // URI base = URI.createPlatformResourceURI("/", true);
       
        
        //Assuming your instance model is contained in a .xmi file, path can be adjusted accordingly
		//URI uri =  URI.createURI("/Hospital2Administration/instances/trg.xmi");
		
		
		switch (resourceType) {
		case Source:
			uri = URI.createURI(wokingDirectory + "instances/src.xmi");
			break;
		case Target:
			uri = URI.createURI(wokingDirectory + "instances/trg.xmi");
			break;
		}
				
				
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
		

		
		instanceModel = rs.createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
		try {
		instanceModel.load(null);
		}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
		
		return instanceModel;
	}
	
	public static Resource loadModelWithResourceHandler(ResourceType resourceType) {
		
		try {
        	
        	MODELGEN_App generator = new MODELGEN_App();
        	MODELGENStopCriterion stop = new MODELGENStopCriterion(generator.getTGG());
        	stop.setMaxRuleCount("HospitaltoAdministrationRule", 1);
    		stop.setMaxElementCount(10);
        	generator.setStopCriterion(stop);
        	generator.run();
		
			switch (resourceType) {
			case Source:
				instanceModel = generator.getResourceHandler().getSourceResource();
				break;
			case Target:
				instanceModel = generator.getResourceHandler().getTargetResource();
				break;
			}
			
			
		 } catch(IOException e) {
	        	System.out.println(e.getMessage());
	        }
		
		return instanceModel;
	}

}
