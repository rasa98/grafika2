package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Cone;
import xyz.marsavic.gfxlab.graphics3d.solids.Cylinder;

import java.util.Collections;


public class TestGICylinderCone extends Scene.Base {

	public TestGICylinderCone() {
		addAllFrom(OpenRoomRGTextured_GI.room);
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz(-0.2, -0.5,  0.0), 0.3), glass),
				Body.uniform(Ball.cr(Vec3.xyz( 0.5,  0.5,  0.5), 0.4), Material.mirror()),
				Body.uniform(Ball.cr(Vec3.xyz( -0.1,  0.1,  0.7), 0.25), Material.LIGHT),
				Body.uniform(Ball.cr(Vec3.xyz( 0.5,  0.5,  -2.5), 0.4), Material.LIGHT),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.6,  0.5,  0.7), 0.5), Material.LIGHT),
//				Body.uniform(Cone.crh(Vec3.xyz( 0.5,  0.5,  0.5), 0.4, 0.4), Material.mirror()),
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0.1, -0.6, -1.8), 0.7, 0.5), Material.mirror())
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.matte(Color.hsb(0.6, 0.79, 0.9)))
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.mirror())
				Body.uniform(Cone.crh(Vec3.xyz( -0.1, -0.36, -0.5), 0.43, 0.4).transformed(Affine.rotationAboutZ(-0.05).andThen(Affine.rotationAboutX(0.1))), Material.matte(0.89))
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0.1, 0.1, -.73), 0.14, 0.4), Material.matte(Color.hsb(0.88, 0.88, 0.99))),
//				Body.uniform(Ball.cr(Vec3.xyz(-0.5, 0.1,  0.6), 0.24), Material.matte(Color.hsb(0.68, 0.92, 0.91))),
//				Body.uniform(Cone.crh(Vec3.xyz( -0.3, 0.5, 0.3), 0.44, 0.8), Material.LIGHT)//Material.MIRROR)
		);
	}
	
}
