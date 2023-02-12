package fr.celestgames.isoworlds.level;

import java.io.Serializable;

public class Tile implements Serializable {
    private TileType value;
    private boolean isRevealed;
    private boolean isBomb;
    private byte nbMine;
    private boolean isMarked;
    private Graphic graphic;


    public Tile(TileType value) {
        this.value = value;
        this.isRevealed = false;
        this.isBomb = false;
        this.nbMine = 0;
        this.isMarked = false;
        this.graphic = new Graphic(value.getId());
    }

    public Tile() {
        this.value = TileType.VOID;
        this.isRevealed = false;
        this.isBomb = false;
        this.nbMine = 0;
        this.isMarked = false;
        this.graphic = new Graphic(value.getId());
    }

    public String getTexture() {
        return graphic.getTexture();
    }

    public void setValue(TileType t){
        this.value = t;
        this.graphic = new Graphic(value.getId());
    }


    public TileType getValue() {
        return value;
    }

    public boolean isRevealed() {
        return isRevealed;
    }
    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public byte getNbMine() {
        return nbMine;
    }
    public void setNbMine(int nbMine) {
        this.nbMine = (byte)nbMine;
    }
    public void setNbMine(byte nbMine) {
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
        if (marked)
            this.setGraphic(new Graphic(getTexture(),"flag"));
        else
            this.setGraphic(new Graphic(getTexture()));
        isMarked = marked;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    @Override
    public String toString() {
        return "[" + value + ']';
    }

    public boolean hasDecoration() {
        return this.getGraphic().hasDecoration();
    }

    public String getNbMineModel() {
        return "numbers/"+getNbMine();
    }
}