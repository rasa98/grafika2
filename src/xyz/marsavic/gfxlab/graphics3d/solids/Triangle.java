package xyz.marsavic.gfxlab.graphics3d.solids;

import com.google.common.collect.Lists;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.List;


public class Triangle extends SolidBBox {

	private final double EPSILON = 1e-9;
	private final Vec3 p1;
	private final Vec3 p2;
	private final Vec3 p3;
	private final Vec3 e1;
	private final Vec3 e2;
	private final Vec3 n;


	protected Triangle(Vec3 p1, Vec3 p2, Vec3 p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;

		e1 = p2.sub(p1);
		e2 = p3.sub(p1);
		n = e1.cross(e2);

		setBBox(calculateBBox());
	}
	
	
	public static Triangle p123(Vec3 p1, Vec3 p2, Vec3 p3) {

		return new Triangle(p1, p2, p3);
	}

	public List<Vec3> points(){
		return Lists.newArrayList(new Vec3[]{p1, p2, p3});
	}
	@Override
	public HitTriangle firstHit(Ray ray, double afterTime) {
		Vec3 dir_cross_e2 = ray.d().cross(e2);
		double det = e1.dot(dir_cross_e2);
		if(Math.abs(det) < EPSILON)
			return null;

		// p1-p3 edge miss
		double f = 1.0 / det;
		Vec3 p1_to_origin = ray.p().sub(p1);
		double u = f * p1_to_origin.dot(dir_cross_e2);
		if(u < 0 || u > 1)
			return null;

		// p1-p2 edge miss
		// p2-p3 edge miss
		Vec3 origin_cross_e1 = p1_to_origin.cross(e1);
		double v = f * ray.d().dot(origin_cross_e1);
		if(v < 0 || (u + v) > 1)
			return null;

		// Strike
		double t = f * e2.dot(origin_cross_e1);
		return getHit(t, afterTime, ray, u, v);
	}

	@Override
	protected BoundingBox calculateBBox() {
		BoundingBox bb = new BoundingBox();
//		System.out.println();
//		System.out.println("Triangle : "+ points());
//		System.out.println("------bb (inside triangle)1: "+bb.box.p()+"  "+bb.box.q());
		bb = bb.addPoint(p1);
//		System.out.println("------bb (inside triangle)2: "+bb.box.p()+"  "+bb.box.q());
		bb = bb.addPoint(p2);
//		System.out.println("------bb (inside triangle)3: "+bb.box.p()+"  "+bb.box.q());
		bb = bb.addPoint(p3);
//		System.out.println("------bb (inside triangle)4: "+bb.box.p()+"  "+bb.box.q());
		return bb;
	}

	protected HitTriangle getHit(double t, double afterTime, Ray ray, double u, double v) {
		return t > afterTime? new HitTriangle(ray, t) : null;
	}


	class HitTriangle extends Hit.RayT {
		
		protected HitTriangle(Ray ray, double t) {
			super(ray, t);
		}
		
		@Override
		public Vec3 n() {
			return n;
		}
		
		/*@Override
		public Vector uv() {
			Vec3 n_ = n_();
			return Vector.xy(
					Numeric.atan2T(n_.z(), n_.x()),
					2 * Numeric.asinT(n_.y()) + 0.5
			);
		}*/
		
	}
	
}
