package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.solids.Triangle;
import xyz.marsavic.gfxlab.graphics3d.solids.TriangleMesh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;


public interface Scene {
	
	Collection<Body> bodies();
	Collection<Light> lights();
	Color backgroundColor();
	
	
	
	public static class Base implements Scene {
		protected List<Body> bodies = new ArrayList<>();
		protected List<Light> lights = new ArrayList<>();
		protected Color backgroundColor = Color.BLACK;
		
		
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

		public Map<String, TriangleMesh> readTriMeshFromFile(String filename, int amount){
			Map<String, TriangleMesh> objMeshes = new HashMap<>();
			try {
				InputStream is = new FileInputStream(filename);
				Map<String, List<Triangle>> nameToTriangles = Parser.getTriMeshes(is);
				for(var entry: nameToTriangles.entrySet()){
					objMeshes.put(entry.getKey(), new TriangleMesh(entry.getValue(), amount));
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			return objMeshes;
		}
		
	}
	
}
