package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;



import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;
import xyz.marsavic.gfxlab.graphics3d.solids.TriangleMesh;

import java.util.Collections;
import java.util.Map;


public class UnionRoom extends Scene.Base {

	public UnionRoom() {
//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);

		Solid sA = Box.$.r(0.5).transformed(Affine.rotationAboutX(0.1).andThen(Affine.rotationAboutY(0.1)));
		Solid sB = Ball.cr(Vec3.xyz(0, 0, 0), 0.62);
		Solid sC = Ball.cr(Vec3.xyz(0, 0, 0), 0.68);
		Solid s = Solid.intersection(Solid.difference(sA, sB), sC);


		double scaleFactor = 0.33;
		int num = 100;
		Solid[] set = new Solid[num];
		for(int i=0; i<num;i++){
			scaleFactor = scaleFactor * 0.957;
			Solid temp = s.transformed(Affine.scaling(scaleFactor)
					.andThen(Affine.rotationAboutZ(Math.random()))
					.andThen(Affine.rotationAboutY(Math.random()))
					.andThen(Affine.translation(Vec3.xyz(-0.01 * i, 0.06, -0.02 * i)))
			);
			set[i] = temp;
		}
		Solid uni = Solid.union(set).transformed(Affine.translation(Vec3.xyz(0.6, -0.5, -0.8)).andThen(Affine.rotationAboutZ(0.1)));
		Body csg = Body.uniform(uni, Material.matte(Color.hsb(0.18, 1, 0.29)));
		bodies.add(csg);

		Collections.addAll(bodies);

	}
	
}
