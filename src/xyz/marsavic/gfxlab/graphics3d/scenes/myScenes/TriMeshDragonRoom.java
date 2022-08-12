package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;
import xyz.marsavic.gfxlab.graphics3d.solids.TriangleMesh;

import java.util.Collections;
import java.util.Map;


public class TriMeshDragonRoom extends Scene.Base {

	public TriMeshDragonRoom() {
//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);

		var meshes = readTriMeshFromFile("resources/obj/dragon.obj", 10);
		for(TriangleMesh tm: meshes.values()){
			bodies.add(Body.uniform(tm.transformed(Affine.scaling(0.15)
													  .andThen(Affine.rotationAboutY(-0.12)
													  .andThen(Affine.translation(Vec3.xyz(0.47, -1.03, 0.2))))),
						  Material.matte(0.6)));
		}


		Collections.addAll(bodies);

	}
	
}
