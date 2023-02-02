package fr.celestgames.isoworlds.level;

import java.util.Random;

public class Graphic {
    private Decoration decoration;
    private int variation;
    private String texture;

    public Graphic(String texture, Decoration decoration) {
        this.decoration = decoration;
        this.texture = texture;
        Random rand = new Random();
        this.variation = rand.nextInt(0,1);
    }
    public Graphic(String texture, String decoration) {
        this.decoration = new Decoration(decoration);
        this.texture = texture;
        Random rand = new Random();
        this.variation = rand.nextInt(0,1);
    }
    public Graphic() {
        Random rand = new Random();
        this.variation = rand.nextInt(0,1);
    }
    public Graphic(String texture) {
        Random rand = new Random();
        this.variation = rand.nextInt(0,2);
        this.texture = texture;
    }

    public boolean hasDecoration() {
        return this.decoration != null;
    }

    public Decoration getDecoration() {
        return decoration;
    }

    public void setDecoration(Decoration decoration) {
        this.decoration = decoration;
    }

    public int getVariation() {
        return variation;
    }

    public void setVariation(int variation) {
        this.variation = variation;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
}
