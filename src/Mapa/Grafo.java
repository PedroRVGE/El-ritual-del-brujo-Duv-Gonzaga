package Mapa;

import java.util.*;

public class Grafo {
    private final List<Nodo> nodos = new ArrayList<>();
    private final List<Arista> aristas = new ArrayList<>();

    // ===== Construcción y acceso =====
    public void agregarNodo(int id, int victimas) { nodos.add(new Nodo(id, victimas)); }
    public void agregarArista(int u, int v, int d){ aristas.add(new Arista(u, v, d)); }
    public List<Nodo>  getNodos()  { return nodos; }
    public List<Arista> getAristas(){ return aristas; }

    public List<Arista> obtenerVecinos(int u){
        List<Arista> out = new ArrayList<>();
        for (Arista e : aristas) if (e.getOrigen()==u) out.add(e);
        return out;
    }

    // ===== Grafo aleatorio dirigido =====
    // Sin lazos (u==v), sin duplicadas u->v y sin reverso v->u para el mismo par.
    public void crearGrafoAleatorio(int n, int m){
        Random r = new Random();
        nodos.clear(); aristas.clear();

        for (int i=0;i<n;i++) agregarNodo(i, r.nextInt(11)); // víctimas 0..10

        Set<String> dir = new HashSet<>();                   // "u->v"
        Set<String> par = new HashSet<>();                   // "min<->max" bloquea reverso
        int creadas = 0;

        // Conectividad mínima si alcanza (cadena dirigida aleatoria)
        if (n>1 && m>=n-1){
            List<Integer> orden = new ArrayList<>();
            for (int i=0;i<n;i++) orden.add(i);
            Collections.shuffle(orden, r);
            for (int i=0;i<n-1 && creadas<m; i++){
                int u=orden.get(i), v=orden.get(i+1);
                if (u==v) continue;
                String d = u+"->"+v, p = Math.min(u,v)+"<->"+Math.max(u,v);
                if (!dir.contains(d) && !par.contains(p)){
                    agregarArista(u, v, 1+r.nextInt(20));
                    dir.add(d); par.add(p); creadas++;
                }
            }
        }
        // Completar
        while (creadas<m){
            int u=r.nextInt(n), v=r.nextInt(n);
            if (u==v) continue;
            String d = u+"->"+v, p = Math.min(u,v)+"<->"+Math.max(u,v);
            if (dir.contains(d) || par.contains(p)) continue;
            agregarArista(u, v, 1+r.nextInt(20));
            dir.add(d); par.add(p); creadas++;
        }
    }

    // ===== Resultado común =====
    public static class PathResult {
        public final List<Integer> path;  // nodos en orden
        public final int value;           // distancia total (Dijkstra) o víctimas totales (BF)
        public PathResult(List<Integer> p, int v){ this.path=p; this.value=v; }
    }

    // ===== Dijkstra: camino más corto (distancia) =====
    public PathResult dijkstra(int inicio, int guarida){
        int n = nodos.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[inicio]=0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        pq.add(new int[]{inicio,0});

        while(!pq.isEmpty()){
            int[] cur = pq.poll();
            int u=cur[0], d=cur[1];
            if (d!=dist[u]) continue;
            if (u==guarida) break;

            for (Arista e: obtenerVecinos(u)){
                int v=e.getDestino(), nd=d+e.getDistancia();
                if (nd<dist[v]){
                    dist[v]=nd; parent[v]=u;
                    pq.add(new int[]{v, nd});
                }
            }
        }
        return new PathResult(reconstruir(parent,inicio,guarida), dist[guarida]);
    }

    private List<Integer> reconstruir(int[] parent,int s,int t){
        LinkedList<Integer> p=new LinkedList<>();
        if (s==t){ p.add(s); return p; }
        if (t<0||t>=parent.length||parent[t]==-1) return p;
        for (int cur=t; cur!=-1; cur=parent[cur]){ p.addFirst(cur); if (cur==s) break; }
        if (p.isEmpty() || p.getFirst()!=s) p.clear();
        return p;
    }

    // ===== Bellman-Ford "max víctimas" con bloqueo de nodos repetidos =====
    // Peso de una arista u->v = víctimas(v). Se suma también víctimas(inicio).
    // Para NO contar dos veces una aldea, evitamos cerrar ciclos: si v ya está en el
    // camino reconstruible de u (v aparece en la cadena parent subyacente), no relajamos u->v.
    public PathResult maxVictimasBellmanFord(int inicio, int guarida){
        int n = nodos.size();
        final int NEG = Integer.MIN_VALUE/4;

        int[] best = new int[n];     // mejor puntaje (víctimas) para llegar a i
        int[] parent = new int[n];   // padre para reconstrucción
        Arrays.fill(best, NEG);
        Arrays.fill(parent, -1);

        // Punto de partida: cuenta víctimas del inicio
        best[inicio] = nodos.get(inicio).getVictimas();

        // n-1 iteraciones
        for (int iter=0; iter<n-1; iter++){
            boolean changed = false;
            for (Arista e : aristas){
                int u=e.getOrigen(), v=e.getDestino();
                if (best[u]==NEG) continue;

                // evita repetir nodos en el camino (simple path constraint ligero)
                if (apareceEnCadena(parent, u, v)) continue;

                int cand = best[u] + nodos.get(v).getVictimas(); // sumar v la 1ª vez
                if (cand > best[v]){
                    best[v] = cand;
                    parent[v] = u;
                    changed = true;
                }
            }
            if (!changed) break;
        }

        List<Integer> path = reconstruir(parent, inicio, guarida);
        return new PathResult(path, best[guarida]);
    }

    // ¿v ya aparece en la cadena de padres desde u hacia el inicio?
    // (si aparece, relajar u->v introduciría un nodo repetido => doble conteo)
    private boolean apareceEnCadena(int[] parent, int u, int v){
        if (u==v) return true;
        int cur = u;
        while (cur!=-1){
            if (cur==v) return true;
            cur = parent[cur];
        }
        return false;
    }
}

