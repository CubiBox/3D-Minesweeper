package fr.celestgames.isoworlds.level;

public class Tile {
    private TileType value;
    private boolean isReveled;
    private boolean isBomb;
    private int nbMine;
    private boolean isMarked;
    private Graphic graphic;


    public Tile(TileType value) {
        this.value = value;
        this.isReveled = false;
        this.isBomb = false;
        this.nbMine = 0;
        this.isMarked = false;
        this.graphic = new Graphic(value.getId());
    }

    public Tile() {
        this.value = TileType.VOID;
        this.isReveled = false;
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