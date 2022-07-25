package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public interface Scene {
	
	Collection<Body> bodies();
	Collection<Light> lights();
	Color backgroundColor();

	double getT(double x);
	
	
	public static class Base implements Scene {
		protected List<Body> bodies = new ArrayList<>();
		protected List<Light> lights = new ArrayList<>();
		protected Color backgroundColor = Color.BLACK;

		protected double t1;
		protected double t2;
		
		
		@Override
		public Collection<Body> bodies() {
			return bodies;
		}
		
		@Override
		public Collection<Light> lights() {
			return lights;
		}
		
		@Override
		public Color backgroundColor() {
			return backgroundColor;
		}

		@Override
		public double getT(double x) {
			return t1 + x*x * (t2 - t1);
		}


		public void addBodiesFrom(Scene other) {
			bodies.addAll(other.bodies());
		}
		
		public void addLightsFrom(Scene other) {
			lights.addAll(other.lights());
		}
		
		public void addAllFrom(Scene other) {
			addBodiesFrom(other);
			addLightsFrom(other);
		}
		
	}
	
}
