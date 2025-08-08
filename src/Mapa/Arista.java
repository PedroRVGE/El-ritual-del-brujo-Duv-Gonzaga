package Mapa;

public class Arista {
    private final int origen;
    private final int destino;
    private final int distancia;

    public Arista(int origen, int destino, int distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    public int getOrigen() { return origen; }
    public int getDestino() { return destino; }
    public int getDistancia() { return distancia; }

    @Override
    public String toString() {
        return origen + " -> " + destino + " [distancia=" + distancia + "]";
    }
}
