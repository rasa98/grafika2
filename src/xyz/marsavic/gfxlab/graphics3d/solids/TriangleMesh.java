package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.*;


public class TriangleMesh extends Solid {

	private BVHSolid bvh;
	private Solid[] tri;


	public TriangleMesh(Collection<Solid> l, int amount) {
		this.tri = l.toArray(new Triangle[0]);
//				bbox();
		bvh = BVHSolid.makeBVH(l, amount);
	}

	@Override
	public Triangle.HitTriangle firstHit(Ray ray, double afterTime) {
		return (Triangle.HitTriangle) bvh.getHit(ray, afterTime);
	}

	@Override
	public BoundingBox calculateBBox() {
		BoundingBox bb = new BoundingBox();
		for(Solid t: tri){
			bb = bb.addBBox(t.bbox());
		}

		return bb;
	}
}
