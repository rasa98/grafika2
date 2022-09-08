package xyz.marsavic.gfxlab.graphics3d.BVH;


import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static xyz.marsavic.gfxlab.graphics3d.Collider.Collision;


public class BVHFactory {

    private int amount;
    private BVH bvh;


    private BVHFactory(Collection<Body> bodies, int amount) {
        this.amount = amount;
        bvh = new BVH(bodies);
    }

    public static BVH makeBVH(Collection<Body> bodies, int amount) {
        return new BVHFactory(bodies, amount).bvh;
    }

//    public Collision getCollision(Ray ray, double epsilon) {
//        return bvh.getCollision(ray, epsilon);
//    }

    public class BVH {
        private BoundingBox bbox;
        private Collection<Body> bodies;
        private BVH left, right;


        private BVH(Collection<Body> bodies) {
            this.bodies = bodies;
            calculateBbox();
            divideBVH(amount);
        }


        private void calculateBbox() {
            BoundingBox localBBox = new BoundingBox();

            for(Iterator<Body> i = bodies.iterator(); i.hasNext();) {
                Body body = i.next();
                Solid s = body.solid();
                localBBox = localBBox.addBBox(s.bbox());
            }

            bbox = localBBox;
        }

        private void divideBVH(int amount) {
            if(bodies.size() < amount){
                return;
            }
            BoundingBox leftHalf = bbox.getLeftHalf();

            Set<Body> leftBodies = new HashSet<>();
            Set<Body> rightBodies = new HashSet<>();

            Set<Body> inMid = new HashSet<>();

            for(Iterator<Body> i = bodies.iterator(); i.hasNext();) {
                Body body = i.next();
                Solid s = body.solid();
                BoundingBox.hasBBox e = leftHalf.hasBBox(s.bbox());

                switch (e){
                    case FULL:
                        leftBodies.add(body);
                        break;
                    case NONE:
                        rightBodies.add(body);
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
                }
                else{
                    leftBodies.add(b);
                }
            }


            left = new BVH(leftBodies);
            right = new BVH(rightBodies);
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

        private void getBestCollision(Ray ray, double epsilon, Collection<Body> bodies, BestCol minC){
            for(Body body: bodies) {
                Solid s = body.solid();
                Hit hit = s.firstHit(ray, epsilon);
                minC.best(body, hit);
            }
        }


//        public Collision getBestCollision(Ray ray, double epsilon, Collection<Body> bodies, Collision minC){
//            BestCol bestC = new BestCol(minC);
//            getBestCollision(ray, epsilon, bodies, bestC);
//            return bestC.c;
//        }


        public boolean getCollisionIn01(Ray r, double epsilon) {
            throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
        }
    }
}
