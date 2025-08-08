import ImagenGrafo.ExportadorDot;
import Mapa.Grafo;

import java.util.*;

public class Main {

    // ---- Helpers de métricas ----
    private static int distanciaDeCamino(Grafo g, List<Integer> path) {
        if (path == null || path.size() < 2) return 0;
        int total = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            int u = path.get(i), v = path.get(i + 1);
            boolean found = false;
            for (Mapa.Arista e : g.getAristas()) {
                if (e.getOrigen() == u && e.getDestino() == v) {
                    total += e.getDistancia();
                    found = true;
                    break;
                }
            }
            if (!found) return Integer.MAX_VALUE; // defensivo
        }
        return total;
    }

    // Incluye víctimas del inicio y evita doble conteo
    private static int victimasDeCamino(Grafo g, List<Integer> path) {
        if (path == null || path.isEmpty()) return 0;
        Set<Integer> visitados = new HashSet<>();
        int total = 0;
        for (int v : path) {
            if (visitados.add(v)) {
                total += g.getNodos().get(v).getVictimas();
            }
        }
        return total;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese # nodos: ");
        int n = sc.nextInt();
        System.out.print("Ingrese # aristas: ");
        int m = sc.nextInt();

        int inicio = 0;       // nodo de partida (puedes cambiarlo o pedirlo)
        int guarida = n - 1;  // nodo de llegada

        Grafo g = new Grafo();
        g.crearGrafoAleatorio(n, m);

        Grafo.PathResult corto = g.dijkstra(inicio, guarida);
        Grafo.PathResult vict  = g.maxVictimasBellmanFord(inicio, guarida);

        if (corto.path.isEmpty()) {
            System.out.println("Dijkstra: NO hay camino de " + inicio + " a " + guarida);
            return;
        }
        if (vict.path.isEmpty()) {
            System.out.println("Bellman-Ford: NO hay camino de " + inicio + " a " + guarida);
            return;
        }

        int distCorto = distanciaDeCamino(g, corto.path);
        int victCorto = victimasDeCamino(g, corto.path);
        int distVict  = distanciaDeCamino(g, vict.path);
        int victVict  = victimasDeCamino(g, vict.path);

        System.out.println("\n=== Resultados ===");
        System.out.println("Inicio: " + inicio + " | Guarida: " + guarida);

        System.out.println("Dijkstra (camino más corto): " + corto.path);
        System.out.println("  Distancia total = " + distCorto);
        System.out.println("  Víctimas en ese camino = " + victCorto);

        System.out.println("Bellman-Ford (máx víctimas): " + vict.path);
        System.out.println("  Víctimas totales = " + victVict);
        System.out.println("  Distancia de ese camino = " + distVict);

        // Ficheros fijos: se sobreescriben en cada ejecución
        ExportadorDot.exportarYMostrar(
                g,
                "grafo.dot",
                "grafo.png",
                corto.path,
                vict.path,
                inicio,
                guarida
        );
    }
}
