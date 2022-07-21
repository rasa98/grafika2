package xyz.marsavic.gfxlab.graphics3d.solids;

import com.google.common.collect.Lists;
import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Arrays;
import java.util.List;


public class TriangleMesh extends SolidBBox {

	private BVHSolid bvh;
	private SolidBBox[] tri;


	public TriangleMesh(List<Triangle> l, int amount) {
		this.tri = l.toArray(new Triangle[0]);
		setBBox(calculateBBox());
//		System.out.println("TriangleMesh bbox: "+getBBox().box.p()+"  "+ getBBox().box.q());
		bvh = BVHSolid.makeBVH(Lists.newArrayList(tri), amount);
	}

	
//	@Override
//	public Triangle.HitTriangle firstHit(Ray ray, double afterTime) {
//		Triangle.HitTriangle earliestHit = null;
//		for(SolidBBox t: tri){
//			Triangle.HitTriangle hit = (Triangle.HitTriangle) t.firstHit(ray, afterTime);
//			if(hit != null ){
//				if(earliestHit == null)
//					earliestHit = hit;
//				else if(earliestHit.t() > 0 && earliestHit.t() < hit.t()){
//					earliestHit = hit;
//				}
//			}
//		}
//		return earliestHit;
//	}
	@Override
	public Triangle.HitTriangle firstHit(Ray ray, double afterTime) {
//		Triangle.HitTriangle h = (Triangle.HitTriangle) bvh.getHit(ray, afterTime);
//		return root.getBestCollision(r, EPSILON, root.outliers, c);
		return (Triangle.HitTriangle) bvh.getHit(ray, afterTime);
	}

	@Override
	public BoundingBox calculateBBox() {
		// malo ne efikasno, sve tacke se vise puta dodaju u BoundingBox..
		BoundingBox bb = new BoundingBox();
		for(SolidBBox t: tri){
			SmoothTriangle st = (SmoothTriangle) t;
//			System.out.println("Triangle : "+st.points());
//			System.out.println("Triangle min: "+t.getBBox().box.p()+" max: "+t.getBBox().box.q());
//			System.out.println();
			bb = bb.addBBox(t.getBBox());
//			System.out.println("Triangle mesh min: "+bb.box.p()+" max: "+bb.box.q());
//			System.out.println();


		}

		return bb;
	}
}
