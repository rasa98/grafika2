package xyz.marsavic.gfxlab.graphics3d;


import xyz.marsavic.gfxlab.Vec3;

import java.util.Collection;
import java.util.List;


public interface Collider {
	
	/**
	 * Returns the first intersection with a positive time of the ray r with a set of bodies.
	 * Returned collision time is "ray-time", that is, it depends on the speed of the ray (r.d).
	 * If there is no collision, null is returned.
	 */
	Collision collide(Ray r);

	default Collision collide(Ray r, double t){
		return collide(r);
	};
	
	
	default boolean collidesIn01(Ray r) {
		Collision collision = collide(r);
		return (collision != null) && (collision.hit().t() < 1);
	}


	record Collision  (
			Hit hit, Body body
	) {
		public static Collision empty(){
			return new Collision(new Hit.Data(Double.MAX_VALUE, null), null);
		}
	}
	
	
	
	
	public static class BruteForce implements Collider {
		
		private static final double EPSILON = 1e-9;
		
		private final Body[] bodies; // Using an array for efficiency.
		
		
		public BruteForce(Collection<Body> bodies) {
			this.bodies = bodies.toArray(new Body[0]);
		}
		
		
		@Override
		public Collision collide(Ray r) {
			Hit minHit = null;
			double minHitT = Double.POSITIVE_INFINITY;
			Body minBody = null;
			
			for (Body body : bodies) {
				Hit hit = body.solid().firstHit(r, EPSILON);
				if ((hit != null) && (hit.t() < minHitT)) {
					minHitT = hit.t();
					minHit = hit;
					minBody = body;
				}
			}
			
			return minBody == null ? null : new Collision(minHit, minBody);
		}
		
		
		@Override
		public boolean collidesIn01(Ray r) {
			for (Body body : bodies) {
				Hit hit = body.solid().firstHit(r, EPSILON);
				if ((hit != null) && (hit.t() < 1)) {
					return true;
				}
			}
			return false;
		}
		
	}

	public static class BvhCollider implements Collider {

		private static final double EPSILON = 1e-9;

		private final BVH root;


		public BvhCollider(Collection<Body> bodies) {
			this.root = BVH.makeBVH(bodies, 3);
		}


		@Override
		public Collision collide(Ray r) {
			return collide(r, 0);
		}

		@Override
		public Collision collide(Ray r, double t) {
//			Collision c = root.getCollision(r, EPSILON);
//			return root.getBestCollision(r, EPSILON, root.outliers, c);
			return root.getCollision(r, EPSILON);


//			Hit minHit = null;
//			double minHitT = Double.POSITIVE_INFINITY;
//			Body minBody = null;
//
//			for (Body body : bodies) {
//				Hit hit = body.solid().firstHit(r, EPSILON);
//				if ((hit != null) && (hit.t() < minHitT)) {
//					minHitT = hit.t();
//					minHit = hit;
//					minBody = body;
//				}
//			}
//
//			return minBody == null ? null : new Collision(minHit, minBody);
		}


		@Override
		public boolean collidesIn01(Ray r) {
			return root.getCollisionIn01(r, EPSILON);
//			for (Body body : bodies) {
//				Hit hit = body.solid().firstHit(r, EPSILON);
//				if ((hit != null) && (hit.t() < 1)) {
//					return true;
//				}
//			}
//			return false;
		}

	}
	
}
