package ImagenGrafo;

import Mapa.Arista;
import Mapa.Grafo;
import Mapa.Nodo;

import java.awt.Desktop;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportadorDot {

    // API con resaltado y marcando inicio/guarida (en español dentro del label)
    public static void exportarYMostrar(Grafo grafo, String archivoDot, String archivoImagen,
                                        List<Integer> caminoCorto, List<Integer> caminoVictimas,
                                        int inicio, int guarida) {
        generarArchivoDot(grafo, archivoDot, caminoCorto, caminoVictimas, inicio, guarida);
        generarImagen(archivoDot, archivoImagen);
        abrirImagen(archivoImagen);
    }

    private static void generarArchivoDot(Grafo grafo, String archivoDot,
                                          List<Integer> caminoCorto, List<Integer> caminoVictimas,
                                          int inicio, int guarida) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoDot))) {
            writer.println("digraph G {");
            writer.println("rankdir=LR; node [shape=ellipse, fontname=\"Helvetica\"]; edge [fontname=\"Helvetica\"];");

            // Nodos: “Aldea X”, con [Inicio]/[Guarida] cuando aplique
            for (Nodo nodo : grafo.getNodos()) {
                int id = nodo.getId();
                String rol = (id == inicio) ? "\\n[Inicio]" : (id == guarida) ? "\\n[Guarida]" : "";
                writer.printf("%d [label=\"Aldea %d%s\\nVíctimas: %d\"];\n",
                        id, id, rol, nodo.getVictimas());
            }
            // Forma/estilo especial a inicio y guarida
            writer.printf("%d [shape=box, color=green, penwidth=2.0];\n", inicio);
            writer.printf("%d [shape=doublecircle, color=black, penwidth=2.0];\n", guarida);

            // Aristas coloreadas según los caminos
            Set<String> cortoEdges = pathEdges(caminoCorto);
            Set<String> victiEdges = pathEdges(caminoVictimas);

            for (Arista arista : grafo.getAristas()) {
                String key = arista.getOrigen() + "->" + arista.getDestino();
                String style;
                if (cortoEdges.contains(key) && victiEdges.contains(key)) {
                    style = "color=purple, penwidth=3.0";
                } else if (cortoEdges.contains(key)) {
                    style = "color=red, penwidth=3.0";
                } else if (victiEdges.contains(key)) {
                    style = "color=blue, penwidth=3.0";
                } else {
                    style = "color=gray";
                }
                writer.printf("%d -> %d [label=\"Dist: %d\", %s];\n",
                        arista.getOrigen(), arista.getDestino(), arista.getDistancia(), style);
            }

            // === Colores de caminos ===
            writer.println();
            writer.println("subgraph cluster_ {");
            writer.println("  label=\"Guia colores\"; fontsize=12; color=gray; style=dashed;");

            writer.println("  l1a [shape=point, width=0.1, label=\"\"];");
            writer.println("  l1b [shape=point, width=0.1, label=\"\"];");
            writer.println("  l2a [shape=point, width=0.1, label=\"\"];");
            writer.println("  l2b [shape=point, width=0.1, label=\"\"];");
            writer.println("  l3a [shape=point, width=0.1, label=\"\"];");
            writer.println("  l3b [shape=point, width=0.1, label=\"\"];");

            writer.println("  l1a -> l1b [color=red,    penwidth=3.0, label=\"Camino más corto (Dijkstra)\"];");
            writer.println("  l2a -> l2b [color=blue,   penwidth=3.0, label=\"Camino con más víctimas\"];");
            writer.println("  l3a -> l3b [color=purple, penwidth=3.0, label=\"Arista en ambos caminos\"];");
            writer.println("}");

            writer.println("}");
            System.out.println("Archivo .dot generado: " + archivoDot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> pathEdges(List<Integer> path) {
        Set<String> s = new HashSet<>();
        if (path == null) return s;
        for (int i = 0; i + 1 < path.size(); i++) {
            s.add(path.get(i) + "->" + path.get(i + 1));
        }
        return s;
    }

    private static void generarImagen(String archivoDot, String archivoImagen) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", archivoDot, "-o", archivoImagen);
            pb.inheritIO();
            Process process = pb.start();
            int code = process.waitFor();
            if (code == 0) {
                System.out.println("Imagen generada: " + archivoImagen);
            } else {
                System.err.println("Graphviz (dot) terminó con código " + code);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private static void abrirImagen(String archivoImagen) {
        try {
            File imagen = new File(archivoImagen);
            if (imagen.exists()) {
                Desktop.getDesktop().open(imagen);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


