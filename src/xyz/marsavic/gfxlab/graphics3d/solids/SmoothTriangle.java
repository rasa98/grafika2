package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;


public class SmoothTriangle extends Triangle {

	private final Vec3 n1;
	private final Vec3 n2;
	private final Vec3 n3;



	private SmoothTriangle(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 n1, Vec3 n2, Vec3 n3) {
		super(p1, p2, p3);
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
	}
	
	
	public static SmoothTriangle p123(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 n1, Vec3 n2, Vec3 n3) { return new SmoothTriangle(p1, p2, p3, n1, n2, n3); }
	
	@Override
	protected HitTriangle getHit(double t, double afterTime, Ray ray, double u, double v) {
		return t > afterTime? new HitSmoothTriangle(ray, t, u, v) : null;
	}
	
	
	class HitSmoothTriangle extends HitTriangle {

		private final double u;

		private final double v;

		protected HitSmoothTriangle(Ray ray, double t, double u, double v) {
			super(ray, t);
			this.u = u;
			this.v = v;
		}
		
		@Override
		public Vec3 n() {
			return n2.mul(u).add(n3.mul(v)).add(n1.mul((1 - u - v)));
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
