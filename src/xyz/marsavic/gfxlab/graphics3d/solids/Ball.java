package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.functions.interfaces.Function1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Affine;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.utils.Numeric;


public class Ball implements Solid {
	
	private final Vec3 c;
	private final double r;

	private Function1<Affine, Double> f1;// = t -> Affine.IDENTITY;
	
	// transient
	private final double rSqr;

	
	
	private Ball(Vec3 c, double r) {
		this.c = c;
		this.r = r;
		rSqr = r * r;
	}

	private Ball(Vec3 c, double r, Function1<Affine, Double> f1) {
		this.c = c;
		this.r = r;
		rSqr = r * r;
		this.f1 = f1;
	}
	
	
	public static Ball cr(Vec3 c, double r) {
		return new Ball(c, r);
	}

	public static Ball cr(Vec3 c, double r, Function1<Affine, Double> f1) {
		return new Ball(c, r, f1);
	}


	
	public Vec3 c() {
		return c;
	}

	public Vec3 c(double t) {
		if(f1 == null)
			return c();
		return f1.at(t).applyTo(c());
	}


	public double r() {	return r; }
	
	
	@Override
	public HitBall firstHit(Ray ray, double t, double afterTime) {
		Vec3 e = c(t).sub(ray.p());                                // Vector from the ray origin to the ball center
		
		double dSqr = ray.d().lengthSquared();
		double l = e.dot(ray.d()) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr > 0) {
			double m = Math.sqrt(mSqr);
			if (l - m > afterTime) return new HitBall(ray, l - m, t);
			if (l + m > afterTime) return new HitBall(ray, l + m, t);
		}
		return null;
	}
	
	
	class HitBall extends Hit.RayT {

		double myT;
		protected HitBall(Ray ray, double t, double tt) {
			super(ray, t);
			myT = tt;
		}
		
		@Override
		public Vec3 n() {
			return ray().at(t()).sub(c(myT));
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
