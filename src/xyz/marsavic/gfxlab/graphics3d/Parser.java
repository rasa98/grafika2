package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.solids.SmoothTriangle;
import xyz.marsavic.gfxlab.graphics3d.solids.Triangle;
import java.io.InputStream;
import java.util.*;


public class Parser {
    List<Vec3> vertices;
    List<Vec3> vertNormals;
    Map<String, Collection<Solid>> groups;

    String lastGroup;

    private Parser(InputStream file){
        vertices = new ArrayList<>();
        vertNormals = new ArrayList<>();
        groups = new HashMap<>();
        parseObjFile(file);
    }

    public static Map<String, Collection<Solid>> getTriMeshes(InputStream file){
        Parser p = new Parser(file);
        return p.groups;
    }

    private void addVert(double x, double y, double z){
        vertices.add(Vec3.xyz(x, y, z));
    }
    private void addVertNormals(double x, double y, double z){
        vertNormals.add(Vec3.xyz(x, y, z));
    }
    private void addTriangle(int a, int b, int c){
        if(lastGroup == null)
            addGroup("default");
        Collection<Solid> l= groups.get(lastGroup);
        l.add(Triangle.p123(vertices.get(a-1), vertices.get(b-1), vertices.get(c-1)));
    }
    private void addSmoothTriangle(int a, int b, int c, int na, int nb, int nc){
        if(lastGroup == null)
            addGroup("defaultSmooth");
        Collection<Solid> l= groups.get(lastGroup);
        l.add(SmoothTriangle.p123(vertices.get(a-1), vertices.get(b-1), vertices.get(c-1),
                                  vertNormals.get(na - 1), vertNormals.get(nb - 1), vertNormals.get(nc - 1)));
    }
    private void addGroup(String s){
        Collection<Solid> l = new HashSet<>();
        lastGroup = s;
        groups.put(s, l);
    }

    private void parseObjFile(InputStream file){
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] words = sc.nextLine().split("\\s+");
                if(words.length != 0){
                    if(words[0].equals("v")){
                        double v1 = Double.parseDouble(words[1]);
                        double v2 = Double.parseDouble(words[2]);
                        double v3 = Double.parseDouble(words[3]);
                        addVert(v1, v2, v3);
                    }else if(words[0].equals("f")){
                        // -1 ako je izostavljena inf
                        int numTri = words.length - 1 - 2;

                        int[][] facePoints = new int[words.length - 1][3];
                        for(int i=1; i<words.length; i++){
                            String[] fPointData = words[i].split("[/]");
                            for(int j=0;j<3;j++){
                                facePoints[i-1][j] = fPointData[j].equals("")? -1 : Integer.parseInt(fPointData[j]);
                            }
                        }

                        for(int i=0; i< numTri;i++){
                            int v1 = facePoints[0][0];
                            int nv1 = facePoints[0][2];

                            int v2 = facePoints[i+1][0];
                            int nv2 = facePoints[i+1][2];

                            int v3 = facePoints[i+2][0];
                            int nv3 = facePoints[i+2][2];

                            addSmoothTriangle(v1, v2, v3, nv1, nv2, nv3);
                        }

                    }else if(words[0].equals("vn")){
                        double v1 = Double.parseDouble(words[1]);
                        double v2 = Double.parseDouble(words[2]);
                        double v3 = Double.parseDouble(words[3]);
                        addVertNormals(v1, v2, v3);
                    }
                    else if(words[0].equals("g")){
                        addGroup(words[1]);
                    }
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(sc != null){
                sc.close();
            }
        }
    }
}


