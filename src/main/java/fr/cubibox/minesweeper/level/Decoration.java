package fr.cubibox.minesweeper.level;

import fr.cubibox.minesweeper.MineSweeper;

import java.io.Serializable;
import java.util.Random;

public class Decoration implements Serializable {
    private String folder;
    private String decoration;
    private byte variation;
    private byte vx;
    private byte vy;

    public Decoration(String decoration) {
        Random rand = new Random();
        this.decoration = decoration;
        this.folder = decoration;
        if (MineSweeper.tileSprites.get(folder).length > 0) {
            this.vx = (byte) rand.nextInt(-64 / 16, 64 / 8);
            this.vy = (byte) rand.nextInt(-64 / 16, 64 / 8);
            this.variation = (byte) rand.nextInt(0, MineSweeper.tileSprites.get(folder).length);
        }
    }

    public Decoration(String decoration, String folder) {
        Random rand = new Random();
        this.decoration = decoration;
        this.folder = folder;
        if (MineSweeper.tileSprites.get(folder).length > 0) {
            this.vx = (byte) rand.nextInt(-64 / 16, 64 / 8);
            this.vy = (byte) rand.nextInt(-64 / 16, 64 / 8);
            this.variation = (byte) rand.nextInt(0, MineSweeper.tileSprites.get(folder).length);
        }
    }

    public String getDecorationModel() {
        return (folder!=null) ?
                        folder+"/"+decoration+"_"+variation :
                        decoration+"_"+variation;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public void setDecoration(String decoration, String folder) {
        this.decoration = decoration;
        this.folder = folder;
    }

    public int getVariation() {
        return variation;
    }

    public void setVariation(int variation) {
        this.variation = (byte) variation;
    }
    public void setVariation(byte variation) {
        this.variation = variation;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = (byte) vx;
    }
    public void setVx(byte vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = (byte) vy;
    }
    public void setVy(byte vy) {
        this.vy = vy;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
