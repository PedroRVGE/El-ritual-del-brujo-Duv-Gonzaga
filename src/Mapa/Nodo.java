package Mapa;

public class Nodo {
    private final int id;
    private int victimas;

    public Nodo(int id, int victimas) {
        this.id = id;
        this.victimas = victimas;
    }

    public int getId()        { return id; }
    public int getVictimas()  { return victimas; }
    public void setVictimas(int victimas) { this.victimas = victimas; }

    @Override
    public String toString() { return "Nodo{id=" + id + ", victimas=" + victimas + "}"; }
}

