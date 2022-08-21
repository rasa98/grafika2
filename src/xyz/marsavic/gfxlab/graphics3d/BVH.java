package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class BVH {

    private BoundingBox bbox;
    private Collection<Body> bodies;

    // Only root uses
    public Collection<Body> outliers;
    private BVH left, right;




    private BVH(Collection<Body> bodies) {
        this.bodies = bodies;
    }


    private BVH(BoundingBox bbox, Collection<Body> l) {
        this(l);
        this.bbox = bbox;
    }


    public static BVH makeBVH(Collection<Body> bodies, int amount) {
        BVH root = new BVH(new HashSet<>()){};
        BoundingBox localBBox = new BoundingBox();

        Set<Body> NoBBoxSolids = new HashSet<>();

        for(Iterator<Body> i = bodies.iterator(); i.hasNext();) {
            Body body = i.next();
            Solid s = body.solid();
            if(s instanceof HalfSpace){
                NoBBoxSolids.add(body);
                i.remove();
            }
            else{
                localBBox = localBBox.addBBox(s.bbox());
            }
        }

        root.bbox = localBBox;
        root.bodies = bodies;
        divideBVH(root, amount);
        root.outliers = NoBBoxSolids;
        return root;
    }

    private static void divideBVH(BVH bvh, int amount) {
        if(bvh.bodies.size() < amount){
            return;
        }
        BoundingBox leftHalf = bvh.bbox.getLeftHalf();

        Set<Body> leftBodies = new HashSet<>();
        Set<Body> rightBodies = new HashSet<>();
        BoundingBox left = new BoundingBox();
        BoundingBox right = new BoundingBox();

        Set<Body> inMid = new HashSet<>();

        for(Iterator<Body> i = bvh.bodies.iterator(); i.hasNext();) {
            Body body = i.next();
            Solid s = body.solid();
            BoundingBox.hasBBox e = leftHalf.hasBBox(s.bbox());

            switch (e){
                case Full:
                    leftBodies.add(body);
                    left = left.addBBox(s.bbox());

                    break;
                case None:
                    rightBodies.add(body);
                    right = right.addBBox(s.bbox());
                    break;
                default:
                    inMid.add(body);
            }

            i.remove();
        }
        for(Body b: inMid){
            if(leftBodies.size() >= rightBodies.size()){
                rightBodies.add(b);
                right = right.addBBox(b.solid().bbox());
            }
            else{
                leftBodies.add(b);
                left = left.addBBox(b.solid().bbox());
            }
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
            minC = getBestCollision(ray, epsilon, bodies, null);
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
                        minC = rightC;
                    }
                }
            }
        }
        return minC;
    }

    public Collider.Collision getBestCollision(Ray ray, double epsilon, Collection<Body> bodies, Collider.Collision minC){
        double minT = minC != null? minC.hit().t() : Double.MAX_VALUE;
        Body minBody = null;
        Hit minHit = null;
        for(Body body: bodies) {
            Solid s = body.solid();
            Hit hit = s.firstHit(ray, epsilon);
            if(hit != null){
                double hitT = hit.t();
                if(hitT > 0 && hitT < minT){
                    minT = hitT;
                    minBody = body;
                    minHit = hit;
                }
            }
        }
        return minBody != null ? new Collider.Collision(minHit , minBody) : minC;
    }


    public boolean getCollisionIn01(Ray r, double epsilon) {
        throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
    }
}
