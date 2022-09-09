package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.BVH.BVHFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class Solid {
    private BoundingBox bbox;

    public BoundingBox bbox() {
        if(bbox == null)
            bbox = calculateBBox();
        return bbox;
    }

    protected abstract BoundingBox calculateBBox();

    public Hit firstHit(Ray ray, double afterTime){
        return firstHit(ray, afterTime);
    }

    public Solid transformed(Affine t) {
        Solid res = new Solid() {
            private final Affine tInv = t.inverse();
            private final Affine tInvTransposed = tInv.transposeWithoutTranslation();


            @Override
            public Hit firstHit(Ray ray, double afterTime) {
                Ray rayO = tInv.applyTo(ray);
                Hit hitO = Solid.this.firstHit(rayO, afterTime);
                if (hitO == null) {
                    return null;
                }
                return hitO.withN(tInvTransposed.applyTo(hitO.n()));
            }

            @Override
            protected BoundingBox calculateBBox() {
                BoundingBox bb = Solid.this.bbox();
                BoundingBox bbNew = new BoundingBox();

                Vec3[] corners = bb.box.getCorners();
                for(Vec3 point : corners){
                    bbNew = bbNew.addPoint(t.applyTo(point));
                }

                return bbNew;
            }
        };
        return res;
    }

    public Solid transformedMotionBlur(Function<Double, Affine> affineF) {
        Solid res = new Solid() {
            final ThreadLocalRandom rGen = ThreadLocalRandom.current();

            @Override
            public Hit firstHit(Ray ray, double afterTime) {
                double t = rGen.nextDouble();
                Affine a = affineF.apply(1 - (t*t));
                Ray rayO = a.applyTo(ray);
                Hit hitO = Solid.this.firstHit(rayO, afterTime);
                if (hitO == null) {
                    return null;
                }
                return hitO.withN(a.transposeWithoutTranslation().applyTo(hitO.n()));
            }

            @Override
            protected BoundingBox calculateBBox() {
                Affine start = affineF.apply(0.);
                Affine end = affineF.apply(1.);

                BoundingBox bb = Solid.this.bbox();
                BoundingBox bbNew = new BoundingBox();

                Vec3[] corners = bb.box.getCorners();
                for(Vec3 point : corners){
                    bbNew = bbNew.addPoint(start.applyTo(point))
                                    .addPoint(end.applyTo(point));
                }

                return bbNew;
            }
        };
        return res;
    }

    abstract static class CsgSolid extends Solid {

        private BVHFactory.BVH<Solid, Hit> bvh = BVHFactory.getSolidBVH(new HashSet<>(Arrays.asList(children())), 3);

        abstract Solid[] children();

        public Boolean ifBoxHit(Ray ray, double afterTime){
            return bvh.getCollision(ray, afterTime) != null;
        }

		@Override
        protected BoundingBox calculateBBox() {
			BoundingBox bbox = new BoundingBox();

			for( Solid c : children()){
				bbox = bbox.addBBox(c.calculateBBox());
			}
			return bbox;
		}
    }

    public static Solid union(Solid... solids) {
        Solid s = new CsgSolid() {

                @Override
                public Solid[] children() {
                    return solids;
                }

                @Override
                public Hit firstHit(Ray ray, double afterTime) {
                    if(ifBoxHit(ray, afterTime)) {
                        int n = solids.length;

                        boolean[] in = new boolean[n];
                        Hit[] hits = new Hit[n];
                        double[] t = new double[n];

                        int inCount = 0;

                        for (int i = 0; i < n; i++) {
                            Hit hit = solids[i].firstHit(ray, afterTime);
                            if (hit == null) {
                                t[i] = Double.POSITIVE_INFINITY;
                                in[i] = false;
                            } else {
                                hits[i] = hit;
                                t[i] = hit.t();
                                in[i] = ray.d().dot(hit.n()) > 0;
                                if (in[i]) {
                                    inCount++;
                                }
                            }
                        }

                        while (true) {
                            double tFirst = Double.POSITIVE_INFINITY;
                            int iFirst = -1;
                            for (int i = 0; i < n; i++) {
                                if (t[i] < tFirst) {
                                    tFirst = t[i];
                                    iFirst = i;
                                }
                            }

                            if (iFirst == -1) {
                                return null;
                            }

                            if (in[iFirst]) {
                                if (--inCount == 0) {
                                    return hits[iFirst];
                                }
                                in[iFirst] = false;
                            } else {
                                if (inCount++ == 0) {
                                    return hits[iFirst];
                                }
                                in[iFirst] = true;
                            }

                            hits[iFirst] = solids[iFirst].firstHit(ray, tFirst);
                            t[iFirst] = hits[iFirst] == null ? Double.POSITIVE_INFINITY : hits[iFirst].t();

                        }
                    }
                    return null;
                }
            };
        return s;
    }


    public static Solid intersection(Solid... solids) {
        Solid s = new CsgSolid() {
            @Override
            public Solid[] children() {
                return solids;
            }

            @Override
            public Hit firstHit(Ray ray, double afterTime) {
                if(ifBoxHit(ray, afterTime)) {
                    int n = solids.length;

                    boolean[] in = new boolean[n];
                    Hit[] hits = new Hit[n];
                    double[] t = new double[n];

                    int inCount = 0;

                    for (int i = 0; i < n; i++) {
                        Hit hit = solids[i].firstHit(ray, afterTime);
                        if (hit == null) {
                            t[i] = Double.POSITIVE_INFINITY;
                            in[i] = false;
                        } else {
                            hits[i] = hit;
                            t[i] = hit.t();
                            in[i] = ray.d().dot(hit.n()) > 0;
                            if (in[i]) {
                                inCount++;
                            }
                        }
                    }

                    while (true) {
                        double tFirst = Double.POSITIVE_INFINITY;
                        int iFirst = -1;
                        for (int i = 0; i < n; i++) {
                            if (t[i] < tFirst) {
                                tFirst = t[i];
                                iFirst = i;
                            }
                        }

                        if (iFirst == -1) {
                            return null;
                        }

                        if (in[iFirst]) {
                            if (inCount-- == n) {
                                return hits[iFirst];
                            }
                            in[iFirst] = false;
                        } else {
                            if (++inCount == n) {
                                return hits[iFirst];
                            }
                            in[iFirst] = true;
                        }

                        hits[iFirst] = solids[iFirst].firstHit(ray, tFirst);
                        t[iFirst] = hits[iFirst] == null ? Double.POSITIVE_INFINITY : hits[iFirst].t();
                    }
                }
                return null;
            }
        };
        return s;
    }

    public static Solid difference(Solid... solids) {
        Solid s = new CsgSolid() {
            @Override
            public Solid[] children() {
                return solids;
            }
            @Override
            public Hit firstHit(Ray ray, double afterTime) {
                if(ifBoxHit(ray, afterTime)) {
                    int n = solids.length;

                    boolean[] in = new boolean[n];
                    Hit[] hits = new Hit[n];
                    double[] t = new double[n];

                    int inCount = 0;

                    for (int i = 0; i < n; i++) {
                        Hit hit = solids[i].firstHit(ray, afterTime);
                        if (hit == null) {
                            t[i] = Double.POSITIVE_INFINITY;
                            in[i] = false;
                        } else {
                            hits[i] = hit;
                            t[i] = hit.t();
                            in[i] = ray.d().dot(hit.n()) > 0;
                            if (in[i]) {
                                inCount++;
                            }
                        }
                    }

                    while (true) {
                        double tFirst = Double.POSITIVE_INFINITY;
                        int iFirst = -1;
                        for (int i = 0; i < n; i++) {
                            if (t[i] < tFirst) {
                                tFirst = t[i];
                                iFirst = i;
                            }
                        }

                        if (iFirst == -1) {
                            return null;
                        }

                        if (iFirst == 0) {
                            if (!in[1]) {
                                return hits[0];
                            }
                            in[0] ^= true;
                        } else {
                            if (in[0]) {
                                return hits[1].inverse();
                            }
                            in[1] ^= true;
                        }

                        hits[iFirst] = solids[iFirst].firstHit(ray, tFirst);
                        t[iFirst] = hits[iFirst] == null ? Double.POSITIVE_INFINITY : hits[iFirst].t();
                    }
                }
                return null;
            }
        };
        return s;
    }
}
