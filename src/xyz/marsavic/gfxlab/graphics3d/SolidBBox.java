package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;

public abstract class SolidBBox implements Solid{
    private BoundingBox bbox;

    protected abstract BoundingBox calculateBBox();

    public BoundingBox getBBox(){
        return bbox;
    }

    public void setBBox(BoundingBox bbox){
        this.bbox = bbox;
    }

    @Override
    public SolidBBox transformed(Affine t) {
        SolidBBox res = new SolidBBox() {
            private final Affine tInv = t.inverse();
            private final Affine tInvTransposed = tInv.transposeWithoutTranslation();

            @Override
            public Hit firstHit(Ray ray, double afterTime) {
                Ray rayO = tInv.applyTo(ray);
                Hit hitO = SolidBBox.this.firstHit(rayO, afterTime);
                if (hitO == null) {
                    return null;
                }
                return hitO.withN(tInvTransposed.applyTo(hitO.n()));
            }

            @Override
            protected BoundingBox calculateBBox() {
//                throw new RuntimeException();
                BoundingBox bb = SolidBBox.this.bbox;
                Vec3 p1 = bb.box.p();
                Vec3 p2 = bb.box.q();
                Vec3 p3 = Vec3.xyz(bb.box.p().x(), bb.box.p().y(), bb.box.q().z());
                Vec3 p4 = Vec3.xyz(bb.box.p().x(), bb.box.q().y(), bb.box.p().z());
                Vec3 p5 = Vec3.xyz(bb.box.p().x(), bb.box.q().y(), bb.box.q().z());
                Vec3 p6 = Vec3.xyz(bb.box.q().x(), bb.box.p().y(), bb.box.p().z());
                Vec3 p7 = Vec3.xyz(bb.box.q().x(), bb.box.p().y(), bb.box.q().z());
                Vec3 p8 = Vec3.xyz(bb.box.q().x(), bb.box.q().y(), bb.box.p().z());

                return bb.addPoint(t.applyTo(p1)).addPoint(t.applyTo(p2)).addPoint(t.applyTo(p3)).addPoint(t.applyTo(p4))
                        .addPoint(t.applyTo(p5)).addPoint(t.applyTo(p6)).addPoint(t.applyTo(p7)).addPoint(t.applyTo(p8));
//                return SolidBBox.this.getBBox();
            }
        };
        res.bbox = res.calculateBBox();
//        SolidBBox.this.bbox = cal
        return res;
    }

    abstract static class CsgSolid extends SolidBBox {
        abstract SolidBBox[] children();

		@Override
        protected BoundingBox calculateBBox() {
			// to do
			BoundingBox bbox = new BoundingBox();
			for( SolidBBox c : children()){
				bbox.addBBox(c.getBBox());
			}

			return bbox;
		}
    }

    static SolidBBox union(SolidBBox... solids) {
        SolidBBox s = new CsgSolid() {

                @Override
                public SolidBBox[] children() {
                    return solids;
                }

                @Override
                public Hit firstHit(Ray ray, double afterTime) {
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
            };
        s.setBBox(s.calculateBBox());
        return s;
    }


    static SolidBBox intersection(SolidBBox... solids) {
        SolidBBox s = new CsgSolid() {
            @Override
            public SolidBBox[] children() {
                return solids;
            }

            @Override
            public Hit firstHit(Ray ray, double afterTime) {
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
        };
        s.setBBox(s.calculateBBox());
        return s;
    }

    static SolidBBox difference(SolidBBox... solids) {
        SolidBBox s = new CsgSolid() {
            @Override
            public SolidBBox[] children() {
                return solids;
            }
            @Override
            public Hit firstHit(Ray ray, double afterTime) {
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
        };
        s.setBBox(s.calculateBBox());
        return s;
    }
}
