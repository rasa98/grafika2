package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Iterator;

public abstract class MyItr<T> implements Iterator<Solid> {

    private T e;
    Iterator<T> it;

    public MyItr(Iterator<T> it) {
        this.it = it;
    }

    public T getE() {
        return e;
    }

    public void setE(T e) {
        this.e = e;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public void remove() {
        it.remove();
    }

    static class MyBodyItr extends  MyItr<Body>{

        public MyBodyItr(Iterator<Body> it) {
            super(it);
        }

        @Override
        public Solid next() {
            Body b = it.next();
            setE(b);
            Solid s = b.solid();
            return s;
        }
    }

    static class MySolidItr extends  MyItr<Solid>{

        public MySolidItr(Iterator<Solid> it) {
            super(it);
        }

        @Override
        public Solid next() {
            Solid s = it.next();
            setE(s);
            return getE();
        }
    }
}


