package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;

public class BoundingBox {
//    private Vec3 min;
//    private Vec3 max;

    public final Box box;

    public BoundingBox() {
//        this.min = Vec3.xyz(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        this.max = Vec3.xyz(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        this(Vec3.xyz(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
             Vec3.xyz(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
    }

    public BoundingBox(Vec3 min, Vec3 max) {
//        this.min = min;
//        this.max = max;
        box = Box.$.pq(min, max);
    }

    public BoundingBox addPoint(Vec3 p){
        double[] newMin = box.p().toArray(); // bilo sta
        double[] newMax = box.q().toArray();
        if (p.x() < box.p().x()){
            newMin[0] = p.x();
        }
        if (p.y() < box.p().y()){
            newMin[1] = p.y();
        }
        if (p.z() < box.p().z()){
            newMin[2] = p.z();
        }
        if (p.x() > box.q().x()){
            newMax[0] = p.x();
        }
        if (p.y() > box.q().y()){
            newMax[1] = p.y();
        }
        if (p.z() > box.q().z()){
            newMax[2] = p.z();
        }

//        box.p() = Vec3.fromArray(newMin);
//        box.q() = Vec3.fromArray(newMax);
        return new BoundingBox(Vec3.fromArray(newMin), Vec3.fromArray(newMax));
    }

    public BoundingBox addBBox(BoundingBox bb) {
        return addPoint(bb.box.p()).addPoint(bb.box.q());
    }

    public boolean hasPoint(Vec3 p){
        return (box.p().x() <= p.x() && p.x() <= box.q().x()) &&
               (box.p().y() <= p.y() && p.y() <= box.q().y()) &&
               (box.p().z() <= p.z() && p.z() <= box.q().z());
    }

    public boolean hasBBox(BoundingBox bb){
        return hasPoint(bb.box.p()) && hasPoint(bb.box.q());
    }

    public boolean hitRay(Ray ray, double afterTime){
        return box.firstHit(ray, afterTime) != null;
    }
}
