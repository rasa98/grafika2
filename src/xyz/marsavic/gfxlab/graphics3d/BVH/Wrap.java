package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Collection;

public interface Wrap<T> extends Iterable<Solid> {
    int getSize();

    MyItr<T> iterator();

    static Wrap<Body> bodyCollection(Collection<Body> col){
        return new Wrap<>() {
            private Collection<Body> c = col;
            @Override
            public int getSize() {
                return c.size();
            }

            @Override
            public MyItr<Body> iterator() {
                return  new MyItr.MyBodyItr(col.iterator());
            }
        };
    }

    static  Wrap<Solid> solidCollection(Collection<Solid> col) {
        return new Wrap<>() {
            private Collection<Solid> c =col;
            @Override
            public int getSize() {
                return c.size();
            }

            @Override
            public MyItr<Solid> iterator() {
                return new MyItr.MySolidItr(col.iterator());
            }
        };
    }
}
