package Mapa;

public class Arista {
    private int origen;
    private int destino;
    private int distancia;

    public Arista(int origen, int destino, int distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public int getDistancia() {
        return distancia;
    }

    @Override
    public String toString() {
        return origen + " -> " + destino + " [distancia=" + distancia + "]";
    }
}