package fr.celestgames.isoworlds;

public class Main {
    public static void main(String[] args) {
        try {
            MineSweeper.launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}