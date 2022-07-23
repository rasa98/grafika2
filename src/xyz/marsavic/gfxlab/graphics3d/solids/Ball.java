package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.utils.Numeric;


public class Ball extends Solid {
	
	private final Vec3 c;
	private final double r;
	
	// transient
	private final double rSqr;

	
	
	private Ball(Vec3 c, double r) {
		this.c = c;
		this.r = r;
		rSqr = r * r;

		bbox(calculateBBox());
	}
	
	
	public static Ball cr(Vec3 c, double r) {
		return new Ball(c, r);
	}
	
	
	public Vec3 c() {
		return c;
	}
	
	
	public double r() {
		return r;
	}
	
	
	@Override
	public HitBall firstHit(Ray ray, double afterTime) {
		Vec3 e = c().sub(ray.p());                                // Vector from the ray origin to the ball center
		
		double dSqr = ray.d().lengthSquared();
		double l = e.dot(ray.d()) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr > 0) {
			double m = Math.sqrt(mSqr);
			if (l - m > afterTime) return new HitBall(ray, l - m);
			if (l + m > afterTime) return new HitBall(ray, l + m);
		}
		return null;
	}

	@Override
	public BoundingBox calculateBBox() {
		Vec3 rVec = Vec3.ONES.mul(r);
		return new BoundingBox(c.sub(rVec), c.add(rVec));
	}


	class HitBall extends Hit.RayT {
		
		protected HitBall(Ray ray, double t) {
			super(ray, t);
		}
		
		@Override
		public Vec3 n() {
			return ray().at(t()).sub(c());
		}
		
		
		@Override
		public Vec3 n_() {
			return n().div(r);
		}
		
		@Override
		public Vector uv() {
			Vec3 n_ = n_();
			return Vector.xy(
					Numeric.atan2T(n_.z(), n_.x()),
					2 * Numeric.asinT(n_.y()) + 0.5
			);
		}
		
	}
	
}
