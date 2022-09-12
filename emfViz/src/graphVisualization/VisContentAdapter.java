package graphVisualization;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class VisContentAdapter extends EContentAdapter {

	private Resource resource;
	private boolean cascadingNotifications = false;
	private Visualizer vis;

	private Collection<Notification> notifications = new LinkedList<>();
	private Set<Object> removedObjects = new HashSet<>();
	private Set<Object> addedObjects = new HashSet<>();
	
	private int xPos;
	private int yPos;

	public VisContentAdapter(Resource r, Visualizer vis) {
		this.vis = vis;
		resource = r;
		r.eAdapters().add(this);
//		if(r instanceof SmartEMFResource smartResource) {
//			cascadingNotifications = smartResource.getCascade();
//		}
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		notifications.add(notification);
		System.out.println(notification);

		preProcessNotification(notification);
//		vis.runLayout();
	}

	// here we search for newly added elements that already exist in our graph
	// hence, if they are deleted and readded, we do not have to remove them from
	// our graph
	private void preProcessNotification(Notification notification) {
		switch (notification.getEventType()) {
		case Notification.REMOVING_ADAPTER:
			removeObject(notification.getOldValue());
			break;
			
		case Notification.REMOVE:
			if (notification.getFeature() == null)
				removeObject(notification.getOldValue());
			else if (notification.getFeature() instanceof EReference ref) {
				if (ref.isContainment())
					removeObject(notification.getOldValue());
			}
			break;
			
		case Notification.ADD:
			if (notification.getFeature() == null)
				addObject(notification.getNewValue());
			else if (notification.getFeature() instanceof EReference ref) {
				if (ref.isContainment())
					addObject(notification.getNewValue());
			}
			break;
			
		case Notification.ADD_MANY:
			throw new IllegalArgumentException("Add Many notifications are not supported yet");
			
		case Notification.REMOVE_MANY:
			throw new IllegalArgumentException("Remove Many notifications are not supported yet");
			
		case Notification.SET:
			if (notification.getFeature() instanceof EReference ref) {
				if(ref.isContainment()) {
					if (notification.getNewValue() == null)
						removeObject(notification.getOldValue());
					else
						addObject(notification.getNewValue());
				}
				return;
			}
			break;
		}
		
		
	}

	public void addObject(Object obj) {
		// if object was removed before, we do not want to neither remove nor read it
		if (removedObjects.remove(obj)) {
			return;
		}

		addedObjects.add(obj);
		
		// if cascading notifications is not activated, we have to explore the emf hierarchy 
		if(cascadingNotifications)
			return;
		
		var eObj = (InternalEObject) obj;
		for(var content : eObj.eContents()) {
			Notification newNotification = new ENotificationImpl((InternalEObject) eObj, Notification.ADD, content.eContainingFeature(), null, content);
			notifyChanged(newNotification);
		}
		for(var content : eObj.eCrossReferences()) {
			Notification newNotification = new ENotificationImpl((InternalEObject) eObj, Notification.ADD, content.eContainingFeature(), null, content);
			notifications.add(newNotification);
		}
	}

	public void removeObject(Object obj) {
		if (!addedObjects.remove(obj)) {
			removedObjects.add(obj);
		}
	}

	private void resetCache() {
		notifications.clear();
		addedObjects.clear();
		removedObjects.clear();
	}

	public boolean isNew(Object obj) {
		return addedObjects.remove(obj);
	}

	public boolean isDeleted(Object obj) {
		return removedObjects.remove(obj);
	}

	// process all notifications seen so far
	public void processNotifications() {
		for (var notification : notifications) {
			switch (notification.getEventType()) {
			case Notification.ADD:
				handleAddNotification(notification);
				break;
			case Notification.ADD_MANY:
				handleAddManyNotificiations(notification);
				break;
			case Notification.REMOVE:
				handleRemoveNotification(notification);
				break;
			case Notification.REMOVE_MANY:
				handleRemoveManyNotificiations(notification);
				break;
			case Notification.REMOVING_ADAPTER:
				handleRemoveAdapterNotification(notification);
				break;
			case Notification.SET:
				handleSetNotification(notification);
				break;
			}
			
		}

		vis.runIncrementalLayout();
		// clear cache for next iteration
		resetCache();
	}

	// vis.removeNodeInGraph(new Node((EObject)notification.getNewValue()));
	// vis.removeEdgeInGraph(new Edge((EObject)notification.getNotifier(),
	// (EObject)notification.getNewValue(), ref));

	private void handleAddNotification(Notification notification) {
		if (notification.getFeature() == null) {
			// the node was added directly into the resource, which means that there is no
			// new edge
			if(isNew(notification.getNewValue()))
				vis.insertNewNodeIntoGraph(new Node((EObject) notification.getNewValue()), xPos, yPos);
			vis.graph.refresh();
			vis.graphComponent.refresh();
				xPos = 20;
				yPos = 20;
			return;
		}

		EReference ref = (EReference) notification.getFeature();
		if (ref.isContainment()) {
			// if reference is a containment then the both the edge and the target node are new
			if(isNew(notification.getNewValue())) //JL ADDED
				vis.insertNodeIntoGraph(new Node((EObject) notification.getNewValue()));
			vis.insertEdgeIntoGraph(new Edge((EObject) notification.getNotifier(), (EObject) notification.getNewValue(), ref));
		} else {
			// else the reference is only a cross-reference and both source and target
			// already exist so we only add the edge
			vis.insertEdgeIntoGraph(
					new Edge((EObject) notification.getNotifier(), (EObject) notification.getNewValue(), ref));
		}
	}

	private void handleAddManyNotificiations(Notification notification) {
		throw new IllegalArgumentException("Add Many notifications are not supported yet");
	}

	private void handleRemoveNotification(Notification notification) {
		// if the node was removed from the resource but will be readded later -> don't
		// remove it
		if (notification.getFeature() == null) {
			//if (isDeleted(notification.getOldValue()))
				vis.removeNodeInGraph(new Node((EObject) notification.getOldValue()));
			return;
		}
		EReference ref = (EReference) notification.getFeature();
		vis.removeEdgeInGraph(new Edge((EObject) notification.getNotifier(), (EObject) notification.getOldValue(), ref));
	}

	private void handleRemoveManyNotificiations(Notification notification) {
		throw new IllegalArgumentException("Remove Many notifications are no handled yet");
	}

	private void handleRemoveAdapterNotification(Notification notification) {
		if (isDeleted(notification.getOldValue()))
			vis.removeNodeInGraph(new Node((EObject) notification.getNotifier()));
		
	}

	private void handleSetNotification(Notification notification) {
		if (notification.getFeature() instanceof EReference ref) {
			if (notification.getNewValue() == null)
				handleRemoveNotification(notification);
			else
				handleAddNotification(notification);
			return;
		}

		if (notification.getFeature() instanceof EAttribute attr) {
			/*if(notification.getOldValue().equals(notification.getNewValue())) {
				vis.removeNodeInGraph(new Node((EObject) notification.getNotifier()));
				vis.insertNodeIntoGraph(new Node((EObject) notification.getNotifier()));
			}*/
			return;
			//throw new UnsupportedOperationException("Attribute changes are not yet implemented");
		}

		throw new UnsupportedOperationException(
				notification.getFeature() + " is not yet supported by VisContentAdapter");
	}
	
	/* set position of where the last mouse click appeared */
	public void setPosition(int x, int y) {
		xPos = x;
		yPos = y;
	}
}
