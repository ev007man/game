package com.brentaureli.mariobros.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Other.FireBall;

/**
 * Created by brentaureli on 9/14/15.
 */
public class Boss extends Enemy
{
    public enum State { STANDING, RUNNING, FIRING, DEAD };
    public State currentState;
    public State previousState;

    private float stateTime;
    private Animation walkAnimation;
    private TextureRegion stand;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    float angle;
    private Array<FireBall> fireballs;
    private boolean runningRight;
    private int armour;
private int runtime;
    public Boss(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i =  1; i < 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("thiers", i)));
        walkAnimation = new Animation(0.09f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 60 / MarioBros.PPM, 60 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
        velocity = new Vector2(0, 0);
        runningRight = false;
        runtime = 0;
armour = 4;
        fireballs = new Array<FireBall>();

        stand = new TextureRegion(screen.getAtlas().findRegion("thiers", 1));
    }
    public TextureRegion getFrame(float dt, float x) {
        currentState = getState(x);

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch (currentState) {
             case RUNNING:
            region = walkAnimation.getKeyFrame(stateTime, true);
            break;
            default: region = stand;
                break;
        }
            if (!runningRight && region.isFlipX() == true) {
                region.flip(true, false);
            }
            if (runningRight && region.isFlipX() == false) {
                region.flip(true, false);
            }

            return region;
        }

    public State getState(float x) {
        //Test to Box2D for velocity on the X and Y-Axis
        //if the boss is far away he moves closer
        if ((b2body.getPosition().x - x) > 10)

        {     return State.RUNNING;}
    else  return State.STANDING;

    }

    public void update(float dt, float x)
    {
        setRegion(getFrame(dt,x));

       if ((b2body.getPosition().x + getWidth()) < x)
       {   runningRight = true;}
        else if ((b2body.getPosition().x ) > x)
       { runningRight = false;}
        if ((runtime % 60)  == 0)
        {
            fire();
        }
        stateTime += dt;
    if ( runtime > 1000){
    runtime = 0;
    }
        else
    runtime ++;

        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("hammer", 1), 0, 0, 4, 4));
            stateTime = 0;
        }
        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
           // setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() - getWidth(), getY() + getHeight());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(21 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.OBJECT_BIT ;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape body = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-10, 28).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(5, 28).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-10, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(-10, 3).scl(1 / MarioBros.PPM);
        body.set(vertice);

        fdef.shape = body;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT |
        MarioBros.FIREBALL_BIT;

        fdef.shape = body;
        b2body.createFixture(fdef).setUserData(this);


    }

    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y + (getHeight() / 5), runningRight ? true : false, false));
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }



    @Override
    public void hitOnHead() {
        if (armour > 0) {
            armour--;
            MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
        } else {
            setToDestroy = true;
            MarioBros.manager.get("audio/sounds/stomp.wav", Sound.class).play();
        }
    }
    @Override
    public void reverse() {

            reverseVelocity(true, false);
    }



}
