package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.ArrayList;
import java.util.List;


public class TriangleMesh extends SolidBBox {

	private Triangle[] tri;


	public TriangleMesh(List<Triangle> l) {
		this.tri = l.toArray(new Triangle[0]);
		this.bbox = getBBox();
	}

	
	@Override
	public Triangle.HitTriangle firstHit(Ray ray, double afterTime) {
		if(bbox.hitRay(ray, afterTime))
			return realFirstHit(ray, afterTime);
		return null;
	}
	private Triangle.HitTriangle realFirstHit(Ray ray, double afterTime){
		Triangle.HitTriangle earliestHit = null;
		for(Triangle t: tri){
			Triangle.HitTriangle hit = t.firstHit(ray, afterTime);
			if(hit != null ){
				if(earliestHit == null)
					earliestHit = hit;
				else if(earliestHit.t() > 0 && earliestHit.t() < hit.t()){
					earliestHit = hit;
				}
			}
		}
		return earliestHit;
	}

	@Override
	public BoundingBox getBBox() {
		// malo ne efikasno, sve tacke se vise puta dodaju u BoundingBox..
		BoundingBox bb = new BoundingBox();
		for(Triangle t: tri){
			bb = bb.addBBox(t.bbox());
		}
		System.out.println("Triangle mesh min: "+bb.box.p()+" max: "+bb.box.q());
		return bb;
	}
}
