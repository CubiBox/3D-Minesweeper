package fr.celestgames.isoworlds.minesweeper;

import javax.swing.*;
import java.awt.*;

public class Case extends JButton {
    private int value;
    private boolean isReveled;

    public Case(int value) {
        this.value = value;
        this.isReveled = false;
    }

    public boolean isReveled() {
        return isReveled;
    }

    public void setReveled(boolean reveled) {
        isReveled = reveled;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
