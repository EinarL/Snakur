package vinnsla;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import vidmot.SnakurController;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi er vinnsluklasi fyrir snák.
 */

public class Snakur extends Rectangle {
    private String direction = "right";
    private boolean allowChange = true;
    private int bodyParts = 0;
    private final int unit_size;
    private final int screenWidth = 800;
    private final int screenHeight = 740;
    private final int[] x;
    private final int[] y;
    private final Rectangle[] partList;
    private final AnchorPane anchorPane;
    private final Timeline tl;
    private boolean isPaused = false;
    private boolean fromBottomHole = false;
    private boolean fromTopHole = false;
    private Color snakeColor = Color.LIME;
    private Color oldSnakeColor = Color.LIME;
    private final SnakurController snakurController;
    private int invincibleTimer = 0;
    private Color[] colorList = new Color[4];

    public Snakur(int parts, AnchorPane ap, int us, Timeline tl, SnakurController sc){
        anchorPane = ap;
        unit_size = us;
        this.tl = tl;
        this.snakurController = sc;


        x = new int[(screenWidth*screenHeight)/unit_size];
        y = new int[(screenWidth*screenHeight)/unit_size];
        partList = new Rectangle[(screenWidth*screenHeight)/unit_size];

        x[0] = unit_size * 15; // setja upphafsstöðu snáksins
        y[0] = unit_size * 15;
        for(int i = 0; i < parts; i++){
            addBodyPart();
        }

        Color sColor = Color.LIME;
        for(Node child : anchorPane.getChildren()){
            if(child instanceof ColorPicker){
                sColor = ((ColorPicker) child).getValue();
            }
        }
        setColor(sColor);
    }

    /**
     * Handler fyrir þegar það er ýtt á snákinn, þá pásast/playast leikurinn.
     */
    public void stopHandler(){
        if(isPaused){
            isPaused = false;
            tl.play();
        }else{
            isPaused = true;
            tl.pause();
        }
    }

    public Rectangle[] getPartList(){
        return partList;
    }

    public int getLength(){
        return bodyParts;
    }

    public int[] getPosX(){
        return x;
    }
    public int[] getPosY(){
        return y;
    }

    /**
     * Bætir við kassa á snákinn til að gera hann lengri.
     */
    public void addBodyPart(){
        bodyParts++;
        Rectangle newRect = new Rectangle();
        newRect.setWidth(unit_size);
        newRect.setHeight(unit_size);
        newRect.setFill(snakeColor);
        newRect.setOnMousePressed(mouseEvent -> stopHandler());
        newRect.setId("snakePart");

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

    /**
     * Breytir áttinni á snáknum ef áttin er leyfileg.
     * @param dir átt.
     */
    public void setDirection(String dir){
        if(!allowChange) return;
        if(direction.equals("up") && dir.equals("down")) return;
        if(direction.equals("down") && dir.equals("up")) return;
        if(direction.equals("right") && dir.equals("left")) return;
        if(direction.equals("left") && dir.equals("right")) return;
        direction = dir;
        allowChange = false;
    }

    /**
     * Breytir litnum á snáknum í "color".
     * @param color litur á snáknum.
     */
    public void setColor(Color color){
        oldSnakeColor = color;
        snakeColor = color;
        for(Rectangle rect : partList){
            if(rect == null) break;
            rect.setFill(color);
        }

        if(snakeColor.getBrightness() >= .5){ // ef litur er bjartur
            colorList = new Color[]{snakeColor.brighter(), snakeColor, snakeColor.darker(), snakeColor.darker().darker()};
        }else if(snakeColor.getBrightness() < .15){ // mjög dimmur
            colorList = new Color[]{snakeColor.brighter().brighter().brighter().brighter(), snakeColor.brighter().brighter().brighter().brighter().brighter(), snakeColor.brighter().brighter().brighter().brighter().brighter().brighter(), snakeColor.brighter().brighter().brighter().brighter().brighter().brighter().brighter()};
        } else{
            colorList = new Color[]{snakeColor, snakeColor.brighter(), snakeColor.brighter().brighter(), snakeColor.brighter().brighter().brighter()};
        }
    }

    public void setInvincibleTimer(int time){
        invincibleTimer = time;
    }

    public boolean isInvincible(){
        return invincibleTimer > 0;
    }

    /**
     * Lætur snákinn vera litríkann.
     */
    private void invincibleColors(){
        int i = 0;
        boolean up = true;
        for(Rectangle rect : partList){
            if(rect == null) break;
            rect.setFill(colorList[i]);
            if(i >= 3 && up) up = false;
            if(i <= 0 && !up) up = true;
            if(up) i++;
            else i--;
        }
        Color tempColor = colorList[0];
        colorList[0] = colorList[1];
        colorList[1] = colorList[2];
        colorList[2] = colorList[3];
        colorList[3] = tempColor;

        invincibleTimer--;
    }


    /**
     * Færir snákinn áfram um einn reit,
     * ef snákurinn fer á vegg þá birtist hann hinum megin á skjánum.
     */
    public void forward(){
        if(invincibleTimer > 0){
            invincibleColors();
        }else if(invincibleTimer == 0){
            invincibleTimer = -1;
            setColor(oldSnakeColor);
        }
        // ef hausinn á snáknum er á neðri holunni
        if(x[0] == unit_size*27 && y[0] == unit_size*29 && !fromTopHole){
            fromBottomHole = true;
        }
        else if(x[0] == unit_size*12 && y[0] == unit_size*10 && !fromBottomHole){
            fromTopHole = true;
        }

        boolean isOnHole = false;

        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];

            if(i != 1 && x[0] == x[i] && y[0] == y[i]){ // ef snákur rekst á sjálfan sig
                snakurController.gameOver(true);
            }

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
            else if(x[i] == unit_size*12 && y[i] == unit_size*10 && fromTopHole){ // efri hola
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

            // hvað gerist ef snákurinn fer á vegg ->
            if(x[i] > screenWidth - unit_size*2 && direction.equals("right")){
                x[i-1] = -unit_size;
            }else if(x[i] < 0 && direction.equals("left")){
                x[i-1] = screenWidth;
            }
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
        allowChange = true;
    }
}
