package application;

import javafx.scene.control.Label;

public class ScoreManager {
    private int score = 0;
    private int highScore = 0;
    private String highScoreOwner = "-";
    private final Label scoreLabel;
    private final Label highScoreLabel;

    public ScoreManager(Label scoreLabel, Label highScoreLabel) {
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
        updateLabels();
    }

    public void setHighScore(int highScore, String owner) {
        this.highScore = highScore;
        this.highScoreOwner = owner;
        updateLabels();
    }

    public void reset() {
        score = 0;
        updateLabels();
    }

    public void add(int delta) {
        score += delta;
        updateLabels();
    }

    public int getScore() {
        return score;
    }

    public void updateLabels() {
        scoreLabel.setText(Integer.toString(score));
        highScoreLabel.setText("HighScore: " + highScore + " (" + highScoreOwner + ")");
    }

    public void setScore(int newScore) {
        this.score = newScore;
        updateLabels();
    }

    public int getHighScore() {
        return highScore;
    }

    public String getHighScoreOwner() {
        return highScoreOwner;
    }
}
