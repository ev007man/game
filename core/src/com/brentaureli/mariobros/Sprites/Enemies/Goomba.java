package com.brentaureli.mariobros.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
public class Goomba extends com.brentaureli.mariobros.Sprites.Enemies.Enemy
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
    private boolean boss;
    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
//      if (object.getProperties().containsKey("mushroom"))
//
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("shoot", i)));
        walkAnimation = new Animation(0.09f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 30 / MarioBros.PPM, 30 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
        velocity = new Vector2(0, 0);
        runningRight = false;
        runtime = 0;

        fireballs = new Array<FireBall>();
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
        setRegion(getFrame(dt));

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
            setRegion(new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", 1)));
            stateTime = 0;
        }
        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
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
                MarioBros.MARIO_BIT|
                MarioBros.FIREBALL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 18).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(5, 18).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.8f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }


    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false, false));
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }



    @Override
    public void hitOnHead() {
        setToDestroy = true;
        MarioBros.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void reverse() {

            reverseVelocity(true, false);
    }



}
