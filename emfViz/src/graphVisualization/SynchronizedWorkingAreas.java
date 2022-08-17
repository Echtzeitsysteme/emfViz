package graphVisualization;

import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.List;

public class SynchronizedWorkingAreas {

	private List<Area> activeAreas;

	public SynchronizedWorkingAreas(LinkedList<Area> areas) {
		activeAreas = areas;
	}

	public synchronized boolean allocateWorkingArea(Area threadArea) {

		// check if area collides with areas that are currently worked on by other
		// threads
		if (blocked(threadArea)) {

			return false;
		} else {
			activeAreas.add(threadArea);

		}

		return true;
	}

	public synchronized void deallocateWorkingArea(Area threadArea) {

		activeAreas.remove(threadArea);
		this.notifyAll();

	}

	private synchronized boolean blocked(Area threadArea) {
		for (Area a : activeAreas) {
			Area copy = (Area) a.clone();
			copy.intersect(threadArea);

			// if intersection is empty for all active areas new area can be savely worked
			// on since it has no collisions
			if (!copy.isEmpty())
				return true;
		}

		return false;
	}
}