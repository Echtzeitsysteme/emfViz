package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.ibex.common.emf.EMFSaveUtils;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler.TGGFileNotFoundException;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
//import org.emoflon.ibex.tgg.run.hospital2administration.MODELGEN_App;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;

//import Hospital2Administration.Hospital2AdministrationPackage;

public class ModelLoader {
	
	public enum ResourceType {Source, Target}
	private static Resource instanceModel;
	private static URI uri;
	
	private IbexOptions options;
	private TGGResourceHandler generator;
	private MODELGEN modelgen;
	private SYNC sync;
	private INITIAL_FWD fwd;
	private INITIAL_BWD bwd;
	private String typeOf;
	protected Resource source;
	protected Resource target;
	
	
	public ModelLoader(MODELGEN modelgen) {
		this.modelgen = modelgen;
		this.options = modelgen.getOptions();
		this.generator = this.modelgen.getResourceHandler();
		this.typeOf = "MODELGEN";
	}
	
	public ModelLoader(SYNC sync) {
		this.sync = sync;
		this.options = sync.getOptions();
		this.generator = this.sync.getResourceHandler();
		this.typeOf = "SYNC";
	}
	public ModelLoader(INITIAL_FWD fwd) {
		this.fwd = fwd;
		this.options = fwd.getOptions();
		this.generator = this.fwd.getResourceHandler();
		this.typeOf = "FWD";
	}
	public ModelLoader(INITIAL_BWD bwd) {
		this.bwd = bwd;
		this.options = bwd.getOptions();
		this.generator = this.bwd.getResourceHandler();
		this.typeOf = "BWD";
	}
	
	public TGGResourceHandler getResourceHandler() {
		return generator;
	}
	

	
	public void generateNewModel() {
		
		if (typeOf == "MODELGEN") {
			try {
				MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
		    	stop.setMaxRuleCount("HospitaltoAdministrationRule", 1);
				stop.setMaxElementCount(10);
				modelgen.setStopCriterion(stop);
				modelgen.run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (typeOf == "SYNC") {
			//do nothing
		}else if (typeOf == "FWD") {
			//do nothing
		}else if (typeOf == "BWD") {
			//do nothing
		}
	}
	/*
	public static Resource loadModelWithURI(ResourceType resourceType, String workingDirectory) {
		
		
		//Example for loading an instance model diagram
       // URI base = URI.createPlatformResourceURI("/", true);
       
        
        //Assuming your instance model is contained in a .xmi file, path can be adjusted accordingly
		//URI uri =  URI.createURI("/Hospital2Administration/instances/trg.xmi");
		
		/*
		switch (resourceType) {
		case Source:
			uri = URI.createURI(workingDirectory + "instances/src.xmi");
			break;
		case Target:
			uri = URI.createURI(workingDirectory + "instances/trg.xmi");
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
		
		*/
//	}
	
	public Resource loadModelWithResourceHandler(ResourceType resourceType) {
		
		switch (resourceType) {
		case Source:
			instanceModel = generator.getSourceResource();
			break;
		case Target:
			instanceModel = generator.getTargetResource();
			break;
		}
		
		
		return instanceModel;
	}
	
	public void CreateResourcesFromPath(String pathSrc, String pathTrg) throws IOException {
		
		if (typeOf == "MODELGEN") {
			if (pathSrc == " " | pathTrg == " ")
				return;
			source = generator.loadResource(pathSrc);
			target = generator.loadResource(pathTrg);
			
		}else if (typeOf == "SYNC"){
			if (pathSrc == " " | pathTrg == " ")
				return;
			source = generator.loadResource(pathSrc);
			target = generator.loadResource(pathTrg);
				
		}else if (typeOf == "FWD") {
			if (pathSrc == " ")
				return;
			source = generator.loadResource(pathSrc);
			target = generator.createResource(options.project.path() + "/instances/trg.xmi");	
			
		}else if (typeOf == "BWD") {
			if (pathTrg == " ")
				return;
			source = generator.createResource(options.project.path() + "/instances/src.xmi");
			target = generator.loadResource(pathTrg);
		}
		
	}
	
	public Resource getSource(){
		return source;
	}
	
	public Resource getTarget() {
		return target;
	}
	
	public String getTypeOf() {
		return typeOf;
	}
	
	public IbexOptions getOptions() {
		return options;
	}

}
