package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;


import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRGTextured_GI;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;
import xyz.marsavic.gfxlab.graphics3d.solids.TriangleMesh;

import java.util.Collections;
import java.util.Map;


public class CombineAllRoom extends Scene.Base {

	public CombineAllRoom(double rotX, double rotY, double rotZ) {
		addAllFrom(new cubeRandomRoom());
		addAllFrom(new teapotMotionRoom());
		addAllFrom(new TriMeshDragonRoom());
		addAllFrom(new UnionRoom());
		addAllFrom(new ConeRoom());
//		addAllFrom(new TorusRoom(rotX, rotY, rotZ));
		System.out.println("All bodies: "+bodies.size());
	}
}
