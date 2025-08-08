package Mapa;

import java.util.*;

public class Grafo {
    private final List<Nodo> nodos = new ArrayList<>();
    private final List<Arista> aristas = new ArrayList<>();

    // ==== Construcción del grafo ====
    public void agregarNodo(int id, int victimas) { nodos.add(new Nodo(id, victimas)); }
    public void agregarArista(int origen, int destino, int distancia) { aristas.add(new Arista(origen, destino, distancia)); }

    public List<Nodo> getNodos() { return nodos; }
    public List<Arista> getAristas() { return aristas; }

    public List<Arista> obtenerVecinos(int nodoId) {
        List<Arista> vecinos = new ArrayList<>();
        for (Arista a : aristas) if (a.getOrigen() == nodoId) vecinos.add(a);
        return vecinos;
    }

    // ==== Grafo aleatorio: nodos con víctimas 0..10 y m aristas sin duplicados ====
    // Si m >= n-1 asegura conectividad básica (cadena aleatoria) y luego agrega extras.
    public void crearGrafoAleatorio(int n, int m) {
        Random random = new Random();

        nodos.clear();
        aristas.clear();

        for (int i = 0; i < n; i++) {
            int victimas = random.nextInt(11);
            this.agregarNodo(i, victimas);
        }

        Set<String> conexiones = new HashSet<>();
        int creadas = 0;

        if (n > 1 && m >= n - 1) {
            List<Integer> orden = new ArrayList<>();
            for (int i = 0; i < n; i++) orden.add(i);
            Collections.shuffle(orden, random);
            for (int i = 0; i < n - 1 && creadas < m; i++) {
                int u = orden.get(i), v = orden.get(i + 1);
                if (u == v) continue;
                String key = u + "->" + v;
                if (conexiones.add(key)) {
                    int dist = 1 + random.nextInt(20);
                    agregarArista(u, v, dist);
                    creadas++;
                }
            }
        }

        while (creadas < m) {
            int u = random.nextInt(n), v = random.nextInt(n);
            if (u == v) continue;
            String key = u + "->" + v;
            if (conexiones.add(key)) {
                int dist = 1 + random.nextInt(20);
                agregarArista(u, v, dist);
                creadas++;
            }
        }
    }

    // ==== Resultado genérico de camino ====
    public static class PathResult {
        public final List<Integer> path;
        public final int value; // distancia total (Dijkstra) o víctimas totales (Bellman-Ford)
        public PathResult(List<Integer> path, int value) { this.path = path; this.value = value; }
    }

    // ==== Dijkstra: camino más corto ====
    public PathResult dijkstra(int start, int target) {
        int n = nodos.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0], d = cur[1];
            if (d != dist[u]) continue;
            if (u == target) break;

            for (Arista e : obtenerVecinos(u)) {
                int v = e.getDestino();
                int nd = d + e.getDistancia();
                if (nd < dist[v]) {
                    dist[v] = nd;
                    parent[v] = u;
                    pq.add(new int[]{v, nd});
                }
            }
        }

        List<Integer> path = reconstruir(parent, start, target);
        return new PathResult(path, dist[target]);
    }

    private List<Integer> reconstruir(int[] parent, int start, int target) {
        LinkedList<Integer> path = new LinkedList<>();
        if (start == target) { path.add(start); return path; }
        if (target < 0 || target >= parent.length) return path;
        if (parent[target] == -1) return path;
        int cur = target;
        path.addFirst(cur);
        while (cur != start) {
            cur = parent[cur];
            if (cur == -1) { path.clear(); return path; }
            path.addFirst(cur);
        }
        return path;
    }

    // ==== Bellman-Ford adaptado: maximizar víctimas SIN contar doble ====
    public PathResult maxVictimasBellmanFord(int start, int target) {
        int n = nodos.size();
        int NEG_INF = Integer.MIN_VALUE / 4;

        int[] score = new int[n];
        int[] parent = new int[n];
        BitSet[] visitedSet = new BitSet[n];

        Arrays.fill(score, NEG_INF);
        Arrays.fill(parent, -1);
        for (int i = 0; i < n; i++) visitedSet[i] = new BitSet(n);

        score[start] = 0;
        visitedSet[start].set(start); // evita contarlo luego

        boolean updated;
        for (int it = 0; it < n - 1; it++) {
            updated = false;
            for (Arista e : aristas) {
                int u = e.getOrigen(), v = e.getDestino();
                if (score[u] == NEG_INF) continue;
                if (visitedSet[u].get(v)) continue; // no repetir nodos

                int gain = nodos.get(v).getVictimas(); // solo 1ª vez
                int cand = score[u] + gain;
                if (cand > score[v]) {
                    score[v] = cand;
                    parent[v] = u;
                    BitSet bs = (BitSet) visitedSet[u].clone();
                    bs.set(v);
                    visitedSet[v] = bs;
                    updated = true;
                }
            }
            if (!updated) break;
        }

        List<Integer> path = reconstruir(parent, start, target);
        return new PathResult(path, score[target]);
    }
}

