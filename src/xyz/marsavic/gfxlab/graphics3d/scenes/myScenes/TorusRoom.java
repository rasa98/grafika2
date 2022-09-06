package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.*;

import java.util.Collections;


public class TorusRoom extends Scene.Base {

	public TorusRoom(double rotX, double rotY, double rotZ) {
		//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz( 0.4,  0.3,  -0.2), 0.3), Material.LIGHT),
				Body.uniform(Torus.UNIT.transformed(Affine.scaling(0.6)
						.andThen(Affine.rotationAboutX(0.25 + rotX)
						.andThen(Affine.rotationAboutY(0.324 + rotY))
						.andThen(Affine.rotationAboutZ(0.35 + rotZ))
						.andThen(Affine.translation(Vec3.xyz(0.5, -0.52, 4.8))))), Material.matte(0.).emittance(Color.rgb(0., 1, 0.4)))

		);
	}
	
}
