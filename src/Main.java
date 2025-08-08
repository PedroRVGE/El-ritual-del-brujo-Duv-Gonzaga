import ImagenGrafo.ExportadorDot;
import Mapa.Grafo;

import java.util.*;

public class Main {

    // Helpers solo para mostrar métricas reales de un camino
    private static int distanciaDeCamino(Grafo g, List<Integer> path) {
        if (path.size() < 2) return 0;
        int total = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            int u = path.get(i), v = path.get(i + 1);
            boolean found = false;
            for (Mapa.Arista e : g.getAristas()) {
                if (e.getOrigen() == u && e.getDestino() == v) {
                    total += e.getDistancia(); found = true; break;
                }
            }
            if (!found) return Integer.MAX_VALUE; // defensivo
        }
        return total;
    }

    private static int victimasDeCamino(Grafo g, List<Integer> path) {
        Set<Integer> seen = new HashSet<>();
        int total = 0;
        for (int i = 1; i < path.size(); i++) { // NO contamos el start
            int v = path.get(i);
            if (seen.add(v)) total += g.getNodos().get(v).getVictimas();
        }
        return total;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese # nodos: ");
        int n = sc.nextInt();
        System.out.print("Ingrese # aristas: ");
        int m = sc.nextInt();

        int start = 0;
        int lair  = n - 1;

        Grafo g = new Grafo();

        // Reintenta hasta conseguir DOS caminos distintos (máx 50 intentos)
        Grafo.PathResult corto = null, vict = null;
        int intentos = 0;
        while (intentos < 50) {
            g.crearGrafoAleatorio(n, m);
            corto = g.dijkstra(start, lair);
            vict  = g.maxVictimasBellmanFord(start, lair);

            if (!corto.path.isEmpty() && !vict.path.isEmpty() && !corto.path.equals(vict.path)) {
                break;
            }
            intentos++;
        }

        if (corto == null || corto.path.isEmpty()) {
            System.out.println("Dijkstra: NO hay camino de " + start + " a " + lair);
            return;
        }
        if (vict == null || vict.path.isEmpty()) {
            System.out.println("Bellman-Ford: NO hay camino de " + start + " a " + lair);
            return;
        }

        int distCorto = distanciaDeCamino(g, corto.path);
        int victCorto = victimasDeCamino(g, corto.path);
        int distVict  = distanciaDeCamino(g, vict.path);
        int victVict  = victimasDeCamino(g, vict.path);

        System.out.println("Intentos para caminos distintos: " + (intentos + 1));
        System.out.println("Dijkstra  -> path " + corto.path + " | dist=" + distCorto + " | victimas=" + victCorto);
        System.out.println("Bellman   -> path " + vict.path  + " | dist=" + distVict  + " | victimas=" + victVict);

        String ts = String.valueOf(System.currentTimeMillis());
        ExportadorDot.exportarYMostrar(
                g,
                "grafo_" + ts + ".dot",
                "grafo_" + ts + ".png",
                corto.path,
                vict.path,
                start, lair
        );
    }
}
