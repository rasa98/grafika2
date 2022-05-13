package xyz.marsavic.gfxlab.graphics3d.scenes;


import xyz.marsavic.gfxlab.graphics3d.solids.Box;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Cylinder;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.graphics3d.textures.Checkerboard;
import xyz.marsavic.gfxlab.graphics3d.textures.Grid;

import java.util.Collections;


public class TestGIMotionBlur extends Scene.Base {

	public TestGIMotionBlur(double t1, double t2) {
		this.t1 = t1;
		this.t2 = t2;
		addAllFrom(new OpenRoomRGTextured_GI());
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.4), BSDF.REFLECTIVE, 0.05));
		Texture tR = Grid.standard(Color.hsb(1.8/3, 1, 1));
		Checkerboard ck = new Checkerboard(Vector.xy(0.05, 0.05),
				Material.matte(Color.hsb(0.65, 0.8, 0.6)),
				Material.matte(Color.hsb(0.31, 0.9, 0.89)));
		
		Collections.addAll(bodies,
//				Body.uniform(Ball.cr(Vec3.xyz(-0.2, -0.5,  0.0), 0.3), glass),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,  0.9,  0), Vec3.xyz( 0, -1,  0)), Material.LIGHT),
				Body.textured(Box.pq(Vec3.xyz(-0.5, 0.1, -0.2), Vec3.xyz(-0.15, 0.45, 0.25), t -> Affine.translation(Vec3.xyz(0, 0.04*t, 0))).transformed(
						Affine.rotationAboutZ(0.08).andThen(Affine.rotationAboutY(0.1))), ck),
//				Body.textured(Ball.cr(Vec3.xyz( 0.5,  0.5,  0.5), 0.4, t -> Affine.translation(Vec3.xyz(-0.19*(-1 / (t*t)), -0.19 * (1 / (t*t)), -0.05*t))), ck),
				Body.textured(Ball.cr(Vec3.xyz( 0.1,  0.1,  0.5), 0.35, t -> Affine.translation(Vec3.xyz(0 * t, 0.06 * 1.4*(t - 0.81)*(t - 0.81), 0))).transformed(Affine.rotationAboutZ(-0.11)), ck),
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0.1, -0.6, -1.8), 0.7, 0.5), Material.mirror())
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.matte(Color.hsb(0.6, 0.79, 0.9)))
//				Body.uniform(Cylinder.crh(Vec3.xyz( 0, -0.5, 0.7), 0.3, 0.5).transformed(Affine.rotationAboutX(-0.1)), Material.mirror())
				Body.uniform(Cylinder.crh(Vec3.xyz( -0.1, -0.6, -0.5), 0.7, 0.002).transformed(Affine.rotationAboutZ(-0.05)), Material.LIGHT)
		);
	}
}
