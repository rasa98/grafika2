package xyz.marsavic.gfxlab.graphics3d.BVH;


import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static xyz.marsavic.gfxlab.graphics3d.Collider.Collision;


public class BVHFactory<T, S> {

    private final int amount;

    private final BVH bvh;

    private final Function<Collection<T>, Wrap<T>> wrapperFactory ;// Foo::new;

    private final Supplier<BestColOrHit<S, T>> chFactory;


    private BVHFactory(Collection<T> bodies, Function<Collection<T>, Wrap<T>> wrapperFactory, int amount, Supplier<BestColOrHit<S, T>> chFactory) {
        this.amount = amount;
        this.wrapperFactory = wrapperFactory;
        this.chFactory = chFactory;
        bvh = new BVH(bodies);
    }

    public static BVHFactory<Body, Collision>.BVH makeBVHBody(Collection<Body> bodies, int amount) {
        return new BVHFactory<>(bodies, Wrap.BodyCol::new, amount, BestColOrHit::bestCol).bvh;
    }

    public static BVHFactory<Solid, Hit>.BVH makeBVHSolid(Collection<Solid> bodies, int amount) {
        return new BVHFactory<>(bodies, Wrap.SolidCol::new, amount, BestColOrHit::bestHit).bvh;
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

            Set<T> leftBodies = new HashSet<>();
            Set<T> rightBodies = new HashSet<>();

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

        public S getColOrHit(Ray ray, double epsilon) {
            BestColOrHit<S, T> bestCorH = chFactory.get();
            if(bbox.rayHitsBox(ray, epsilon)){
                getBestColOrHit(ray, epsilon, wraper, bestCorH);
                if(left != null){
                    S leftC = left.getColOrHit(ray, epsilon);
                    S rightC = right.getColOrHit(ray, epsilon);

                    bestCorH.best(leftC, rightC);
                }
            }
            return bestCorH.getCH();
        }

        private void getBestColOrHit(Ray ray, double epsilon, Wrap<T> wrapper, BestColOrHit<S, T> bestCorH){
            for(MyItr<T> i = wrapper.iterator(); i.hasNext();) {
                Solid s = i.next();
                Hit hit = s.firstHit(ray, epsilon);
                bestCorH.best(i.getE(), hit);
            }
        }


        public boolean getAnyColOrHit(Ray ray, double epsilon) {
            if(bbox.rayHitsBox(ray, epsilon)){
                for (Solid s : wraper) {
                    Hit hit = s.firstHit(ray, epsilon);
                    if (hit != null)
                        return true;
                }
                if(left != null){
                    return left.getAnyColOrHit(ray, epsilon) || right.getAnyColOrHit(ray, epsilon);
                }
            }
            return false;
        }


        public boolean getCollisionIn01(Ray r, double epsilon) {
            throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
        }
    }
}
