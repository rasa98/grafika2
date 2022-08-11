package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Cylinder;

import java.util.Collections;


public class TestGICylinder extends Scene.Base {

	public TestGICylinder() {
		addAllFrom(OpenRoomRGTextured_GI.room);
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz(-0.2, -0.5,  0.0), 0.3), glass),
				Body.uniform(Ball.cr(Vec3.xyz( 0.5,  0.5,  0.5), 0.4), Material.mirror()),
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0.1, -0.6, -1.8), 0.7, 0.5), Material.mirror())
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.matte(Color.hsb(0.6, 0.79, 0.9)))
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.mirror())
				Body.uniform(Cylinder.crh(Vec3.xyz( -0.1, -0.6, -0.5), 0.7, 0.002).transformed(Affine.rotationAboutZ(-0.05)), Material.LIGHT)
		);
	}
	
}
