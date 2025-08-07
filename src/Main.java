import ImagenGrafo.ExportadorDot;
import Mapa.Grafo;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Grafo grafo = new Grafo();

        System.out.print("Ingrese la cantidad de nodos: ");
        int n = sc.nextInt();

        System.out.print("Ingrese la cantidad de aristas: ");
        int m = sc.nextInt();

        grafo.crearGrafoAleatorio(n, m);
        System.out.println("Grafo aleatorio generado correctamente.");

        // Exportar y mostrar imagen
        ExportadorDot.exportarYMostrar(grafo, "grafo.dot", "grafo.png");
    }
}