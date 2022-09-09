package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider.Collision;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public interface BestColOrHit<COLorHIT, T>{
    COLorHIT getCH();

    void best(COLorHIT cs1, COLorHIT cs2);

    void best(T b, Hit h);

    static BestColOrHit<Collision, Body> bestCol() {
        return new BestColOrHit<>() {
            private Collision c = Collision.EMPTY;

            @Override
            public Collision getCH() {
                if (c.body() == null)
                    return null;
                return c;
            }

            @Override
            public void best(Collision c1, Collision c2) {
                for (Collision c : new Collision[]{c1, c2}) {
                    if (c == null)
                        continue;

                    double t = c.hit().t();

                    if (t > 0 && t < this.c.hit().t()) {
                        this.c = c;
                    }
                }
            }

            @Override
            public void best(Body b, Hit h) {
                if (h == null || b == null)
                    return;

                double t = h.t();

                if (t > 0 && t < this.c.hit().t()) {
                    this.c = new Collision(h, (Body) b);
                }
            }
        };
    }


    static BestColOrHit<Hit, Solid> bestHit() {
        return new BestColOrHit<>() {
            private Hit h = Hit.Data.tn(Double.MAX_VALUE, null);

            @Override
            public Hit getCH() {
                if (h.n() == null)
                    return null;
                return h;
            }

            @Override
            public void best(Hit h1, Hit h2) {
                for (Hit h : new Hit[]{h1, h2}) {
                    best(h);
                }
            }

            @Override
            public void best(Solid b, Hit h) {
                if (h == null)
                    return;

                double t = h.t();

                if (t > 0 && t < this.h.t()) {
                    this.h = h;
                }
            }

            private void best(Hit h) {
                if (h == null)
                    return;

                double t = h.t();

                if (t > 0 && t < this.h.t()) {
                    this.h = h;
                }
            }

        };
    }
}


