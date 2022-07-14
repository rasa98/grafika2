package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.utils.Numeric;


public class Triangle implements Solid {

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
	}
	
	
	public static Triangle p123(Vec3 p1, Vec3 p2, Vec3 p3) { return new Triangle(p1, p2, p3); }
	
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
