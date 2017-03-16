package com.brentaureli.mariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.brentaureli.mariobros.MarioBros;

/**
 * Created by brentaureli on 8/17/15.
 */
public class Hud implements Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Mario score/time Tracking Variables
    private Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    private float timeCount;
    private static Integer score;
private static int health;
    //Scene2D widgets
    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    private static boolean changeHealth;
    public static Image leftImg[] = new Image[5];
    Table table = new Table();
    public Hud(SpriteBatch sb){
        //define our tracking variables
        worldTimer = 300;
        timeCount = 0;
        score = 0;


        //setup the HUD viewport using a new camera seperate from our gamecam
        //define our stage using that viewport and our games spritebatch
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //define a table used to organize our hud's labels

        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define our labels using the String, and a Label style consisting of a font and color
        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel =new Label(String.format("%02d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("STAGE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARX", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        for (int i = 0; i < 5; i++) {
            leftImg[i] = new Image(new Texture("heart-icon-14.png"));
            leftImg[i].setSize(15, 15);
        }
        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        for (int i = 0; i < 5; i++) {
            table.add(leftImg[i]).size(leftImg[i].getWidth(), leftImg[i].getHeight()).left().padTop(12);
        }
        table.add(scoreLabel).expandX();
        //add a second row to our table
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();

        //add our table to the stage
        stage.addActor(table);
    changeHealth = false;
    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
        if (changeHealth) {
            table.clear();
            table.add(marioLabel).expandX().padTop(10);
            table.add(worldLabel).expandX().padTop(10);
            for (int i = 0; i < health; i++) {
                table.add(leftImg[i]).size(leftImg[i].getWidth(), leftImg[i].getHeight()).left().padTop(12);
            }
            for (int i = health; i < 5; i++) {
                table.add().size(leftImg[1].getWidth(), leftImg[1].getHeight()).left().padTop(12);
            }
            table.row();
            table.add(scoreLabel).expandX();
            table.add(levelLabel).expandX();
            changeHealth = false;
            stage.addActor(table);
        }
    }

    public static void getHealth(int value){
       health = value;
        changeHealth = true;
    }
    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%02d", score));
    }

    @Override
    public void dispose() { stage.dispose(); }

    public boolean isTimeUp() { return timeUp; }
}
