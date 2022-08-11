package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;


public class Cone extends Solid {

	public static Cone UNIT = Cone.crh(Vec3.EY.mul(0.5), 0.5, 1);

	private final Vec3 yEnd;
	private final double r;



	private final Vec3 c;

	private final double h;

	//private final double upper;

	private final double lower;

	// transient
	private final double rSqr;
	private final double k;
	private final double h2;
	private final double upper;
	private final Vec3 top;


	private Cone(Vec3 top, double r, double h) {
		this.top = top;
		this.r = r;
		this.h = h;
		this.h2 = h / 2;

		this.c = top.add(Vec3.EY.mul(-h2));
		yEnd  = top;
		lower = c.y() - h2;
		upper = c.y() + h2;
		rSqr = r * r;
		k = r / h;

		bbox(calculateBBox());
	}


	public static Cone crh(Vec3 top, double r, double h){
		return new Cone(top, r, h);
	}
	
	
	public Vec3 yEnd() {
		return yEnd;
	}

	public Vec3 c() {
		return c;
	}
	
	public double r() {
		return r;
	}


	public double h() {
		return h;
	}


	private HitCone hitLid(double height, Ray ray) {
		Vec3 uppVec = Vec3.EZX.mul(c()).add(Vec3.EY.mul(height));;
		Vec3 norm = Vec3.EY.mul(-1);
		HalfSpace plane = HalfSpace.pn(uppVec, norm);
		HalfSpace.HitHalfSpace hit = plane.firstHit(ray, 0);
		if(hit != null){
			Vec3 p = ray.at(hit.t());
			Vec3 v = p.sub(uppVec);
			double d2 = v.dot(v);
			if(d2 <= rSqr)
				return new HitCone(ray, hit.t(), norm);
		}
		return null;
	}

	@Override
	public HitCone firstHit(Ray ray, double afterTime) {
		if(lower > ray.p().y()){
			HitCone hit = hitLid(lower, ray);
			if(hit != null)
				return hit;
		}
		double X0 = ray.p().x() - c.x();
		double Y0 = ray.p().y() ;//- c.y();
		double Z0 = ray.p().z() - c.z();
		double yk = Y0 - yEnd.y();
		double a = ray.d().x() * ray.d().x() + ray.d().z() * ray.d().z() - k * k * ray.d().y() * ray.d().y();
		double b = 2 * (ray.d().x() * X0 + ray.d().z() * Z0 - k * k * yk * ray.d().y());
		double c = X0 * X0 + Z0 * Z0 - k * k * yk * yk;


		double discriminant = b * b - 4 * a * c;
		double t;
		if(discriminant < 0){
			return null;
		}
		else if (discriminant == 0){
			t = -b / (2 * a);
		}else {
			double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
			double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
			if(t1 < 0 && t2 < 0)
				return null;
			t = (t1 < t2 || t2 < 0) && t1 > 0? t1 : t2;

		}
		if(afterTime > t)
			return null;
		Vec3 rayEnd = ray.at(t);
		Vec3 xz = Vec3.EZX.mul(rayEnd.sub(c()));
		Vec3 n = xz.add(Vec3.EY.mul(k * xz.length()));


		double yCoor = rayEnd.y();
		if(lower <= yCoor && yCoor <= upper)
			return new HitCone(ray, t, n);
		return null;



		/*if (mSqr > 0 ) {
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
		return null;*/

	}

	@Override
	protected BoundingBox calculateBBox() {
		Vec3 abc = Vec3.xyz(r, h2, r);
		return new BoundingBox(c.sub(abc), c.add(abc));
	}


	class HitCone extends Hit.RayT {
		private final Vec3 n;
		protected HitCone(Ray ray, double t, Vec3 n) {
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
