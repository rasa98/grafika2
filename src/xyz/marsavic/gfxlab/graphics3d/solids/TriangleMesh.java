package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.ArrayList;
import java.util.List;


public class TriangleMesh implements Solid {

	private Triangle[] tri;


	public TriangleMesh(List<Triangle> l) {
		this.tri = l.toArray(new Triangle[0]);
	}
	
	@Override
	public Triangle.HitTriangle firstHit(Ray ray, double afterTime) {
		Triangle.HitTriangle earliestHit = null;
		for(Triangle t: tri){
			Triangle.HitTriangle hit = t.firstHit(ray, afterTime);
			if(hit != null ){
				if(earliestHit == null)
					earliestHit = hit;
				else if(earliestHit.t() > 0 && earliestHit.t() < hit.t()){
					earliestHit = hit;
				}
			}
		}
		return earliestHit;
	}
}
