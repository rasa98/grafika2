package xyz.marsavic.gfxlab.graphics3d.scenes;


import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class teapotRoom extends Scene.Base {

	public teapotRoom() {
		addAllFrom(new OpenRoomRGTextured_GI());
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));

		Vec3 a = Vec3.xyz(0, 0, 0);
		Vec3 b = Vec3.xyz(-0.5, -0.5, -0.5);
		Vec3 c = Vec3.xyz(0.5, -0.5, -0.5);
		Vec3 d = Vec3.xyz(0, -0.5, -2);

		//"../obj/teapot.obj"
		List<Body> objBodies = new ArrayList<>();
		try {
//			String filename = "resources/obj/debug.obj";
			String filename = "resources/obj/teapot-low.obj";
			InputStream is = new FileInputStream(filename);
			Collection<List<Triangle>> ll = Parser.getTriMeshes(is);
			System.out.println(ll.size());
			for(List<Triangle> l: ll){
				objBodies.add(Body.uniform(new TriangleMesh(l, 5).transformed(Affine.scaling(0.04).andThen(Affine.rotationAboutX(-0.25).andThen(Affine.translation(Vec3.xyz(0, -0.67, -0.)))))));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		bodies.addAll(objBodies);
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz(0.5, 0.2,  -0.8), 0.3), Material.LIGHT)
//				Body.uniform(Triangle.p123(d, a, c), Material.matte(Color.hsb(0.57, 0.98, 0.99))),
//				Body.uniform(Triangle.p123(b, a, d), Material.matte(Color.hsb(0.07, 0.88, 0.79))),
				//Body.uniform(),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,  0.9,  0), Vec3.xyz( 0, -1,  0)), Material.LIGHT)
				,Body.uniform(new Box(Vec3.xyz(-0.8, -1, -0.9), Vec3.xyz(0.8, -0.65, 2.1)))





//				Body.uniform(Ball.cr(Vec3.xyz( 0.7,  0.6,  0.7), 0.4), Material.LIGHT),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.6,  0.6,  -1.9), 0.4), Material.LIGHT),
//				Body.uniform(Ball.cr(Vec3.xyz( -0.6,  0.6,  -1.9), 0.4), Material.LIGHT)
		);

	}
	
}
