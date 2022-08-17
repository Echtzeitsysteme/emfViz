package graphVisualization;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;

public class InstanceDiagrammLoader extends DataLoader {

	private Resource instanceModel;
	private boolean internalEdgesOnly;

	public InstanceDiagrammLoader(Resource loadedResource, boolean internalEdgesOnly) {
		super();

		instanceModel = loadedResource;
		this.internalEdgesOnly = internalEdgesOnly;

		System.out.println("Instance Model: " + instanceModel.getURI().toString());
	}

	@Override
	public void loadData() {

		List<EObject> content = instanceModel.getContents();
		for (EObject oPack : content) {
			collectContentHierarchichal(oPack);
		}

	}

	private void collectContentHierarchichal(EObject content) {

		nodes.add(new Node("defaultNode", content));

		for (EStructuralFeature f : ((EClassImpl) content.eClass()).getEAllStructuralFeatures()) {

			if (f instanceof EReference ref) {

				if (!f.isMany()) {
					if (content.eGet(f) == null) {
						continue;
					}

					if (internalEdgesOnly) {
						if (!((EObject) content.eGet(f)).eResource().getURI().equals(instanceModel.getURI()))
							continue;
					}
					Edge visEdge = new Edge("defaultEdge", content, ((EObject) content.eGet(f)), ref);

					EReference opp = ((EReference) f).getEOpposite();

					if (opp != null) {
						visEdge.setOppositeId(content.eGet(f).toString() + opp.getName() + content.toString());
					}

					edges.put(visEdge.hashCode(), visEdge);

				} else {

					EList<EObject> values = (EList<EObject>) content.eGet(f);
					EReference opp = ((EReference) f).getEOpposite();

					for (EObject oMulti : values) {
						if (internalEdgesOnly) {
							if (!oMulti.eResource().getURI().equals(instanceModel.getURI()))
								continue;
						}

						Edge visEdge = new Edge("defaultEdge", content, oMulti, ref);

						if (opp != null) {
							visEdge.setOppositeId(oMulti.toString() + opp.getName() + content.toString());
						}

						edges.put(visEdge.hashCode(), visEdge);

					}
				}
			}

		}

		for (EObject containedContent : content.eContents()) {
			collectContentHierarchichal(containedContent);
		}
	}

	public Resource getInstanceModel() {
		return instanceModel;
	}

	public void setInstanceModel(Resource instanceModel) {
		this.instanceModel = instanceModel;
	}

}
