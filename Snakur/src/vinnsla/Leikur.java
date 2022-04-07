package vinnsla;

/**
 *  Nafn    : Einar Logi Pétursson
 *  T-póstur: elp26@hi.is
 *
 *  Lýsing  : Þessi klasi heldur utan um stig og high score leiks.
 */

public class Leikur {
    private int highScore = 0;
    private int score = 0;

    public void incrementScore(){
        score++;
    }

    public void setScore(int s){
        score = s;
    }

    public int getScore(){
        return score;
    }

    public int getHighScore(){
        return highScore;
    }

    public void setHighScore(int h){
        highScore = h;
    }
}
