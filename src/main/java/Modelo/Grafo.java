package Modelo;

import java.util.*;

public class Grafo {
    private final Map<String, Map<String, Double>> adyacencia = new LinkedHashMap<>();

    public void agregarNodo(String id) {
        validarId(id);
        adyacencia.computeIfAbsent(id.trim(), k -> new LinkedHashMap<>());
    }

    /** Elimina todos los nodos y conexiones para iniciar un grafo vacío. */
    public void limpiar() {
        adyacencia.clear();
    }

    public void agregarArista(String origen, String destino, double peso) {
        origen = normalizar(origen); destino = normalizar(destino);
        if (peso < 0 || !Double.isFinite(peso)) throw new IllegalArgumentException("El peso debe ser un numero positivo.");
        agregarNodo(origen); agregarNodo(destino);
        adyacencia.get(origen).put(destino, peso);
        adyacencia.get(destino).put(origen, peso);
    }

    public Set<String> nodos() { return Collections.unmodifiableSet(adyacencia.keySet()); }
    public Map<String, Map<String, Double>> aristas() { return Collections.unmodifiableMap(adyacencia); }

    public Resultado caminoMasCorto(String origen, String destino) {
        comprobarNodos(origen, destino);
        Map<String, Double> dist = new HashMap<>(); Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> cola = new PriorityQueue<>(Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.POSITIVE_INFINITY)));
        for (String n : adyacencia.keySet()) dist.put(n, Double.POSITIVE_INFINITY);
        dist.put(origen, 0d); cola.add(origen);
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(destino)) break;
            for (var e : adyacencia.get(actual).entrySet()) {
                double nueva = dist.get(actual) + e.getValue();
                if (nueva < dist.get(e.getKey())) { dist.put(e.getKey(), nueva); prev.put(e.getKey(), actual); cola.remove(e.getKey()); cola.add(e.getKey()); }
            }
        }
        return dist.get(destino).isInfinite() ? Resultado.inalcanzable() : new Resultado(reconstruir(origen, destino, prev), dist.get(destino));
    }

    /** Camino simple de mayor peso; la búsqueda exhaustiva es apropiada para grafos de tamaño didáctico. */
    public Resultado caminoMasLargo(String origen, String destino) {
        comprobarNodos(origen, destino);
        Mejor mejor = new Mejor();
        buscarLargo(origen, destino, new HashSet<>(), new ArrayList<>(), 0, mejor);
        return mejor.camino == null ? Resultado.inalcanzable() : new Resultado(mejor.camino, mejor.distancia);
    }

    private void buscarLargo(String actual, String destino, Set<String> vistos, List<String> camino, double distancia, Mejor mejor) {
        vistos.add(actual); camino.add(actual);
        if (actual.equals(destino)) {
            if (mejor.camino == null || distancia > mejor.distancia) { mejor.distancia = distancia; mejor.camino = List.copyOf(camino); }
        } else for (var e : adyacencia.get(actual).entrySet()) if (!vistos.contains(e.getKey()))
            buscarLargo(e.getKey(), destino, vistos, camino, distancia + e.getValue(), mejor);
        camino.remove(camino.size()-1); vistos.remove(actual);
    }
    private static class Mejor { List<String> camino; double distancia; }
    private void comprobarNodos(String a, String b) {
        a = normalizar(a); b = normalizar(b);
        if (!adyacencia.containsKey(a) || !adyacencia.containsKey(b)) throw new IllegalArgumentException("El origen y el destino deben existir en el grafo.");
    }
    private static List<String> reconstruir(String a, String b, Map<String,String> prev) {
        LinkedList<String> p = new LinkedList<>();
        for (String n=b; n!=null; n=prev.get(n)) { p.addFirst(n); if(n.equals(a)) break; }
        return p;
    }
    private static void validarId(String id) { if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("El nodo no puede estar vacío."); }
    private static String normalizar(String id) { validarId(id); return id.trim(); }
    public static class Resultado {
        private final List<String> camino; private final double distancia;
        public Resultado(List<String> camino, double distancia) { this.camino=List.copyOf(camino); this.distancia=distancia; }
        private static Resultado inalcanzable() { return new Resultado(List.of(), Double.POSITIVE_INFINITY); }
        public List<String> camino() { return camino; } public double distancia() { return distancia; }
        public boolean alcanzable() { return !camino.isEmpty(); }
    }
}
