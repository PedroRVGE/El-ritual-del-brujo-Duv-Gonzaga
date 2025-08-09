import ImagenGrafo.ExportadorDot;
import Mapa.Grafo;

import java.util.*;

public class Main {

    private static int distanciaDeCamino(Grafo g, List<Integer> path) {
        if (path == null || path.size() < 2) return 0;
        int total = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            int u = path.get(i), v = path.get(i + 1);
            for (Mapa.Arista e : g.getAristas()) {
                if (e.getOrigen() == u && e.getDestino() == v) { total += e.getDistancia(); break; }
            }
        }
        return total;
    }

    private static int victimasDeCamino(Grafo g, List<Integer> path) {
        if (path == null || path.isEmpty()) return 0;
        Set<Integer> visitados = new HashSet<>();
        int total = 0;
        for (int v : path) if (visitados.add(v)) total += g.getNodos().get(v).getVictimas();
        return total;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese # nodos: ");   int n = sc.nextInt();
        System.out.print("Ingrese # aristas: "); int m = sc.nextInt();

        int inicio = 0;
        int guarida = n - 1;

        Grafo g = new Grafo();
        g.crearGrafoAleatorio(n, m);

        var corto = g.dijkstra(inicio, guarida);
        var vict  = g.maxVictimasBellmanFord(inicio, guarida);

        if (corto.path.isEmpty()) { System.out.println("Dijkstra: NO hay camino."); return; }
        if (vict.path.isEmpty())   { System.out.println("Bellman-Ford: NO hay camino."); return; }

        int distCorto = distanciaDeCamino(g, corto.path);
        int victCorto = victimasDeCamino(g, corto.path);
        int distVict  = distanciaDeCamino(g, vict.path);
        int victVict  = victimasDeCamino(g, vict.path);

        System.out.println("\n=== Resultados ===");
        System.out.println("Inicio: " + inicio + " | Guarida: " + guarida);
        System.out.println("Dijkstra  -> " + corto.path + " | Distancia=" + distCorto + " | Víctimas=" + victCorto);
        System.out.println("Bellman   -> " + vict.path  + " | Distancia=" + distVict  + " | Víctimas=" + victVict);

        ExportadorDot.exportarYMostrar(
                g, "grafo.dot", "grafo.png",
                corto.path, vict.path, inicio, guarida
        );
    }
}



