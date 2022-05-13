package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.*;

import java.util.Collections;


public class TestGITorus extends Scene.Base {
	public TestGITorus(double rotX, double rotY, double rotZ) {
		addAllFrom(new OpenRoomRGTextured_GI());
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz( 0.4,  0.3,  -0.2), 0.3), Material.LIGHT),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,  0.9,  0), Vec3.xyz( 0, -1,  0)), Material.LIGHT),
				Body.uniform(Torus.crh(Vec3.xyz(-0, -0, -0), 0.3, 0.09).transformed(Affine.rotationAboutX(rotX).
						andThen(Affine.rotationAboutY(rotY)).
						andThen(Affine.rotationAboutZ(rotZ))), Material.LIGHT)

		);
	}
}
