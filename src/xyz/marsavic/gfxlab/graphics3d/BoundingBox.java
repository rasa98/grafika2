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
        this(Vec3.xyz(3.14 * 10e30, 3.14 * 10e30, 3.14 * 10e30),
                Vec3.xyz(-3.14 * 10e30, -3.14 * 10e30, -3.14 * 10e30));
    }

    public BoundingBox(Vec3 min, Vec3 max) {
//        this.min = min;
//        this.max = max;

        box = new Box(min, max);//Box.$.pd(min, max);
    }

    public BoundingBox(Box box) {
//        this.min = min;
//        this.max = max;

        this.box = box;
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

        Vec3 min = Vec3.fromArray(newMin);
        Vec3 max = Vec3.fromArray(newMax);

        return new BoundingBox(min, max);
    }

    public BoundingBox addBBox(BoundingBox bb) {
        return addPoint(bb.box.p()).addPoint(bb.box.q());
    }

    public boolean hasPoint(Vec3 p){
        return (box.p().x() <= p.x() && p.x() <= box.q().x()) &&
               (box.p().y() <= p.y() && p.y() <= box.q().y()) &&
               (box.p().z() <= p.z() && p.z() <= box.q().z());
    }


    public enum hasBBox {
        Full, half, None
    }

    public hasBBox hasBBox(BoundingBox bb){
        boolean hasP = hasPoint(bb.box.p());
        boolean hasQ = hasPoint(bb.box.q());
        if (hasP && hasQ)
            return hasBBox.Full;
        else if(hasP || hasQ)
            return hasBBox.half;
        return hasBBox.None;
    }

    public boolean rayHitsBox(Ray ray, double afterTime){
        return box.firstHit(ray, afterTime) != null;
    }

    public BoundingBox getLeftHalf(){
        double x0 = box.p().x();
        double y0 = box.p().y();
        double z0 = box.p().z();

        double x1 = box.q().x();
        double y1 = box.q().y();
        double z1 = box.q().z();

        double dx = x1 - x0;
        double dy = y1 - y0;
        double dz = z1 - z0;

        if(dx >= dy && dx >= dz){
            x0 = x0 + dx / 2;
            x1 = x0;
        }else if(dy >= dx && dy >= dz){
            y0 =y0 + dy / 2;
            y1 = y0;
        }else{
            z0 = z0 + dz / 2;
            z1 = z0;
        }

        Vec3 mid_max = Vec3.xyz(x1, y1, z1);
        return new BoundingBox(box.p(), mid_max);
    }


}

