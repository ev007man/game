package com.brentaureli.mariobros;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by brentaureli on 10/23/15.
 */
public class Controller {
    Viewport viewport;
    Stage stage;
    boolean upPressed, downPressed, leftPressed, rightPressed, fired;
    OrthographicCamera cam;
int bsize = 90;
    public Controller(SpriteBatch spriteBatch){
        cam = new OrthographicCamera();
        viewport = new FitViewport(800, 480, cam);
        stage = new Stage(viewport, MarioBros.batch);

        stage.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        Table jump = new Table();
        table.left().bottom();

        Image upImg = new Image(new Texture("buttons/lineLight24.png"));
        upImg.setSize(bsize, bsize);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image downImg = new Image(new Texture("buttons/lineLight22.png"));
        downImg.setSize(bsize, bsize);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image rightImg = new Image(new Texture("buttons/lineLight23.png"));
        rightImg.setSize(bsize, bsize);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image leftImg = new Image(new Texture("buttons/lineLight22.png"));
        leftImg.setSize(bsize, bsize);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });
        Image fire = new Image(new Texture("buttons/lineLight47.png"));
        fire.setSize(bsize, bsize);
        fire.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fired = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                 fired = false;
            }
        });
        table.row().pad(5, 5, 10, 5);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight()).left();
        table.add();
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.setDebug(true);
        jump.bottom().right();
        jump.setFillParent(true);
        jump.setDebug(true);
        jump.add(fire).size(upImg.getWidth(), upImg.getHeight()).padBottom(5);
        jump.add(upImg).size(upImg.getWidth(), upImg.getHeight()).padBottom(5);
        stage.addActor(table);
        stage.addActor(jump);
    }

    public void draw(){
        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isfired() {
        return fired;
    }
    public void resize(int width, int height){
        viewport.update(width, height);
    }
}