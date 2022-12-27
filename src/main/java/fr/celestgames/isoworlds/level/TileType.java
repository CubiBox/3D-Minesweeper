package fr.celestgames.isoworlds.level;

public enum TileType {
    VOID("void");

    private final String id;

    TileType(String s) {
        this.id = s;
    }

    public String getId() {
        return id;
    }
}
