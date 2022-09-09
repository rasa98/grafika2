package xyz.marsavic.gfxlab.graphics3d.BVH;

import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Iterator;

public interface MyItr<T> extends Iterator<Solid> {
    public T get();

    static MyItr<Body> bodyItr(Iterator<Body> iter){
        return new MyItr<>() {
            Iterator<Body> it= iter;
            Body b;
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
            public void remove(){
                it.remove();
            }
        };
    }

    static MyItr<Solid> solidItr(Iterator<Solid> iter) {
        return new MyItr<>() {
            private Iterator<Solid> it = iter;
            private Solid s;
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
                Solid s = it.next();
                this.s = s;
                return s;
            }

            @Override
            public void remove(){
                it.remove();
            }
        };
    }
}


