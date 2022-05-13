package xyz.marsavic.gfxlab.graphics3d.raytracers;

import xyz.marsavic.functions.interfaces.Function1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Collection;

/**
 *  A very simple raytracer that renders only the distance from the camera plane.
 */
public class Sonar extends RayTracer {
	
	public Sonar(Scene scene, Function1<Collider, Collection<Body>> colliderFactory, Camera camera) {
		super(scene, colliderFactory, camera);
	}
	
	
	@Override
	protected Color sample(Ray ray, double t) {
		Collider.Collision collision = collider().collide(ray);
		return collision == null ?
				scene().backgroundColor() :
				Color.gray(1 / (1 + collision.hit().t()));
	}
	
}
