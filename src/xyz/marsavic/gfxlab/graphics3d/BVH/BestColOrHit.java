package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider.Collision;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public abstract class BestColOrHit<COLorHIT, T>{
    COLorHIT cORh;

    BestColOrHit(COLorHIT cORh){ this.cORh = cORh; }

    public abstract COLorHIT getCH();

    abstract void best(COLorHIT cs1, COLorHIT cs2);

    abstract void best(T b, Hit h);

    public static class BestCol extends BestColOrHit<Collision, Body>{

        public BestCol() {
            super(Collision.EMPTY);
        }

        @Override
        public Collision getCH() {
            if(cORh.body() == null)
                return null;
            return cORh;
        }

        @Override
        void best(Collision c1, Collision c2) {
            for(Collision c : new Collision[]{c1, c2}) {
                if (c == null)
                    continue;

                double t = c.hit().t();

                if (t > 0 && t < this.cORh.hit().t()) {
                    this.cORh = c;
                }
            }
        }

        @Override
        void best(Body b, Hit h) {
            if(h == null || b == null)
                return;

            double t = h.t();

            if(t > 0 && t < this.cORh.hit().t()){
                this.cORh = new Collision(h, (Body) b);
            }
        }
    }

    public static class BestHit extends BestColOrHit<Hit, Solid>{

        public BestHit() {
            super(Hit.Data.tn(Double.MAX_VALUE, null));
        }

        @Override
        public Hit getCH() {
            if(cORh.n() == null)
                return null;
            return cORh;
        }

        @Override
        void best(Hit h1, Hit h2) {
            for(Hit c : new Hit[]{h1, h2}) {
                best(c);
            }
        }

        @Override
        void best(Solid s, Hit h) {
            this.best(h);
        }

        private void best(Hit h) {
            if (h == null)
                return;

            double t = h.t();

            if (t > 0 && t < this.cORh.t()) {
                this.cORh = h;
            }
        }
    }

}


