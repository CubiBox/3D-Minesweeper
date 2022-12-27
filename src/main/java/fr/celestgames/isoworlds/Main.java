package fr.celestgames.isoworlds;

public class Main {
    public static void main(String[] args) {
        try {
            IWApplication.launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}