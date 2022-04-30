package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Light;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Scene;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;

import java.util.Collections;


public class OpenRoom extends Scene.Base {
	
	public OpenRoom(Material materialWalls) {
		Collections.addAll(bodies,
				Body.uniform(HalfSpace.pn(Vec3.xyz( 1,  0,  0), Vec3.xyz(-1,  0,  0)), materialWalls),
				Body.uniform(HalfSpace.pn(Vec3.xyz(-1,  0,  0), Vec3.xyz( 1,  0,  0)), materialWalls),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,  1,  0), Vec3.xyz( 0, -1,  0)), materialWalls),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, -1,  0), Vec3.xyz( 0,  1,  0)), materialWalls),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,  0,  1), Vec3.xyz( 0,  0, -1)), materialWalls)
		);
		
		Collections.addAll(lights,
				Light.p(Vec3.xyz(-0.5, 0.75, -0.5)),
				Light.p(Vec3.xyz(-0.5, 0.75,  0.5)),
				Light.p(Vec3.xyz( 0.5, 0.75, -0.5)),
				Light.p(Vec3.xyz( 0.5, 0.75,  0.5))
		);
	}

	public OpenRoom() {
		this(Material.MATTE);
	}
}
