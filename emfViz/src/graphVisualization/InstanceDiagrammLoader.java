package graphVisualization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import com.mxgraph.view.mxGraph;


public class InstanceDiagrammLoader extends DataLoader {
	
	private Resource instanceModel;

	public InstanceDiagrammLoader(Resource loadedResource) {
		super();
		
		instanceModel = loadedResource;
	}

	@Override
	protected void loadData() {
		
		List<EObject> content = instanceModel.getContents();
		for(EObject oPack : content) {				
			collectContentHierarchichal(oPack);						
		}

	}
	
	private void collectContentHierarchichal(EObject content) {
		
		nodes.add(new Node(content.toString(),content.eClass().getName(), "defaultNode"));
		
		for(EStructuralFeature f : ((EClassImpl)content.eClass()).getEAllStructuralFeatures()) {
			
			if(f instanceof EReference) {
					
				if(!edges.containsKey(content.toString())) {						
					ArrayList<Edge> outgoingEdges = new ArrayList<Edge>();
					edges.put(content.toString(), outgoingEdges);
				}
				
				if(!f.isMany()) {
					
					Edge visEdge = new Edge(f.getName(), "defaultEdge", content.toString(), ((EObject) content.eGet(f)).toString());
					
					EReference opp = ((EReference) f).getEOpposite();
					
					if(opp != null) {
						//edgeOpposites.put(content.toString()+f.getName()+content.eGet(f).toString(), content.eGet(f).toString()+opp.getName()+content.toString());
						//edgeOpposites.put(f.toString(), opp.toString());
						visEdge.setOppositeId(content.eGet(f).toString()+opp.getName()+content.toString());
					}
					edges.get(content.toString()).add(visEdge);
					

				}
				else {
												
				EList<EObject> values = (EList<EObject>) content.eGet(f);
				EReference opp = ((EReference) f).getEOpposite();
				
				for(EObject oMulti : values) {
					
					
					Edge visEdge = new Edge(f.getName(), "defaultEdge", content.toString(), oMulti.toString());
					
					if(opp != null) {
						visEdge.setOppositeId(oMulti.toString() + opp.getName() + content.toString());
					}
					
					edges.get(content.toString()).add(visEdge);

				}
			}	
		 }
		
	   }
		
		for(EObject containedContent : content.eContents()) {
			collectContentHierarchichal(containedContent);
		}
			
	}
	
	/*private void loadResource(String path) {
		
		URI base = URI.createPlatformResourceURI("/", true);
		URI uri =  URI.createURI(path);
		
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
		
		//rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		instanceModel = rs.createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
		try {
		instanceModel.load(null);
		}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
		
	}*/

}