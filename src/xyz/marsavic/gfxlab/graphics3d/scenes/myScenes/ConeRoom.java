package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Cone;
import xyz.marsavic.gfxlab.graphics3d.solids.Cylinder;

import java.util.Collections;


public class ConeRoom extends Scene.Base {

	public ConeRoom() {
		//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		
		Collections.addAll(bodies,
				Body.uniform(Cylinder.UNIT.transformed(Affine.scaling(Vec3.xyz(0.3, 20, 0.3))
										  .andThen(Affine.rotationAboutX(0.25))
										  .andThen(Affine.translation(Vec3.xyz(.9, .9, 3)))
										  ),
//						Material.matte(Color.hsb(0.17, 1, 1))
						Material.BLACK.emittance(Color.rgb(1, .05, .05))
				),
//				Body.uniform(Ball.cr(Vec3.xyz( -1.9,  0.3,  4.07), 0.25), Material.LIGHT),
				Body.uniform(Cone.UNIT.transformed(Affine.rotationAboutZ(0.32)
												   .andThen(Affine.rotationAboutY(-0.91))
												   .andThen(Affine.translation(Vec3.xyz(-0.28, -2.15, -1.7)))
												   .andThen(Affine.scaling(0.4))),
							 Material.matte(Color.hsb(0.78, 0.99, .99))

				)

		);
	}
	
}
