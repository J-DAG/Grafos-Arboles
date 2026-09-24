package Modelo;

import java.util.*;

/** Árbol general con un único padre por nodo. */
public class Arbol {
    private final Map<String, List<String>> hijos = new LinkedHashMap<>();
    private final Map<String, String> padre = new LinkedHashMap<>();
    private String raiz;

    public void agregarRelacion(String padreId, String hijoId) {
        String p=limpiar(padreId), h=limpiar(hijoId);
        if (p.equals(h)) throw new IllegalArgumentException("Un nodo no puede ser su propio padre.");
        if (raiz != null && !hijos.containsKey(p)) throw new IllegalArgumentException("El padre debe pertenecer al árbol configurado.");
        if (padre.containsKey(h)) throw new IllegalArgumentException("El nodo '"+h+"' ya tiene padre.");
        if (seriaCiclo(p,h)) throw new IllegalArgumentException("La relación produciría un ciclo.");
        hijos.computeIfAbsent(p,k->new ArrayList<>()); hijos.computeIfAbsent(h,k->new ArrayList<>());
        if (raiz == null) raiz=p;
        if (raiz.equals(h)) throw new IllegalArgumentException("La raíz no puede convertirse en hija.");
        hijos.get(p).add(h); padre.put(h,p);
    }
    private boolean seriaCiclo(String p,String h) { for(String n=p;n!=null;n=padre.get(n)) if(n.equals(h)) return true; return false; }
    public Set<String> nodos() { return Collections.unmodifiableSet(hijos.keySet()); }
    public List<String> hijos(String n) { return List.copyOf(hijos.getOrDefault(n,List.of())); }
    public String raiz() { return raiz; }

    public List<String> camino(String origen, String destino) {
        origen=limpiar(origen); destino=limpiar(destino);
        if (!hijos.containsKey(origen) || !hijos.containsKey(destino)) throw new IllegalArgumentException("El origen y el destino deben existir en el árbol.");
        Map<String,String> ancestros = new HashMap<>();
        ArrayList<String> pa=new ArrayList<>();
        for(String n=origen;n!=null;n=padre.get(n)) pa.add(n);
        ArrayList<String> pb=new ArrayList<>();
        for(String n=destino;n!=null;n=padre.get(n)) pb.add(n);
        int i=pa.size()-1,j=pb.size()-1;
        if (!pa.get(i).equals(pb.get(j))) return List.of();
        while(i>0 && j>0 && pa.get(i-1).equals(pb.get(j-1))) { i--; j--; }
        ArrayList<String> ruta=new ArrayList<>(pa.subList(0,i));
        ArrayList<String> tramo=new ArrayList<>(pb.subList(0,j+1));
        Collections.reverse(tramo);
        ruta.addAll(tramo);
        return ruta;
    }
    private static String limpiar(String s) { if(s==null||s.trim().isEmpty()) throw new IllegalArgumentException("El nombre del nodo no puede estar vacío."); return s.trim(); }
}
