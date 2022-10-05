package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Affine;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Scene;
import xyz.marsavic.gfxlab.graphics3d.solids.TriangleMesh;

import java.util.Map;


public class teapotLowRes extends Scene.Base {

	public teapotLowRes(double Z) {
//		addAllFrom(OpenRoomRGTextured_GI.room);
		addAllFrom(LightBehindRoom.room);


		Map<String, TriangleMesh> meshes = readTriMeshFromFile("resources/obj/teapot-low.obj", 2);
		for(TriangleMesh tm: meshes.values()){
			bodies.add(Body.uniform(tm.transformed(Affine.scaling(0.058)
													  .andThen(Affine.rotationAboutX(-0.25)
													  .andThen(Affine.rotationAboutZ(-0.14)
													  .andThen(Affine.translation(Vec3.xyz(-0.3, -0.5, -2.7+Z)))))),

						  Material.matte(0.25)));
		}


	}
	
}
