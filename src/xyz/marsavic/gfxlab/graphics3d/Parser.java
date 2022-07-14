package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.solids.Triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Parser {
    List<Vec3> vertices;
    Map<String, List<Triangle>> groups;

    String lastGroup;

    private Parser(InputStream file){
        vertices = new ArrayList<>();
        groups = new HashMap<>();
        parseObjFile(file);
    }

    public static Collection<List<Triangle>> getTriMeshes(InputStream file){
        Parser p = new Parser(file);
        return p.ParsedFileToGroups();
    }

    private void addVert(double x, double y, double z){
        vertices.add(Vec3.xyz(x, y, z));
    }
    private void addTriangle(int a, int b, int c){
        if(lastGroup == null)
            addGroup("default");
        List<Triangle> l= groups.get(lastGroup);
        l.add(Triangle.p123(vertices.get(a-1), vertices.get(b-1), vertices.get(c-1)));
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
                        int numTri = words.length - 1 - 2;
                        for(int i=0; i< numTri;i++){
                            int v1 = Integer.parseInt(words[1].split("[/]")[0]);
                            int v2 = Integer.parseInt(words[i+2].split("[/]")[0]);
                            int v3 = Integer.parseInt(words[i+3].split("[/]")[0]);
                            addTriangle(v1, v2, v3);
                        }
                    }else if(words[0].equals("g")){
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


