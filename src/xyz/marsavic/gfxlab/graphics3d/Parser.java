package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.solids.SmoothTriangle;
import xyz.marsavic.gfxlab.graphics3d.solids.Triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Parser {
    List<Vec3> vertices;

//    Map<String, List<Vec3>> groupToVertexList;
    List<Vec3> vertNormals;
    Map<String, List<Triangle>> groups;

    String lastGroup;

    private Parser(InputStream file){
        vertices = new ArrayList<>();
        vertNormals = new ArrayList<>();
        groups = new HashMap<>();
//        groupToVertexList = new HashMap<>();
        parseObjFile(file);
    }

    public static Map<String, List<Triangle>> getTriMeshes(InputStream file){
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
        List<Triangle> l= groups.get(lastGroup);
        l.add(Triangle.p123(vertices.get(a-1), vertices.get(b-1), vertices.get(c-1)));
    }
    private void addSmoothTriangle(int a, int b, int c, int na, int nb, int nc){
        if(lastGroup == null)
            addGroup("defaultSmooth");
        List<Triangle> l= groups.get(lastGroup);
        l.add(SmoothTriangle.p123(vertices.get(a-1), vertices.get(b-1), vertices.get(c-1),
                                  vertNormals.get(na - 1), vertNormals.get(nb - 1), vertNormals.get(nc - 1)));
    }
    private void addGroup(String s){
        List<Triangle> l = new ArrayList<>();
        lastGroup = s;
        groups.put(s, l);
    }

    private Collection<List<Triangle>> ParsedFileToGroups(){
        return groups.values();
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
                        // refactor Ako face ima vise od 3, splituje se vise puta isto... u hashmap-u ili nesto tako
                        int numTri = words.length - 1 - 2;
                        for(int i=0; i< numTri;i++){
                            String [] v1_info = words[1].split("[/]");
                            int v1 = Integer.parseInt(v1_info[0]);
                            int nv1 = Integer.parseInt(v1_info[2]);

                            String [] v2_info = words[i+2].split("[/]");
                            int v2 = Integer.parseInt(v2_info[0]);
                            int nv2 = Integer.parseInt(v2_info[2]);

                            String [] v3_info = words[i+3].split("[/]");
                            int v3 = Integer.parseInt(v3_info[0]);
                            int nv3 = Integer.parseInt(v3_info[2]);

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


