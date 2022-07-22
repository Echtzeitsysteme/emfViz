package graphVisualization;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class VisContentAdapter extends EContentAdapter{
	
	private Visualizer vis;
	
	public VisContentAdapter(Visualizer vis) {
		this.vis = vis;
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		
		switch(notification.getEventType()) {
			case Notification.REMOVING_ADAPTER: handleRemoveAdapterNotification(notification); break;
			case Notification.REMOVE: handleRemoveNotification(notification); break;
			case Notification.ADD: handleAddNotification(notification); break;
			case Notification.ADD_MANY: break;
			case Notification.REMOVE_MANY: break;
			case Notification.SET: break;
	
		}
	}

	private void handleRemoveAdapterNotification(Notification notification) {
		//vis.removeNodeInGraph(new Node((EObject)notification.getOldValue()));
	}

	private void handleRemoveNotification(Notification notification) {
		System.out.println("Remove this: "+
				notification.getNotifier() +
				notification.getFeature() +
				notification.getOldValue());
		
		
		//EReference ref = (EReference) notification.getFeature();
		//vis.removeEdgeInGraph(new Edge((EObject)notification.getNotifier(), (EObject)notification.getOldValue(), ref));
	}

	private void handleAddNotification(Notification notification) {
		System.out.println(""+
		notification.getNotifier() +
		notification.getFeature() +
		notification.getNewValue());
		
		if(notification.getFeature() == null) {
			//neuer Knoten malen
			
			vis.insertNodeIntoGraph(new Node((EObject)notification.getNewValue()));
			vis.removeNodeInGraph(new Node((EObject)notification.getNewValue()));
			return;
		}
		
		EReference ref = (EReference) notification.getFeature();
		
		if(ref.isContainment())
		{
			// Kante + Knoten malen
			
			vis.insertEdgeIntoGraph(new Edge((EObject)notification.getNotifier(), (EObject)notification.getNewValue(), ref));
			vis.removeEdgeInGraph(new Edge((EObject)notification.getNotifier(), (EObject)notification.getNewValue(), ref));
		}
		else {
			// Neue Kante malen
			
		}
		
	}
	
	
}
