package vinnsla;

import javafx.scene.shape.Circle;

import java.util.Random;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi heldur utan um staðsetningu fyrir hvert epli.
 */

public class Faeda extends Circle {

    private final int x;
    private final int y;

    public Faeda(){
        Random r = new Random();
        x = 11 + r.nextInt(40) * 20;
        y = 11 + r.nextInt(37) * 20;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
