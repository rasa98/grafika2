package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;

import java.lang.reflect.Array;
import java.util.*;

public class BVH {

    private BoundingBox bbox;
    private List<Body> bodies;

    // Only root uses
    public List<Body> outliers;
    private BVH left, right;

    private BVH() {
        this.bodies = new ArrayList<>();
    }

    private BVH(List<Body> bodies) {
        this.bodies = bodies;
    }

    private BVH(BoundingBox bbox) {
        this();
        this.bbox = bbox;
    }

    private BVH(BoundingBox bbox, List<Body> l) {
        this(l);
        this.bbox = bbox;
    }

    private BVH(BoundingBox bbox, BVH left, BVH right) {
        this();
        this.bbox = bbox;
        this.left = left;
        this.right = right;
    }

    private BVH(BoundingBox bbox, List<Body> bodies, BVH left, BVH right) {
        this.bbox = bbox;
        this.bodies = bodies;
        this.left = left;
        this.right = right;
    }

    public static BVH makeBVH(List<Body> bodies, int amount) {
        BVH root = new BVH(new ArrayList<Body>()){

        };
//        Body[] b = bodies.toArray(new Body[0]);
        BoundingBox localBBox = new BoundingBox();

//        List<Body> NoBBoxSolids = new ArrayList<>();
//        for(int i=bodies.size() - 1; i>=0; i--){
//            Body body = bodies.get(i);
//            if(body.solid() instanceof HalfSpace){
//                NoBBoxSolids.add(body);
//                bodies.remove(i);
//            }
//            else{
//                SolidBBox s = (SolidBBox) body.solid();
//                localBBox = localBBox.addBBox(s.getBBox());
//            }
//        }
        for(Body body: bodies){
            SolidBBox s = (SolidBBox) body.solid();
            localBBox = localBBox.addBBox(s.getBBox());
        }

        root.bbox = localBBox;
        root.bodies = bodies;
        divideBVH(root, amount);
//        root.outliers = NoBBoxSolids;
        return root;
    }

    private static void divideBVH(BVH bvh, int amount) {
        if(bvh.bodies.size() < amount){
            return;
        }
        BoundingBox[] children = bvh.bbox.splitBox();

        BoundingBox leftHalf = children[0];
        List<Body> leftBodies = new ArrayList<>();
        List<Body> rightBodies = new ArrayList<>();
        BoundingBox left = new BoundingBox();
        BoundingBox right = new BoundingBox();

        for(int i=bvh.bodies.size() - 1; i>=0; i--){
            Body b = bvh.bodies.get(i);
            SolidBBox s = (SolidBBox) b.solid();

            BoundingBox.hasBBox e = leftHalf.hasBBox(s.getBBox());

            switch (e){
                case Full:
                    leftBodies.add(b);
                    left.addBBox(s.getBBox());
                    bvh.bodies.remove(i);
                break;
                case None:
                    rightBodies.add(b);
                    right.addBBox(s.getBBox());
                    bvh.bodies.remove(i);
                break;
//                default:
//                    if(leftBodies.size() >= rightBodies.size()){
//                        rightBodies.add(b);
//                        right.addBBox(s.getBBox());
//                    }
//                    else{
//                        leftBodies.add(b);
//                        left.addBBox(s.getBBox());
//                    }
            }
//            bvh.bodies.remove(i);
        }

        bvh.left = new BVH(left, leftBodies);
        bvh.right = new BVH(right, rightBodies);

        divideBVH(bvh.left, amount);
        divideBVH(bvh.right, amount);
    }

    public Collider.Collision getCollision(Ray ray, double epsilon) {
        Collider.Collision minC = null;
        if(bbox.rayHitsBox(ray, epsilon)){
            double minT = Double.MAX_VALUE;
            minC = getBestCollision(ray, epsilon, bodies, minC);
            if(left != null){
                Collider.Collision leftC = left.getCollision(ray, epsilon);
                Collider.Collision rightC = right.getCollision(ray, epsilon);

                if(leftC != null){
                    double t = leftC.hit().t();
                    if(t > 0 && t < minT){
                        minT = t;
                        minC = leftC;
                    }
                }
                if(rightC != null){
                    double t = rightC.hit().t();
                    if(t > 0 && t < minT){
                        minT = t;
                        minC = rightC;
                    }
                }
            }
        }
        return minC;
    }

    public Collider.Collision getBestCollision(Ray ray, double epsilon, List<Body> bodies, Collider.Collision minC){
        double minT = minC != null? minC.hit().t() : Double.MAX_VALUE;
        for(Body body: bodies) {
            SolidBBox s = (SolidBBox) body.solid();
            Hit hit = s.firstHit(ray, epsilon);
            if(hit != null){
                double hitT = hit.t();
                if(hitT > 0 && hitT < minT){
                    minT = hitT;
                    minC = new Collider.Collision(hit, body);
                }
            }
        }
        return minC;
    }


    public boolean getCollisionIn01(Ray r, double epsilon) {
        throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
    }
}
