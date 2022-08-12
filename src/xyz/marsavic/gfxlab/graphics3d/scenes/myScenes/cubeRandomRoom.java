package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;

import java.util.*;
import java.util.stream.DoubleStream;


public class cubeRandomRoom extends Scene.Base {

	public cubeRandomRoom() {
//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);

		Solid cubeSolid = Box.UNIT;

		int num = 50;
		double min = 0.02;
		double max = 0.14;

		Random rand = new Random();

		Solid[] solids = new Solid[num];


		Set<Double> shuffle= new HashSet<>();
		solids[0] = cubeSolid;
		for(int i=0; i<num-1; i++){
			double smallCubeDiag = rand.nextDouble() * (max - min) + min;


			double r1 = rand.nextDouble() - 0.5;
			double r2 = rand.nextDouble() - 0.5;
			double r3 = (rand.nextDouble() * (0.5 - 0.4) + 0.4) * (rand.nextBoolean() ? -1 : 1);

			shuffle.add(r1);shuffle.add(r2);shuffle.add(r3);

			Iterator<Double> it = shuffle.iterator();
			Vec3 rVec = Vec3.xyz(it.next(), it.next(), it.next());

			shuffle.clear();

			Solid temp = Box.$.cr(rVec ,Vec3.EXYZ.mul(smallCubeDiag));

			solids[i+1] = temp;
		}

		Solid cubeFinal = Solid.union(solids)
				.transformed(Affine.translation(Vec3.xyz(-.6, -0.5, 2.8
				)));

		Body b = Body.uniform(cubeFinal, Material.matte(Color.hsb(0.4, 0.8, .8)));


		Collections.addAll(bodies, b);

	}
	
}
