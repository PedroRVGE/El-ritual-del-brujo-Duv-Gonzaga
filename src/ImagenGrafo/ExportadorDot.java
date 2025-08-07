package ImagenGrafo;

import Mapa.Arista;
import Mapa.Grafo;
import Mapa.Nodo;

import java.awt.Desktop;
import java.io.*;

public class ExportadorDot {

    public static void exportarYMostrar(Grafo grafo, String archivoDot, String archivoImagen) {
        generarArchivoDot(grafo, archivoDot);
        generarImagen(archivoDot, archivoImagen);
        abrirImagen(archivoImagen);
    }

    private static void generarArchivoDot(Grafo grafo, String archivoDot) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoDot))) {
            writer.println("digraph G {");

            // Nodos con etiquetas
            for (Nodo nodo : grafo.getNodos()) {
                writer.printf("%d [label=\"%d\\nVíctimas: %d\"];\n", nodo.getId(), nodo.getId(), nodo.getVictimas());
            }

            // Aristas con etiquetas
            for (Arista arista : grafo.getAristas()) {
                writer.printf("%d -> %d [label=\"Dist: %d\"];\n",
                        arista.getOrigen(), arista.getDestino(), arista.getDistancia());
            }

            writer.println("}");
            System.out.println("Archivo .dot generado: " + archivoDot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generarImagen(String archivoDot, String archivoImagen) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", archivoDot, "-o", archivoImagen);
            pb.inheritIO(); // para ver errores si los hay
            Process process = pb.start();
            process.waitFor();
            System.out.println("Imagen generada: " + archivoImagen);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
