package fr.celestgames.isoworlds.level;

import java.util.Random;

public class Decoration {
    private String folder;
    private String decoration;
    private int variation;
    private int vx;
    private int vy;

    public Decoration(String decoration) {
        Random rand = new Random();
        this.decoration = decoration;
        this.folder = decoration;
        if (!decoration.equals("flag")) {
            this.vx = rand.nextInt(-64 / 16, 64 / 8);
            this.vy = rand.nextInt(-64 / 16, 64 / 8);
            this.variation = rand.nextInt(0, 2);
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

    public int getVariation() {
        return variation;
    }

    public void setVariation(int variation) {
        this.variation = variation;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
