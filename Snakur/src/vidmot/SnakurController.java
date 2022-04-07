package vidmot;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import vinnsla.Leikur;
import vinnsla.Snakur;
import javax.sound.sampled.Clip;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class SnakurController implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private Label stigLabel;
    @FXML private Label overText1;
    @FXML private Label overText2;
    @FXML private Button newGameBtn;
    @FXML private Label highScoreText;
    @FXML private Label scoreText;
    @FXML private Label settingsText;
    @FXML private Button settingsContinue;
    @FXML private Button settingsNewGame;
    @FXML private Button settingsMusicBtn;
    @FXML private ImageView settingsIcon;
    @FXML private ColorPicker colorPicker;
    @FXML private Label colorText;

    private Timeline tl;
    private Snakur snake;
    private final int unit_size = 20;
    private final Leikur leikur = new Leikur();
    private Clip clip;

    /**
     *  Nafn    : Einar Logi Pétursson
     *  T-póstur: elp26@hi.is
     *
     *  Lýsing  : Þessi klasi er controller fyrir snakur-view.fxml.
     */

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            hefjaLeik();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setur upp leiklykkjuna (Timeline) og býr til nýjan leik
     */
    public void hefjaLeik() throws java.io.FileNotFoundException {
        tl = new Timeline(new KeyFrame(Duration.millis(150), this::gameLoop));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.playFromStart();
        colorPicker.setValue(Color.LIME);
        snake = new Snakur(3, anchorPane, unit_size, tl, this);
        stigLabel.setText(String.valueOf(leikur.getScore()));
        SnakurBord.nyrLeikur(snake, anchorPane, unit_size);
        settingsIcon.toFront();
        stigLabel.toFront();

    }

    /**
     * Þetta fall er "Game loop-ið", það er kallað aftur og aftur á.
     * @param actionEvent actionEvent.
     */
    private void gameLoop(javafx.event.ActionEvent actionEvent){
        try{
            snake.forward();
            SnakurBord.movePSnakes();
            boolean didHit = SnakurBord.didHitSnake();
            if(didHit){
                gameOver(false);
            }
            boolean didEat = SnakurBord.checkApple();
            if(didEat){
                if(SnakurBord.didEatGolden()){
                    leikur.incrementScore();
                    leikur.incrementScore();
                }
                leikur.incrementScore();
                stigLabel.setText(String.valueOf(leikur.getScore()));

                settingsIcon.toFront();
                stigLabel.toFront();
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Handler fyrir "Nýr Leikur" takkann í stillingum eða í game over skjánum.
     * Fallið setur upp nýjann leik.
     */
    public void newGame() throws FileNotFoundException {
        overText1.setVisible(false);
        overText2.setVisible(false);
        newGameBtn.setVisible(false);
        highScoreText.setVisible(false);
        scoreText.setVisible(false);
        settingsText.setVisible(false);
        settingsNewGame.setVisible(false);
        settingsContinue.setVisible(false);
        settingsMusicBtn.setVisible(false);
        colorPicker.setVisible(false);
        colorText.setVisible(false);
        anchorPane.getChildren().removeIf(child -> child instanceof Rectangle || child instanceof Circle || child instanceof ImageView && child.getId().equals("starView"));
        snake = new Snakur(3, anchorPane, unit_size, tl, this);
        SnakurBord.nyrLeikur(snake, anchorPane, unit_size);
        leikur.setScore(0);
        stigLabel.setText(String.valueOf(leikur.getScore()));
        tl.play();
    }

    /**
     * Aðferð fyrir þegar maður rekst á eitursnák eða sjálfan sig.
     * Fallið birtir "Leik lokið" skjáinn.
     * @param hitSelf - true ef þú rakst á sjálfann þig, false ef þú rakst á eitursnák.
     */
    public void gameOver(boolean hitSelf){
        tl.pause();

        if(leikur.getScore() > leikur.getHighScore()){
            leikur.setHighScore(leikur.getScore());
            highScoreText.setText("Nýtt high score: " + leikur.getHighScore());
        }else{
            highScoreText.setText("High score: " + leikur.getHighScore());
            scoreText.setVisible(true);
            scoreText.setText("Score: " + leikur.getScore());
        }

        if(hitSelf){
            overText2.setText("Þú rakst á sjálfann þig");
        }else{
            overText2.setText("Þú rakst á eitursnák");
        }

        darkerBackground();

        highScoreText.toFront();
        overText1.toFront();
        overText2.toFront();
        scoreText.toFront();

        overText1.setVisible(true);
        overText2.setVisible(true);
        newGameBtn.setVisible(true);
        highScoreText.setVisible(true);
        newGameBtn.toFront();
    }

    /**
     * Fall sem lætur skjáinn vera dekkri.
     */
    private void darkerBackground(){
        Rectangle tempRec = new Rectangle();
        tempRec.setWidth(anchorPane.getWidth());
        tempRec.setHeight(anchorPane.getHeight());
        tempRec.setFill(Color.BLACK);
        tempRec.setOpacity(.6);
        tempRec.toFront();
        anchorPane.getChildren().add(tempRec);
    }

    /**
     * Þetta fall keyrist þegar það er ýtt á settings takkann efst hægra megin á skjánum.
     * Fallið pásar leikinn og birtir settings hlutina.
     */
    public void openSettings(){
        tl.pause();

        darkerBackground();

        settingsText.toFront();
        colorText.toFront();
        settingsNewGame.toFront();
        settingsContinue.toFront();
        settingsMusicBtn.toFront();
        colorPicker.toFront();

        settingsText.setVisible(true);
        settingsNewGame.setVisible(true);
        settingsContinue.setVisible(true);
        settingsMusicBtn.setVisible(true);
        colorPicker.setVisible(true);
        colorText.setVisible(true);
    }

    /**
     * Þegar ýtt er á "Halda Áfram" takkann í settings glugganum keyrist þetta fall.
     * Fallið felur settings hlutina og resume-ar leikinn.
     */
    public void continueGame(){
        settingsText.setVisible(false);
        settingsNewGame.setVisible(false);
        settingsContinue.setVisible(false);
        settingsMusicBtn.setVisible(false);
        colorPicker.setVisible(false);
        colorText.setVisible(false);

        anchorPane.getChildren().removeIf(child -> child instanceof Rectangle && ((Rectangle) child).getWidth() == anchorPane.getWidth());

        tl.play();
    }

    public void setClip(Clip clip){
        this.clip = clip;
    }

    /**
     * Þetta fall er keyrt þegar það er ýtt á Kveikja/Slökkva takkann í settings.
     * Fallið stoppar/spilar tónlistina og lætur viðeigandi texta á takkann.
     */
    public void toggleMusic(){
        if(clip.isActive()){
            clip.stop();
            settingsMusicBtn.setText("Kveikja á Tónlist");
        }else{
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            settingsMusicBtn.setText("Slökkva á Tónlist");
        }
    }

    /**
     * Þetta fall keyrist þegar það er breytt litnum í ColorPicker í settings.
     * Fallið kallar á fall sem breytir litnum á snáknum í litinn sem er valinn.
     */
    public void changeSnakeColor(){
        snake.setColor(colorPicker.getValue());
    }

    /**
     * Kallar á fall í snáknum sem breytir áttinni ef áttin er leyfileg.
     * @param dir átt furir snákinn að fara í.
     */
    public void setDirection(String dir){
        snake.setDirection(dir);
    }
}
