package Mapa;

import java.util.*;

public class Grafo {
    private List<Nodo> nodos = new ArrayList<>();
    private List<Arista> aristas = new ArrayList<>();

    public void agregarNodo(int id, int victimas) {
            nodos.add(new Nodo(id, victimas));
            // crea nodo y lo agrega al array
    }

    public void agregarArista(int origen, int destino, int distancia) {
        aristas.add(new Arista(origen, destino, distancia));
        //crea Arista y la agrega al array
    }

    public List<Nodo> getNodos() {
        return nodos;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public List<Arista> obtenerVecinos(int nodoId) {
        List<Arista> vecinos = new ArrayList<>();
        for (Arista arista : aristas) {
            if (arista.getOrigen() == nodoId) {
                vecinos.add(arista);
            }
        }
        return vecinos;
    }

    public void crearGrafoAleatorio(int n, int m) {
        // nos llega desde el main n cantidad de nodos y m cantidad de aristas
        Random random = new Random();

        // Crear nodos con víctimas aleatorias
        for (int i = 0; i < n; i++) {
            int victimas = random.nextInt(11); // 0 a 10 víctimas posibles
            this.agregarNodo(i, victimas); // se agrega el nodo con esas víctimas al grafo
        }
        //-----------CRUCIAL---------------//
        Set<String> conexionesExistentes = new HashSet<>();
        // Aca usamos set para no repetir la creation de una arista que ya estaba con una distancia puesta ya que seria como crear un camino con 2 distancias diferentes
        // Set es como una lista que no permite duplicados.
        // HashSet es una de las implementaciones más comunes de Set en Java.
        // Cuando haces add(...), si ese elemento ya existe, simplemente no lo vuelve a agregar.

        // -------------------------------//
        int aristasCreadas = 0; // Empezamos en 0

        // Crear m aristas aleatorias sin repetir conexiones
        while (aristasCreadas < m) {
            int u = random.nextInt(n); // de acuerdo con la instruccion este u es el nodo de origen
            int v = random.nextInt(n); // y este es v es el nodo de destino

            if (u != v) { // si el origen es diferente del destino creado se hace esto, si no es como si no contara
                String camino= u + "->" + v; // este es el "camino"
                if (!conexionesExistentes.contains(camino)) { // si aun no hemos creado este camino lo creamos (por eso usamos lo de CRUCIAL)
                    // este .contains va a revisar si dentro de las aristas existentes ya esta este camino:
                    // si ya esta !true -> false
                    // si no esta !false -> true
                    int distancia = 1 + random.nextInt(20); // hacemos distancia entre 1 y 20

                    this.agregarArista(u, v, distancia); // agregamos arista a nuestro array de aristas
                    conexionesExistentes.add(camino); // agregamos a nuestro validador de caminos existentes
                    aristasCreadas++; // aumentamos el contador en 1
                }
            }
        }
    }

}
