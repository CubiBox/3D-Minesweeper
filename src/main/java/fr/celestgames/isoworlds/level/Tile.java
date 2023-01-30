package fr.celestgames.isoworlds.level;

public class Tile {
    private TileType value;
    private boolean isReveled;
    private boolean isBomb;
    private int nbMine;
    private boolean isMarked;


    public Tile(TileType value) {
        this.value = value;
        this.isReveled = false;
        this.isBomb = false;
        this.nbMine = 0;
        this.isMarked = false;
    }

    public Tile() {
        this.value = TileType.VOID;
        this.isReveled = false;
        this.isBomb = false;
        this.nbMine = 0;
        this.isMarked = false;
    }



    public String getStrValue() {
        return value.getId();
    }

    public void setValue(TileType t){
        this.value = t;
    }

    public TileType getValue() {
        return value;
    }

    public boolean isReveled() {
        return isReveled;
    }
    public void setReveled(boolean reveled) {
        isReveled = reveled;
    }

    public int getNbMine() {
        return nbMine;
    }
    public void setNbMine(int nbMine) {
        this.nbMine = nbMine;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }
}