package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;

import java.util.Collections;


public class cubeRandomRoom extends Scene.Base {

	public cubeRandomRoom() {
		addAllFrom(OpenRoomRGTextured_GI.room);

		Solid cubeSolid = Box.$.pq(Vec3.xyz(-0.5, -0.5, -0.5), Vec3.xyz(0.5, 0.5, 0.5));

		int num = 100;
		double min = 0.1;
		double max = 0.4;


		Solid[] solids = new Solid[num];


		solids[0] = cubeSolid;
		for(int i=1;i<num;i++){
			double smallCubeDiag = Math.random() * (max - min) + min;

			double r1 = (Math.random()-0.7);
			double r2 = (Math.random()-0.7);
			double r3 = (Math.random()-0.7);

			Vec3 rVec = Vec3.xyz(r1, r2, r3);
			Box temp = Box.$.pq(rVec, rVec.add(Vec3.ONES.mul(smallCubeDiag)));

			solids[i] = temp;
		}

		Solid cubeFinal = Solid.union(solids)
				.transformed(Affine.translation(Vec3.xyz(-0.5, -0.5, 2.3)));

		Body b = Body.uniform(cubeFinal);


		Collections.addAll(bodies, b);

	}
	
}
