package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi er "aðal" klasi leiksins, hann er keyrður til að hefja leik.
 */

public class SnakurApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("../resources/snakur-view.fxml"));
        Parent root = fxmlloader.load();
        primaryStage.setTitle("Snákur");
        Scene scene = new Scene(root, 800, 740);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

        File file = new File("");
        String cwd = file.getAbsolutePath();
        InputStream is = new FileInputStream(cwd + "\\src\\sounds\\background_music.wav");
        InputStream bufferedIs = new BufferedInputStream(is);
        Clip clip = null;
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIs);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception e) {
            e.printStackTrace();
        }

        SnakurController controller = fxmlloader.getController();
        controller.setClip(clip);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> controller.setDirection("up");
                case A, LEFT -> controller.setDirection("left");
                case S, DOWN -> controller.setDirection("down");
                case D, RIGHT -> controller.setDirection("right");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
