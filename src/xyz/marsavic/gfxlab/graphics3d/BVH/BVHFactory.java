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
        return new BVHFactory<>(bodies, Wrap::bodyCollection, amount, BestColOrHit::bestCol).bvh;
    }

    public static BVHFactory<Solid, Hit>.BVH makeBVHSolid(Collection<Solid> bodies, int amount) {
        return new BVHFactory<>(bodies, Wrap::solidCollection, amount, BestColOrHit::bestHit).bvh;
    }


    public class BVH {
        private BoundingBox bbox;
        private final Wrap<T> wrapper;
        private BVH left, right;


        private BVH(Collection<T> col) {
            this.wrapper = wrapperFactory.apply(col);
            calculateBbox();
            divideBVH(amount);
        }


        private void calculateBbox() {
            BoundingBox localBBox = new BoundingBox();

            for (Solid s : wrapper) {
                localBBox = localBBox.addBBox(s.bbox());
            }

            bbox = localBBox;
        }

        private void divideBVH(int amount) {
            if(wrapper.getSize() < amount){
                return;
            }
            BoundingBox leftHalf = bbox.getLeftHalf();

            Set<T> leftBodies = new HashSet<>();
            Set<T> rightBodies = new HashSet<>();

            Set<T> inMid = new HashSet<>();

            for(MyItr<T> i = wrapper.iterator(); i.hasNext();) {
                Solid s = i.next();
                T body = i.get();

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
            return getColOrHitPrivate(ray, epsilon).getCH();
        }

        private BestColOrHit<S, T> getColOrHitPrivate(Ray ray, double epsilon){
            BestColOrHit<S, T> bestCorH = chFactory.get();
            getBestColOrHit(ray, epsilon, wrapper, bestCorH); // from body/solids in this bvh level (if any)
            if(left != null){
                Hit h1 = left.bbox.rayHitsBox(ray, epsilon);
                Hit h2 = right.bbox.rayHitsBox(ray, epsilon);

                BestColOrHit<Hit, Solid> minHit = BestColOrHit.bestHit();
                minHit.best(h1, h2);

                Hit earlierHit = minHit.getCH();
                Hit laterHit;
                if(earlierHit != null){ // if ray hit any of child (h1 or h2)
                    BestColOrHit<S, T> fromBetterChild;
                    BestColOrHit<S, T> fromWorseChild;

                    BVH first , second;
                    if(earlierHit == h1){
                        first = left; second = right;
                        laterHit = h2;
                    }else{
                        first = right; second = left;
                        laterHit = h1;
                    }

                    fromBetterChild = first.getColOrHitPrivate(ray, epsilon);

                    if(fromBetterChild.getCH() == null || (laterHit != null && fromBetterChild.time() > laterHit.t())){
                        fromWorseChild = second.getColOrHitPrivate(ray, epsilon);
                    }else{
                        fromWorseChild = chFactory.get();
                    }
                    bestCorH.best(fromBetterChild.getCH(), fromWorseChild.getCH());
                }
            }
            return bestCorH;
        }

        private void getBestColOrHit(Ray ray, double epsilon, Wrap<T> wrapper, BestColOrHit<S, T> bestCorH){
            for(MyItr<T> i = wrapper.iterator(); i.hasNext();) {
                Solid s = i.next();
                Hit hit = s.firstHit(ray, epsilon);
                bestCorH.best(i.get(), hit);
            }
        }


        public boolean getAnyColOrHit(Ray ray, double epsilon) {
            if(bbox.rayHitsBox(ray, epsilon) != null){
                for (Solid s : wrapper) {
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
