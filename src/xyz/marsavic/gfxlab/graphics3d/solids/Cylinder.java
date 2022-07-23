package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;


public class Cylinder extends Solid {

	private final Vec3 c;
	private final double r;

	private final double h2;

	private final double upper;

	private final double lower;

	// transient
	private final double rSqr;


	private Cylinder(Vec3 c, double r, double h) {
		this.c = c;
		this.r = r;
		this.h2 = h / 2;
		upper = c.y() + h2;
		lower = c.y() - h2;
		rSqr = r * r;

		bbox(calculateBBox());
	}


	public static Cylinder crh(Vec3 c, double r, double h){
		return new Cylinder(c, r, h);
	}
	
	
	public Vec3 c() {
		return c;
	}
	
	
	public double r() {
		return r;
	}


	private HitCyl hitLid(double height, Ray ray) {
		Vec3 uppVec = Vec3.EZX.mul(c()).add(Vec3.EY.mul(height));
		Vec3 norm = upper == height? Vec3.EY: Vec3.EY.mul(-1);
		HalfSpace plane = HalfSpace.pn(uppVec, norm);
		HalfSpace.HitHalfSpace hit = plane.firstHit(ray, 0);
		if(hit != null){
			Vec3 p = ray.at(hit.t());
			Vec3 v = p.sub(uppVec);
			double d2 = v.dot(v);
			if(d2 <= rSqr)
				return new HitCyl(ray, hit.t(), norm);
		}
		return null;
	}
	
	@Override
	public HitCyl firstHit(Ray ray, double afterTime) {
		if(upper < ray.p().y()){
			HitCyl hit = hitLid(upper, ray);
			if(hit != null)
				return hit;
		}else if(lower > ray.p().y()){
			HitCyl hit = hitLid(lower, ray);
			if(hit != null)
				return hit;
		}

		Vec3 c = this.c.mul(Vec3.EZX);
		Vec3 p = ray.p().mul(Vec3.EZX);
		Vec3 d = ray.d().mul(Vec3.EZX);

		Vec3 e = c.sub(p);                                // Vector from the ray origin to the ball center

		double dSqr = d.lengthSquared();
		double l = e.dot(d) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;


		if (mSqr > 0 ) {
			double m = Math.sqrt(mSqr);
			double t1 = (l - m);
			double t2 = (l + m);
			if (t1 > afterTime) {
				double yCoor = ray.at(t1).y();
				if(lower <= yCoor && yCoor <= upper)
					return new HitCyl(ray, t1, ray.at(t1).sub(c()).mul(Vec3.EZX));
			}
			if (t2 > afterTime) {
				double yCoor = ray.at(t2).y();
				if(lower <= yCoor && yCoor <= upper)
					return new HitCyl(ray, t2, ray.at(t2).sub(c()).mul(Vec3.EZX));
			}
		}
		return null;

	}

	@Override
	protected BoundingBox calculateBBox() {
		Vec3 abc = Vec3.xyz(r, h2, r);
		return new BoundingBox(c.sub(abc), c.add(abc));
	}


	class HitCyl extends Hit.RayT {
		private final Vec3 n;
		protected HitCyl(Ray ray, double t, Vec3 n) {
			super(ray, t);
			this.n = n;
		}

		@Override
		public Vec3 n() {
			return n;
		}
		
		
		/*@Override
		public Vec3 n_() {
			return n().div(r);
		}*/
		
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
