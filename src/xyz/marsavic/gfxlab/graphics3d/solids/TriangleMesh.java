package xyz.marsavic.gfxlab.graphics3d.solids;

import com.google.common.collect.Lists;
import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.List;


public class TriangleMesh extends Solid {

	private BVHSolid bvh;
	private Solid[] tri;


	public TriangleMesh(List<Triangle> l, int amount) {
		this.tri = l.toArray(new Triangle[0]);
		bbox(calculateBBox());
		bvh = BVHSolid.makeBVH(Lists.newArrayList(tri), amount);
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
