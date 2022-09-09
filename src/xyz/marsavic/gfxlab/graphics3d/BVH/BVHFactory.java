package xyz.marsavic.gfxlab.graphics3d.BVH;


import xyz.marsavic.gfxlab.graphics3d.*;

import java.util.*;

import static xyz.marsavic.gfxlab.graphics3d.Collider.Collision;


public class BVHFactory {

    private final int amount;

    private BVHFactory(int amount) {
        this.amount = amount;
    }

    public static BodyBVH getBodyBVH(Collection<Body> bodies, int amount){
        BVHFactory f = new BVHFactory(amount);
        return f.body(bodies);
    }

    public static SolidBVH getSolidBVH(Collection<Solid> solids, int amount){
        BVHFactory f = new BVHFactory(amount);
        return f.solid(solids);
    }

    public BodyBVH body(Collection<Body> bodies) {
        return new BodyBVH(bodies);
    }
    public SolidBVH solid(Collection<Solid> solids) {
        return new SolidBVH(solids);
    }

    public interface Itr<T> extends Iterator<Solid>{        ;
        T get();
    }
    class BodyItr implements Itr<Body>{
        Body b;
        Iterator<Body> it;

        BodyItr(Iterator<Body> it){
            this.it = it;
        }

        @Override
        public Body get() {
            return b;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Solid next() {
            Body b = it.next();
            this.b = b;
            return b.solid();
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    class SolidItr implements Itr<Solid>{
        Solid s;
        Iterator<Solid> it;

        SolidItr(Iterator<Solid> it){
            this.it = it;
        }

        @Override
        public Solid get() {
            return s;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Solid next() {
            s = it.next();
            return s;
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    public abstract class BVH<T, S> {
        private BoundingBox bbox;
        final Collection<T> bodies;
        BVH<T, S> left, right;


        private BVH(Collection<T> bodies) {
            this.bodies = bodies;
            calculateBbox();
        }

        protected abstract Itr<T> getIter();

        private void calculateBbox() {
            BoundingBox localBBox = new BoundingBox();

            for(Itr<T> i = getIter(); i.hasNext();) {
                Solid s = i.next();
                localBBox = localBBox.addBBox(s.bbox());
            }

            bbox = localBBox;
        }

        private List<Set<T>> divideBVH() {
            if(bodies.size() < amount){
                return null;
            }
            BoundingBox leftHalf = bbox.getLeftHalf();

            Set<T> leftBodies = new HashSet<>();
            Set<T> rightBodies = new HashSet<>();

            Set<T> inMid = new HashSet<>();

            for(Itr<T> i = getIter(); i.hasNext();) {
                Solid s = i.next();
                BoundingBox.hasBBox e = leftHalf.hasBBox(s.bbox());

                switch (e){
                    case FULL:
                        leftBodies.add(i.get());
                        break;
                    case NONE:
                        rightBodies.add(i.get());
                        break;
                    case HALF:
                        inMid.add(i.get());
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
            List<Set<T>> l = new ArrayList<>();
            l.add(leftBodies);l.add(rightBodies);
            return l;
        }


        public S getCollision(Ray ray, double epsilon) {
            S s = null;
            if(bbox.rayHitsBox(ray, epsilon)){
                s = getBestCollision(ray, epsilon);
                if(left != null){
                    S leftS = left.getCollision(ray, epsilon);
                    S rightS = right.getCollision(ray, epsilon);

                    s = min(s, leftS, rightS);
                }
            }
            return s;
        }

        abstract S min(S s1, S s2, S s3);

        abstract S getBestCollision(Ray ray, double epsilon);


        public boolean getCollisionIn01(Ray r, double epsilon) {
            throw new RuntimeException("Method getCollisionIn01 in BVH class not implemented!!!");
        }
    }

    class BodyBVH extends BVH<Body, Collision> {

        private BodyBVH(Collection<Body> bodies) {
            super(bodies);
            repeatDivide();
        }

        @Override
        protected Itr<Body> getIter() {
            return new BodyItr(bodies.iterator());
        }


        private void repeatDivide(){
            List<Set<Body>> l = super.divideBVH();
            if(l != null){
                left = new BodyBVH(l.get(0));
                right = new BodyBVH(l.get(1));
            }
        }

        @Override
        Collision min(Collision c1, Collision c2, Collision c3) {
            Collision col = Collision.EMPTY;
            for(Collision c : new Collision[]{c1, c2, c3}) {
                if (c != null) {
                    double t = c.hit().t();

                    if (t > 0 && t < col.hit().t()) {
                        col = c;
                    }
                }
            }
            return col.body() == null ? null: col;
        }

        @Override
        Collision getBestCollision(Ray ray, double epsilon) {
            Collision min = Collision.EMPTY;
            for(Itr<Body> i = getIter(); i.hasNext();) {
                Solid s = i.next();
                Hit hit = s.firstHit(ray, epsilon);
                min = min(i.get(), hit, min);
            }
            return min;
        }

        Collision min(Body b, Hit h, Collision c) {
            if(h != null && b != null){
                double t = h.t();

                if(t > 0 && t < c.hit().t()){
                    return new Collision(h, b);
                }
            }
            return c;
        }
    }

    class SolidBVH extends BVH<Solid, Hit> {

        private SolidBVH(Collection<Solid> bodies) {
            super(bodies);
            repeatDivide();
        }

        @Override
        protected Itr<Solid> getIter() {
            return new SolidItr(bodies.iterator());
        }


        private void repeatDivide(){
            List<Set<Solid>> l = super.divideBVH();
            if(l != null){
                left = new SolidBVH(l.get(0));
                right = new SolidBVH(l.get(1));
            }
        }

        @Override
        Hit min(Hit h1, Hit h2, Hit h3) {
            Hit hit = new Hit.Data(Double.MAX_VALUE, null);
            for(Hit c : new Hit[]{h1, h2, h3}) {
                if (c != null) {
                    double t = c.t();

                    if (t > 0 && t < hit.t()) {
                        hit = c;
                    }
                }
            }
            return hit.n() == null ? null: hit;
        }

        @Override
        Hit getBestCollision(Ray ray, double epsilon) {
            Hit min = null;
            for(Itr<Solid> i = getIter(); i.hasNext();) {
                Solid s = i.next();
                Hit hit = s.firstHit(ray, epsilon);
                min = min(hit, min, null);
            }
            return min;
        }
    }
}
