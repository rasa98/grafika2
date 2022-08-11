package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.*;

import java.util.*;


public class teapotMotionRoom extends Scene.Base {

	public teapotMotionRoom() {
		addAllFrom(OpenRoomRGTextured_GI.room);


		Map<String, TriangleMesh> meshes = readTriMeshFromFile("resources/obj/teapot-high.obj", 5);
		for(TriangleMesh tm: meshes.values()){
			bodies.add(Body.uniform(tm.transformed(Affine.scaling(0.04)
													  .andThen(Affine.rotationAboutX(-0.25)
													  .andThen(Affine.rotationAboutZ(-0.14)
													  .andThen(Affine.translation(Vec3.xyz(-1.0, -0.77, -0.3))))))
										 .transformedMotionBlur(t -> Affine.translation(Vec3.xyz(-0.2*t, 0, 0))),
						  Material.matte(0.25)));
		}


	}
	
}
