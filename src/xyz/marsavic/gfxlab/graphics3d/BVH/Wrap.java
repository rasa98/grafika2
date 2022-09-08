package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Collection;

public abstract class Wrap<T> implements Iterable<Solid> {
    Collection<T> col;

    public Wrap(Collection<T> col){
        this.col = col;
    }

    public abstract MyItr<T> iterator();

    static class BodyCol extends Wrap<Body> {

        public BodyCol(Collection<Body> col) {
            super(col);
        }

        @Override
        public MyItr<Body> iterator() {
            return new MyItr.MyBodyItr(col.iterator());
        }
    }

    static class SolidCol extends Wrap<Solid> {

        public SolidCol(Collection<Solid> col) {
            super(col);
        }

        @Override
        public MyItr<Solid> iterator() {
            return new MyItr.MySolidItr(col.iterator());
        }
    }
}
