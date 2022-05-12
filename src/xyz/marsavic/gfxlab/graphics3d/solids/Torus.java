package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;


public class Torus implements Solid {

	private final double A;
	private final Vec3 O;

	private final double r;

	static LaguerreSolver laguerreSolver = new LaguerreSolver();


	private Torus(Vec3 O, double A, double r) {
		this.O = O;
		this.A = A;
		this.r = r;
	}


	public static Torus crh(Vec3 origin, double A,double r){
		return new Torus(origin, A, r);
	}
	
	public double r() {
		return r;
	}


	@Override
	public HitTorus firstHit(Ray ray, double afterTime) {
		Vec3 d = ray.d();
		double px = ray.p().x() - O.x(), dx = d.x();
		double pz = ray.p().z() - O.z(), dz = d.z();
		double py = ray.p().y() - O.y(), dy = d.y();

		double G = 4*A*A*(dx*dx + dz*dz);
		double H = 8*A*A*(px*dx + pz*dz);
		double I = 4*A*A*(px*px + pz*pz);
		double J = dx*dx + dy*dy + dz*dz;
		double K = 2 * (px*dx + py*dy + pz*dz);
		double L = px*px + py*py + pz*pz + A*A - r*r;

		double t = solveRealQuarticRoots(J*J, 2*J*K, (2*J*L + K*K - G), (2*K*L - H), (L*L - I));

		if (t != Double.MAX_VALUE)
			return new HitTorus(ray, t);
		return null;
	}

	class HitTorus extends Hit.RayT {

		protected HitTorus(Ray ray, double t) {
			super(ray, t);
		}

		@Override
		public Vec3 n() {
			Vec3 P = ray().at(t());
			Vec3 Pxz = P.mul(Vec3.EZX);

			Vec3 Q = Pxz.normalized_().mul(A);

			return P.sub(Q);
		}
		
		
		@Override
		public Vec3 n_() {
			return n().div(r);
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


	public static double solveRealQuarticRoots(double a, double b, double c, double d, double e) {
		Complex[] root = laguerreSolver.solveAllComplex(new double[]{ e, d, c, b, a}, 0, 100);
		double res = Double.MAX_VALUE;
		for(Complex cplx : root) {
			double comp = cplx.getImaginary();
			if(comp <= 1e-9 && comp >= -1e-9){
				double real = cplx.getReal();
				if(real >= 0 && real < res)
					res = real;
			}
		}
		return res;
	}
}
