package vidmot;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import vinnsla.EiturSnakur;
import vinnsla.Faeda;
import vinnsla.Snakur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi hefur aðferðir og breytur aðallega fyrir epli og eitursnáka.
 */

public class SnakurBord extends AnchorPane {
    private static final int maxSnakes = 3;
    private static final int maxApples = 7;
    private static Faeda[] foodList = new Faeda[maxApples];
    private static Circle[] appleList = new Circle[maxApples];
    private static EiturSnakur[] pSnakeList = new EiturSnakur[maxSnakes];
    private static Snakur snake;
    private static AnchorPane anchorPane;
    private static int unit_size;
    private static boolean didEatGolden = false;
    private static final int starOffset = -15;

    /**
     * Stillir breytur fyrir nýjann leik.
     * @param s Snákur.
     * @param ap anchorPane.
     * @param uz unit_size, stærð hvers kassa fyrir snáka í leiknum.
     */
    public static void nyrLeikur(Snakur s, AnchorPane ap, int uz) throws FileNotFoundException {
        unit_size = uz;
        anchorPane = ap;
        snake = s;
        pSnakeList = new EiturSnakur[maxSnakes];
        appleList = new Circle[maxApples];
        foodList = new Faeda[maxApples];
        spawnApple();
        spawnPSnake();
    }

    /**
     * Athugar hvort hausinn á snáknum er á sama stað og epli, ef svo, þá borða eplið.
     * @return skilar true ef hausinn er á sama stað og epli, annars false.
     */
    public static boolean checkApple() throws FileNotFoundException {
        Rectangle[] partList = snake.getPartList();
        boolean returnVal = false;
        for(int i = 0; i < foodList.length; i++){
            if(foodList[i] != null && appleList[i] != null){
                if(partList[0].getBoundsInParent().getMaxY() >= foodList[i].getY() && partList[0].getBoundsInParent().getMinY() <= foodList[i].getY() && partList[0].getBoundsInParent().getMaxX() >= foodList[i].getX() && partList[0].getBoundsInParent().getMinX() <= foodList[i].getX()){
                    didEatGolden = false;
                    if(appleList[i].getId() != null && appleList[i].getId().equals("star")){ // ef "epli" er stjarna
                        anchorPane.getChildren().remove(appleList[i]);
                        int finalI = i;
                        anchorPane.getChildren().removeIf(child -> child instanceof ImageView && child.getId().equals("starView") && ((ImageView) child).getX() == foodList[finalI].getX() + starOffset && ((ImageView) child).getY() == foodList[finalI].getY() + starOffset);
                        snake.setInvincibleTimer(30);
                    }
                    else if(String.valueOf(appleList[i].getStroke()).equals("0xffff00ff")){ // ef epli er gullepli
                        didEatGolden = true;
                        snake.addBodyPart();
                        snake.addBodyPart();
                        snake.addBodyPart();
                        anchorPane.getChildren().remove(appleList[i]);
                        returnVal = true;
                    }else{
                        snake.addBodyPart();
                        anchorPane.getChildren().remove(appleList[i]);
                        returnVal = true;
                    }

                    appleList[i] = null;
                    foodList[i] = null;
                    spawnApple();
                    Random r = new Random();
                    if(r.nextInt(5) == 0){ // í hvert skipti sem snákur borðar, þá er 20% líkur á að eitursnákur birtist (ef það er ekki komið max eitursnákar)
                        spawnPSnake();
                    }
                }
            }
        }
        return returnVal;
    }

    /**
     * Býr til eitt eða tvö epli og setur það á skjáinn.
     */
    private static void spawnApple() throws FileNotFoundException {
        Random r = new Random();
        int spawnAmount = r.nextInt(2) + 1;
        for(int j = 0; j < spawnAmount; j++){
            boolean spawnGolden = r.nextFloat() <= .1; // 10% líkur á að gullepli spawnast ef stjarna spawnast ekki
            boolean spawnStar = r.nextFloat() <= .08;

            Faeda apple = new Faeda();
            if(spawnStar){
                File file = new File("");
                String cwd = file.getAbsolutePath();
                InputStream is = new FileInputStream(cwd + "\\src\\images\\star.png");
                Image starImg = new Image(is);
                ImageView imgView = new ImageView();
                imgView.setImage(starImg);
                imgView.setFitWidth(25);
                imgView.setFitHeight(25);
                imgView.setX(apple.getX() + starOffset);
                imgView.setY(apple.getY() + starOffset);
                imgView.setId("starView");

                for(int i = 0; i < foodList.length; i++){
                    if(foodList[i] == null){
                        foodList[i] = apple;
                        Circle starCircle = new Circle();
                        starCircle.setId("star");
                        appleList[i] = starCircle;
                        anchorPane.getChildren().add(imgView);
                        break;
                    }
                }
            }else{
                Circle c = new Circle();
                if(spawnGolden){
                    c.setStroke(Color.YELLOW);
                }else{
                    c.setStroke(Color.RED);
                }
                c.setScaleX(15);
                c.setScaleY(15);
                c.setCenterX(apple.getX());
                c.setCenterY(apple.getY());

                for(int i = 0; i < foodList.length; i++){
                    if(foodList[i] == null){
                        foodList[i] = apple;
                        appleList[i] = c;
                        anchorPane.getChildren().add(c);
                        break;
                    }
                }
            }


        }
    }

    /**
     * Bætir við nýjum eitursnák í leikinn.
     */
    public static void spawnPSnake(){
        for(int i = 0; i < pSnakeList.length; i++){
            if(pSnakeList[i] == null) {
                EiturSnakur pSnake = new EiturSnakur(unit_size, anchorPane);
                pSnakeList[i] = pSnake;
                break;
            }
        }
    }

    /**
     * Fjarlægir eitursnák úr listanum.
     * @param ps Eitursnákur til að fjarlægja
     */
    public static void removePSnake(EiturSnakur ps){
        for(int i = 0; i < pSnakeList.length; i++){
            if(ps == pSnakeList[i]){
                pSnakeList[i] = null;
                break;
            }
        }
    }

    /**
     * Athugar hvort snákurinn hafi rekst á eitursnák.
     * @return true ef snákurinn rakst á eitursnák, annars false;
     */
    public static boolean didHitSnake(){
        int[] sX = snake.getPosX();
        int[] sY = snake.getPosY();
        int sLen = snake.getLength();

        for(EiturSnakur ps : pSnakeList){
            if(ps != null){
                int[] psX = ps.getPosX();
                int[] psY = ps.getPosY();
                int len = ps.getLength();
                for(int i = 0; i < len; i++){ // for each cube on ps
                    for(int j = 0; j < sLen; j++){ // for each cube on snake
                        if(psX[i] == sX[j] && psY[i] == sY[j]){
                            if(!(psX[i] == 0 && sX[j] == 0 && psY[i] == 0 && sY[j] == 0)){
                                if(snake.isInvincible()){
                                    ps.doRunAway();
                                    return false;
                                }
                                if(!ps.isRunning()){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Færir hvern einasta eitursnák áfram.
     */
    public static void movePSnakes(){
        for (EiturSnakur ps : pSnakeList) {
            if(ps != null) ps.forward();
        }
    }

    /**
     * Skilar true ef snákurinn borðaði gull epli síðast.
     * @return true ef snákur var að borða gull epli, annars false.
     */
    public static boolean didEatGolden(){
        return didEatGolden;
    }
}
