package Mapa;

public class Nodo {
    private int id;
    private int victimas;

    public Nodo(int id, int victimas) {
        this.id = id;
        this.victimas = victimas;
    }

    public int getId() {
        return id;
    }

    public int getVictimas() {
        return victimas;
    }

    @Override
    public String toString() {
        return "Nodo{" +
                "id=" + id +
                ", victimas=" + victimas +
                '}';
    }
}

