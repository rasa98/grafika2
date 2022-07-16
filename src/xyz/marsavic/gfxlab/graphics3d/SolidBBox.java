package xyz.marsavic.gfxlab.graphics3d;

public abstract class SolidBBox implements Solid{
    protected BoundingBox bbox;

    public BoundingBox bbox(){
        return bbox;
    }

    @Override
    public SolidBBox transformed(Affine t) {
        return new SolidBBox() {
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
            public BoundingBox getBBox() {
                // bbox() ili bbox
                return SolidBBox.this.bbox;
            }
        };
    }
    //todo -> transplant this method from Solid
    //abstract protected BoundingBox getBBox();
}
