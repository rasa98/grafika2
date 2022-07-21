package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import java.util.*;

public class BVHSolid {

    private BoundingBox bbox;
    private List<SolidBBox> solids;

    //Used only for root
    public List<SolidBBox> outliers;

    private BVHSolid left, right;

    private BVHSolid() {
        this.solids = new ArrayList<>();
    }

    private BVHSolid(List<SolidBBox> solids) {
        this.solids = solids;
    }

    private BVHSolid(BoundingBox bbox) {
        this();
        this.bbox = bbox;
    }

    private BVHSolid(BoundingBox bbox, BVHSolid left, BVHSolid right) {
        this();
        this.bbox = bbox;
        this.left = left;
        this.right = right;
    }

    private BVHSolid(BoundingBox bbox, List<SolidBBox> solids, BVHSolid left, BVHSolid right) {
        this.bbox = bbox;
        this.solids = solids;
        this.left = left;
        this.right = right;
    }

    public static BVHSolid makeBVH(List<SolidBBox> solids, int amount) {
        BVHSolid root = new BVHSolid(new ArrayList<SolidBBox>());
//        Body[] b = bodies.toArray(new Body[0]);
        BoundingBox localBBox = new BoundingBox();

        List<SolidBBox> NoBBoxSolids = new ArrayList<>();
        for(int i=solids.size() - 1; i>=0; i--){
            SolidBBox s = solids.get(i);
            if(s instanceof HalfSpace){
                NoBBoxSolids.add(s);
                solids.remove(i);
            }
            else{
                localBBox = localBBox.addBBox(s.getBBox());
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
        BoundingBox[] children = bvh.bbox.splitBox();
        bvh.left = new BVHSolid(children[0]);
        bvh.right = new BVHSolid(children[1]);

        for(int i = bvh.solids.size() - 1; i>=0; i--){
            SolidBBox s = bvh.solids.get(i);
            BoundingBox.hasBBox e = bvh.left.bbox.hasBBox(s.getBBox());
            switch (e){
                case Full:
                    bvh.left.solids.add(s);
                    bvh.solids.remove(i);
                    break;
                case None:
                    bvh.right.solids.add(s);
                    bvh.solids.remove(i);
                    break;
            }
        }
        divideBVH(bvh.left, amount);
        divideBVH(bvh.right, amount);
    }

    public Hit getHit(Ray ray, double epsilon) {
        Hit minH = null;
        if(bbox.rayHitsBox(ray, epsilon)){
            double minT = Double.MAX_VALUE;

            for(SolidBBox s: solids) {
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
                        minT = t;
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
