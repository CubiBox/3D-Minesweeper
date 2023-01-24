package fr.celestgames.isoworlds.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App extends JFrame {
    private Case baseCase[][];
    private Demineur demineur;


    public App() {
        super("Demineur");
        this.baseCase = new Case[10][10];
        //this.demineur = new Demineur(baseCase);

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        };

        addWindowListener(l);
        setSize(700,700);
        setVisible(true);
    }

    public Demineur getDemineur() {
        return demineur;
    }

    public void setDemineur(Demineur demineur) {
        this.demineur = demineur;
    }
    public Case[][] getBaseCase() {
        return baseCase;
    }

    public void setBaseCase(Case[][] baseCase) {
        this.baseCase = baseCase;
    }

    public void actionPerformed(ActionEvent e) {
        ;
    }

    public static void Initialize(App frame){
        int size = frame.getDemineur().getSize();

        GridLayout base = new GridLayout(size,size);
        frame.setLayout(base);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Case c = new Case(frame.getBaseCase()[j][i].getValue());
                c.setSize(size*8,size*8);
                int finalJ = j;
                int finalI = i;
                c.addActionListener(e ->{
                    c.setBackground(Color.BLACK);
                    frame.getDemineur().revele(finalI, finalJ);
                });
                frame.getBaseCase()[j][i] = c;
                frame.add(c);
            }
        }

        //définir la disposition en grille de 3 lignes et 2 colonnes

        frame.setSize(700,700);
        frame.setVisible(true);
    }

    public static void main(String [] args){
        App frame = new App();
        Initialize(frame);

        frame.getDemineur().revele(8,8);
    }
}