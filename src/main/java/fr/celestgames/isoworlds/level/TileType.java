package fr.celestgames.isoworlds.level;

import java.io.Serializable;

public enum TileType implements Serializable {
    VOID("void"),
    CUBE("cube"),
    BOMB("bomb");

    private final String id;

    TileType(String s) {
        this.id = s;
    }

    public String getId() {
        return id;
    }
}
