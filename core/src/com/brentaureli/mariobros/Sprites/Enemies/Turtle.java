package com.brentaureli.mariobros.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Other.FireBall;

/**
 * Created by brentaureli on 9/14/15.
 */
public class Turtle extends com.brentaureli.mariobros.Sprites.Enemies.Enemy
{
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    float angle;
    private Array<FireBall> fireballs;
    private boolean runningRight;
    private int runtime;
    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 22; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("bayonet_charge", i)));
        walkAnimation = new Animation(0.05f, frames);
        frames.clear();

        stateTime = 0;
        setBounds(getX(), getY(), 30 / MarioBros.PPM, 30 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
      //  velocity = new Vector2(0, 0);
        runningRight = false;
        runtime = 0;

    }
    public TextureRegion getFrame(float dt){
        TextureRegion region;

        region = walkAnimation.getKeyFrame(stateTime, true);

        if(!runningRight && region.isFlipX() == false){
            region.flip(true, false);
        }
        if( runningRight && region.isFlipX() == true){
            region.flip(true, false);
        }

        return region;
    }
    public void update(float dt, float x)
    {
/*

        if ((b2body.getPosition().x + getWidth()) < x)
        {   runningRight = true;}
        else if ((b2body.getPosition().x ) > x)
        { runningRight = false;}
*/

        stateTime += dt;
        if ( runtime > 1000){
            runtime = 0;
        }
        else
            runtime ++;

        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            stateTime = 0;

            setRegion(new TextureRegion(screen.getAtlas().findRegion("hammer", 1), 0, 0, 4, 4));
        }
        else if(!destroyed) {

            setRegion(getFrame(dt));
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }

    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(9 / MarioBros.PPM, 13 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.FIREBALL_BIT;;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }
/*
    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false, false));
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }*/



    @Override
    public void hitOnHead() {
        setToDestroy = true;
        MarioBros.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void reverse() {

            reverseVelocity(true, false);
   if (runningRight)
       runningRight = false;
        else runningRight = true;
    }



}
