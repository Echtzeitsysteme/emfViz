package graphVisualization;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.locationtech.jts.index.strtree.STRtree;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class Grid {
	public static final double EPS = 0.0001;
	private GeometryFactory geoFac;
	private STRtree rTree;
	private Envelope bounds;
	double margin = 0.1;

	private double maxDistFactorX = 3;
	private double maxDistFactorY = 3;

	public Grid(GeometryFactory geoFac, Envelope bounds) {
		this.geoFac = geoFac;
		rTree = new STRtree(4);
		this.bounds = bounds;
	}

	public double getCostForArea(Envelope envelope) {
		if(rTree.isEmpty())
			return 0.0;

		double cost = 0.0;

		@SuppressWarnings("unchecked")
		List<Object> query = rTree.query(envelope);
		for (Object object : query) {
			GridObject go = (GridObject) object;
			if (go.object() instanceof mxCell cell) {
				if (cell.isVertex()) {
					return Double.MAX_VALUE;
				} else {
					cost += go.cost();
				}
			} else if (go.object() instanceof mxRectangle rect) {
				return Double.MAX_VALUE;
			} else {
				// Unknown Type
				return Double.MAX_VALUE;
			}
		}

		return cost;
	}
	
	@SuppressWarnings({ "unchecked"})
	public synchronized void insertIntoTree(GridObject go) {
		if(rTree.isEmpty()) {
			rTree.insert(go.envolope(), go);
		} else {
			STRtree tree = new STRtree();
			flattenTreeItems(rTree.itemsTree().stream())
			.forEach(itm -> tree.insert(((GridObject)itm).envolope(), itm));
			tree.insert(go.envolope(), go);
			rTree = tree;
			rTree.build();
		}
	}
	
	@SuppressWarnings({ "unchecked"})
	public synchronized void insertIntoTree(List<GridObject> gos) {
		if(rTree.isEmpty()) {
			gos.forEach(go -> rTree.insert(go.envolope(), go));
		} else {
			STRtree tree = new STRtree();
			flattenTreeItems(rTree.itemsTree().stream())
			.forEach(itm -> tree.insert(((GridObject)itm).envolope(), itm));
			gos.forEach(go -> tree.insert(go.envolope(), go));
			rTree = tree;
			rTree.build();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Object> flattenTreeItems(Stream<Object> iStream) {
		return iStream.flatMap(o -> {
			if(o instanceof Collection oC) {
				return flattenTreeItems(oC.stream());
			} else {
				return Stream.of(o);
			}
		});
	}

	public mxPoint placeInFreeGridPosition(Object object, Point2D.Double pos, double width, double height) {
		double maxDistX = maxDistFactorX * width;
		double maxDistY = maxDistFactorY * height;
		double marginX = margin * width / 2.0;
		double marginY = margin * height / 2.0;
		
		Envelope env = new Envelope(pos.x - width / 2, pos.x + width / 2, pos.y - height / 2, pos.y + height / 2);
		if (getCostForArea(env) <= 0) {
			Envelope insert = Quadtree.ensureExtent(env, margin);
			GridObject go = new GridObject(object, pos, insert, Double.MAX_VALUE);
			insertIntoTree(go);
			return new mxPoint(pos.x, pos.y);
		} else {
			// Search in areas around the initial estimate within a certain radius
			List<Envelope> queryEnvs = new LinkedList<>();
			for (double x = pos.x - maxDistX; x <= pos.x + maxDistX; x += width + marginX) {
				for (double y = pos.y - maxDistY; x <= pos.y + maxDistY; y += height + marginY) {
					if(Math.abs(x-pos.x)<=EPS && Math.abs(y-pos.y)<=EPS)
						continue;
					
					queryEnvs.add(new Envelope(x - width / 2, x + width / 2, y - height / 2, y + height / 2));
				}
			}
			
			Optional<Envelope> candidate = queryEnvs.parallelStream()
					.filter(envelope -> getCostForArea(envelope) <= 0)
					.findAny();
			
			if(candidate.isPresent()) {
				Envelope envelope = candidate.get();
				Envelope insert = Quadtree.ensureExtent(envelope, margin);
				Point2D.Double center = new Point2D.Double(insert.centre().x, insert.centre().y);
				GridObject go = new GridObject(object, center, insert, Double.MAX_VALUE);
				insertIntoTree(go);
				return new mxPoint(center.x, center.y);
			} else {
				// Fall-back solution
				Envelope insert = Quadtree.ensureExtent(env, margin);
				GridObject go = new GridObject(object, pos, insert, Double.MAX_VALUE);
				insertIntoTree(go);
				return new mxPoint(pos.x, pos.y);
			}
		}

	}

}

record GridObject(Object object, Point2D.Double location, Envelope envolope, double cost) {}
