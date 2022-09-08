package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Deprecated
public class BVHSolid {

    private BoundingBox bbox;
    private Collection<Solid> solids;

    //Used only for root
    public Collection<Solid> outliers;
    private BVHSolid left, right;

    private BVHSolid(Collection<Solid> solids) {
        this.solids = solids;
    }

    private BVHSolid(BoundingBox bbox, Collection<Solid> rightSolids) {
        this(rightSolids);
        this.bbox = bbox;
    }

    public static BVHSolid makeBVH(Collection<Solid> solids, int amount) {
        BVHSolid root = new BVHSolid(new HashSet<>());
        BoundingBox localBBox = new BoundingBox();

        Set<Solid> NoBBoxSolids = new HashSet<>();
//        for(int i=solids.size() - 1; i>=0; i--){
//            Solid s = solids.get(i);
//            if(s instanceof HalfSpace){
//                NoBBoxSolids.add(s);
//                solids.remove(i);
//            }
//            else{
//                localBBox = localBBox.addBBox(s.bbox());
//            }
//        }
        for(Iterator<Solid> i = solids.iterator(); i.hasNext();) {
            Solid s = i.next();
            if(s instanceof HalfSpace){
                NoBBoxSolids.add(s);
                i.remove();
            }
            else{
                localBBox = localBBox.addBBox(s.bbox());
            }
        }

        root.bbox = localBBox;
        root.solids = solids;
        divideBVH(root, amount);
        root.outliers = NoBBoxSolids;
        return root;
    }

    private static void divideBVH(BVHSolid bvh, int amount) {
        if(bvh.solids.size() < amount){
            return;
        }
        BoundingBox leftHalf = bvh.bbox.getLeftHalf();

        Set<Solid> leftSolids = new HashSet<>();
        Set<Solid> rightSolids = new HashSet<>();
        BoundingBox left = new BoundingBox();
        BoundingBox right = new BoundingBox();

        Set<Solid> inMid = new HashSet<>();

        for(Iterator<Solid> i = bvh.solids.iterator(); i.hasNext();) {
            Solid s = i.next();
            BoundingBox.hasBBox e = leftHalf.hasBBox(s.bbox());

            switch (e){
                case FULL:
                    leftSolids.add(s);
                    left = left.addBBox(s.bbox());

                    break;
                case NONE:
                    rightSolids.add(s);
                    right = right.addBBox(s.bbox());
                    break;
                default:
                    inMid.add(s);
            }

            i.remove();
        }
        for(Solid s: inMid){
            if(leftSolids.size() >= rightSolids.size()){
                rightSolids.add(s);
                right = right.addBBox(s.bbox());
            }
            else{
                leftSolids.add(s);
                left = left.addBBox(s.bbox());
            }
        }

        bvh.left = new BVHSolid(left, leftSolids);
        bvh.right = new BVHSolid(right, rightSolids);

        divideBVH(bvh.left, amount);
        divideBVH(bvh.right, amount);
    }

    public Hit getHit(Ray ray, double epsilon) {
        Hit minH = null;
        if(bbox.rayHitsBox(ray, epsilon)){
            double minT = Double.MAX_VALUE;

            for(Solid s: solids) {
                Hit hit = s.firstHit(ray, epsilon);
                if(hit != null){
                    double hitT = hit.t();
                    if(hitT > 0 && hitT < minT){
                        minT = hitT;
                        minH = hit;
                    }
                }
            }
            if(left != null){
                Hit leftC = left.getHit(ray, epsilon);
                Hit rightC = right.getHit(ray, epsilon);

                if(leftC != null){
                    double t = leftC.t();
                    if(t > 0 && t < minT){
                        minT = t;
                        minH = leftC;
                    }
                }
                if(rightC != null){
                    double t = rightC.t();
                    if(t > 0 && t < minT){
                        minH = rightC;
                    }
                }
            }
        }
        return minH;
    }


    public boolean getCollisionIn01(Ray r, double epsilon) {
        throw new RuntimeException("Method getCollisionIn01 in BVHSolids class not implemented!!!");
    }
}
