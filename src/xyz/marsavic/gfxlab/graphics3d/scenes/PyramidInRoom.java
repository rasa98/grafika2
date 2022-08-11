package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.BSDF;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Scene;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.graphics3d.solids.Triangle;

import java.util.Collections;


public class PyramidInRoom extends Scene.Base {

	public PyramidInRoom() {
		addAllFrom(OpenRoomRGTextured_GI.room);
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));

		Vec3 a = Vec3.xyz(0, 0, 0);
		Vec3 b = Vec3.xyz(-0.5, -0.5, -0.5);
		Vec3 c = Vec3.xyz(0.5, -0.5, -0.5);
		Vec3 d = Vec3.xyz(0, -0.5, -2);


		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz(-0.2, -0.5,  0.0), 0.3), glass),
				Body.uniform(Triangle.p123(d, a, c), Material.matte(Color.hsb(0.57, 0.98, 0.99))),
				Body.uniform(Triangle.p123(b, a, d), Material.matte(Color.hsb(0.07, 0.88, 0.79))),
//				Body.textured(HalfSpace.pn(Vec3.xyz( 0,  0.9,  0), Vec3.xyz( 0, -1,  0)), Material.LIGHT),



				Body.uniform(Ball.cr(Vec3.xyz( 0.7,  0.6,  0.7), 0.4), Material.LIGHT),
				Body.uniform(Ball.cr(Vec3.xyz( 0.6,  0.6,  -1.9), 0.4), Material.LIGHT),
				Body.uniform(Ball.cr(Vec3.xyz( -0.6,  0.6,  -1.9), 0.4), Material.LIGHT)
		);
	}
	
}
