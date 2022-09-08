package xyz.marsavic.gfxlab.graphics3d.BVH;


import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static xyz.marsavic.gfxlab.graphics3d.Collider.Collision;


public class BVH {

    private BoundingBox bbox;
    private Collection<Body> bodies;
    private BVH left, right;


    private BVH(Collection<Body> bodies) {
        this.bodies = bodies;
    }


    private BVH(BoundingBox bbox, Collection<Body> l) {
        this(l);
        this.bbox = bbox;
    }

    public static BVH makeBVH(Collection<Body> bodies, int amount) {

        return createBVH(bodies, amount);
    }


    public static BVH createBVH(Collection<Body> bodies, int amount) {
        BVH root = new BVH(new HashSet<>()){};
        BoundingBox localBBox = new BoundingBox();

        for(Iterator<Body> i = bodies.iterator(); i.hasNext();) {
            Body body = i.next();
            Solid s = body.solid();
            localBBox = localBBox.addBBox(s.bbox());
        }

        root.bbox = localBBox;
        root.bodies = bodies;
        divideBVH(root, amount);
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
                case FULL:
                    leftBodies.add(body);
                    left = left.addBBox(s.bbox());
                    break;
                case NONE:
                    rightBodies.add(body);
                    right = right.addBBox(s.bbox());
                    break;
                case HALF:
                    inMid.add(body);
                    break;
                default: // outlier
                    continue;
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

        // jvm no tail recursion
        leftHalf = null;leftBodies = null;rightBodies = null;
        left = null;right = null;inMid = null;

        divideBVH(bvh.left, amount);
        divideBVH(bvh.right, amount);
    }

    private class BestCol{
        Collision c;

        BestCol(){ this.c = Collision.empty();}

        BestCol(Collision c){ this.c = c; }

        void best(Collision... cs){
            for(Collision c : cs) {
                if (c == null)
                    continue;

                double t = c.hit().t();

                if (t > 0 && t < this.c.hit().t()) {
                    this.c = c;
                }
            }
        }

        void best(Body b, Hit h){
            if(h == null || b == null)
                return;

            double t = h.t();

            if(t > 0 && t < this.c.hit().t()){
                this.c = new Collision(h, b);
            }
        }
    }

    public Collision getCollision(Ray ray, double epsilon) {
        BestCol minC = new BestCol();
        if(bbox.rayHitsBox(ray, epsilon)){
            getBestCollision(ray, epsilon, bodies, minC);
            if(left != null){
                Collision leftC = left.getCollision(ray, epsilon);
                Collision rightC = right.getCollision(ray, epsilon);

                minC.best(leftC, rightC);
            }
        }
        return minC.c;
    }

    public void getBestCollision(Ray ray, double epsilon, Collection<Body> bodies, BestCol minC){
        for(Body body: bodies) {
            Solid s = body.solid();
            Hit hit = s.firstHit(ray, epsilon);
            minC.best(body, hit);
        }
    }


    public Collision getBestCollision(Ray ray, double epsilon, Collection<Body> bodies, Collision minC){
        BestCol bestC = new BestCol(minC);
        getBestCollision(ray, epsilon, bodies, bestC);
        return bestC.c;
    }


    public boolean getCollisionIn01(Ray r, double epsilon) {
        throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
    }
}
