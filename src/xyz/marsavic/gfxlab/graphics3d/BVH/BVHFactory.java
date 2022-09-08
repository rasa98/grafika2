package xyz.marsavic.gfxlab.graphics3d.BVH;


import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static xyz.marsavic.gfxlab.graphics3d.Collider.Collision;


public class BVHFactory<T> {

    private final int amount;
    private final BVH bvh;

    private final Function<Collection<T>, Wrap<T>> wrapperFactory ;// Foo::new;


    private BVHFactory(Collection<T> bodies, Function<Collection<T>, Wrap<T>> wrapperFactory, int amount) {
        this.amount = amount;
        this.wrapperFactory = wrapperFactory;
        bvh = new BVH(bodies);
    }

    public static BVHFactory<Body>.BVH makeBVHBody(Collection<Body> bodies, int amount) {
        return new BVHFactory<>(bodies, Wrap.BodyCol::new, amount).bvh;
    }

    public static BVHFactory<Solid>.BVH makeBVHSolid(Collection<Solid> bodies, int amount) {
        return new BVHFactory<>(bodies, Wrap.SolidCol::new, amount).bvh;
    }


    public class BVH {
        private BoundingBox bbox;
        private final Wrap<T> wraper;
        private BVH left, right;


        private BVH(Collection<T> col) {
            this.wraper = wrapperFactory.apply(col);
            calculateBbox();
            divideBVH(amount);
        }


        private void calculateBbox() {
            BoundingBox localBBox = new BoundingBox();

            for (Solid s : wraper) {
                localBBox = localBBox.addBBox(s.bbox());
            }

            bbox = localBBox;
        }

        private void divideBVH(int amount) {
            if(wraper.col.size() < amount){
                return;
            }
            BoundingBox leftHalf = bbox.getLeftHalf();

            Collection<T> leftBodies = new HashSet<>();
            Collection<T> rightBodies = new HashSet<>();

            Set<T> inMid = new HashSet<>();

            for(MyItr<T> i = wraper.iterator(); i.hasNext();) {
                Solid s = i.next();
                T body = i.getE();

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
            for(T b: inMid){
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
                getBestCollision(ray, epsilon, wraper, minC);
                if(left != null){
                    Collision leftC = left.getCollision(ray, epsilon);
                    Collision rightC = right.getCollision(ray, epsilon);

                    minC.best(leftC, rightC);
                }
            }
            return minC.c;
        }

        private void getBestCollision(Ray ray, double epsilon, Wrap<T> wrapper, BestCol minC){
            for(MyItr<T> i = wrapper.iterator(); i.hasNext();) {
                Solid s = i.next();
                Hit hit = s.firstHit(ray, epsilon);
                minC.best((Body) i.getE(), hit);
            }
        }


        public boolean getCollisionIn01(Ray r, double epsilon) {
            throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
        }
    }
}
