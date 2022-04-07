package vinnsla;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import vidmot.SnakurBord;
import java.util.Random;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi er vinnsluklasi fyrir eitursnák.
 */

public class EiturSnakur extends Rectangle {
    private final int maxLength = 15;
    private final int unit_size;
    private final int length;
    private final int[] x = new int[maxLength + 5];
    private final int[] y = new int[maxLength + 5];
    private final Rectangle[] partList = new Rectangle[maxLength];
    private final AnchorPane anchorPane;
    private String direction = "";
    private final Random r = new Random();
    private int changeDirCounter = 0;
    private int changeDirOn = 5 + r.nextInt(30);
    private boolean fromBottomHole = false;
    private boolean fromTopHole = false;
    private boolean isRunning = false;
    private int runCounter = 0;

    public EiturSnakur(int us, AnchorPane anchorPane){
        unit_size = us;
        this.anchorPane = anchorPane;
        x[0] = r.nextInt(40) * 20; // setja upphafsstöðu snáksins
        y[0] = r.nextInt(37) * 20;
        length = 4 + r.nextInt(maxLength - 3);
        setRandomDirection();
        for(int i = 0; i < length; i++){
            addBodyPart();
        }
    }

    /**
     * Kallar á fall til að breyta áttinni á snáknum ef counterinn er búinn að telja upp í changeDirOn.
     */
    private void changeDirHandler(){
        changeDirCounter++;
        if(changeDirCounter >= changeDirOn){
            changeDirCounter = 0;
            changeDirOn = 5 + r.nextInt(30);
            setRandomDirection();
        }
    }

    /**
     * Breytir áttinni á eitursnáknum í einhverja handahófskennda leyfilega átt.
     */
    private void setRandomDirection(){

        if(direction.equals("")){
            String[] dirList = {"up","down","left","right"};
            direction = dirList[r.nextInt(4)];
        }else{
            String[] dirList;
            if(direction.equals("left") || direction.equals("right")){
                dirList = new String[]{"up", "down"};
            }else{
                dirList = new String[]{"right", "left"};
            }
            direction = dirList[r.nextInt(2)];
        }

    }

    /**
     * Bætir við kassa á eitursnákinn til að gera hann lengri.
     */
    private void addBodyPart(){
        Rectangle newRect = new Rectangle();
        newRect.setWidth(unit_size);
        newRect.setHeight(unit_size);
        newRect.setFill(Color.FORESTGREEN);
        newRect.setX(x[0]);
        newRect.setY(y[0]);

        for(int i = 0; i < partList.length; i++){
            if(partList[i] == null){
                partList[i] = newRect;
                anchorPane.getChildren().add(newRect);
                break;
            }
        }
    }

    public int getLength(){
        return length;
    }

    public int[] getPosX(){
        return x;
    }
    public int[] getPosY(){
        return y;
    }

    public void doRunAway(){
        isRunning = true;
    }

    public boolean isRunning(){
        return isRunning;
    }

    /**
     * Þetta fall lætur Eitursnákinn fara ofan í jörðina.
     * Fallið er notað þegar snákur sem er með stjörnu power-up klessir á hann.
     */
    private void runAway(){
        if(runCounter < length){
            anchorPane.getChildren().removeIf(child -> child instanceof Rectangle && child == partList[runCounter]);
            partList[runCounter] = null;
        }else{
            SnakurBord.removePSnake(this);
        }

        for(int i = runCounter + 1; i < partList.length; i++){
            if(partList[i] == null) break;
            partList[i].setX(x[i]);
            partList[i].setY(y[i]);
        }

        runCounter++;
    }

    /**
     * Færir eitursnákinn áfram um einn reit,
     * ef snákurinn fer á vegg þá birtist hann hinum megin á skjánum.
     */
    public void forward(){
        changeDirHandler();

        // ef hausinn á snáknum er á neðri holunni
        if(x[0] == unit_size*27 && y[0] == unit_size*29 && !fromTopHole){
            fromBottomHole = true;
        }
        else if(x[0] == unit_size*12 && y[0] == unit_size*10 && !fromBottomHole){
            fromTopHole = true;
        }

        boolean isOnHole = false;

        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];

            // neðri hola
            if(x[i] == unit_size*27 && y[i] == unit_size*29 && fromBottomHole){
                isOnHole = true;
                x[i-1] = unit_size*12;
                y[i-1] = unit_size*10;
                switch(direction){
                    case "up" -> y[i-1] += unit_size;
                    case "down" -> y[i-1] -= unit_size;
                    case "right" -> x[i-1] -= unit_size;
                    case "left" -> x[i-1] += unit_size;
                }
            }
            else if(x[0] == unit_size*12 && y[0] == unit_size*10 && fromTopHole){ // efri hola
                isOnHole = true;
                x[i-1] = unit_size*27;
                y[i-1] = unit_size*29;
                switch(direction){
                    case "up" -> y[i-1] += unit_size;
                    case "down" -> y[i-1] -= unit_size;
                    case "right" -> x[i-1] -= unit_size;
                    case "left" -> x[i-1] += unit_size;
                }
            }

            int screenWidth = 800;
            if(x[i] > screenWidth - unit_size*2 && direction.equals("right")){
                x[i-1] = -unit_size;
            }else if(x[i] < 0 && direction.equals("left")){
                x[i-1] = screenWidth;
            }
            int screenHeight = 740;
            if(y[i] < 1 && direction.equals("up")){
                y[i-1] = screenHeight;
            }else if(y[i] > screenHeight - unit_size*2 && direction.equals("down")){
                y[i-1] = -unit_size;
            }
        }

        if(!isOnHole){
            fromBottomHole = false;
            fromTopHole = false;
        }

        switch (direction) {
            case "right" -> x[0] = x[0] + unit_size;
            case "left" ->  x[0] = x[0] - unit_size;
            case "up" -> y[0] = y[0] - unit_size;
            case "down" -> y[0] = y[0] + unit_size;
        }
        for(int i = 0; i < partList.length; i++){
            if(partList[i] == null) break;
            partList[i].setX(x[i]);
            partList[i].setY(y[i]);
        }

        if(isRunning) runAway();
    }
}
