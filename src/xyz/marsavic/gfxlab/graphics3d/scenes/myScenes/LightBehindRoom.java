package xyz.marsavic.gfxlab.graphics3d.scenes.myScenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.graphics3d.textures.Grid;

import java.util.Collections;


public class LightBehindRoom extends Scene.Base {

	public static final Scene room = new LightBehindRoom();

	private LightBehindRoom() {

		Texture tL = Grid.standard(Color.hsb(0.0/3, 0.5, 0.5));
		Texture tW = Grid.standard(Color.gray(0.5));
		Texture tS = Grid.standard(Color.hsb(0.76, 0.84, 0.96));
		Texture tN = Grid.standard(Color.hsb(0.27, 0.78, 0.91));

		Collections.addAll(bodies,
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,  0,  -3), Vec3.xyz( 0,  0, 1)), Material.LIGHT),
				Body.textured(HalfSpace.pn(Vec3.xyz( -8,  0,  0), Vec3.xyz( 1,  0, 0)), Material.LIGHT),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1,  0,  0), Vec3.xyz(-1,  0,  0)), tL),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,  1,  0), Vec3.xyz( 0, -1,  0)), tN),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, -1,  0), Vec3.xyz( 0,  1,  0)), tS),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,  0,  7), Vec3.xyz( 0,  0, -1)), tW)
		);
	}
	
}
