package com.brentaureli.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Other.FireBall;
import com.brentaureli.mariobros.Scenes.Hud;

/**
 * Created by brentaureli on 8/27/15.
 */
public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, FIRING, GROWING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private Animation bigMarioRun;
    private Animation growMario;
public float position;
    private float stateTimer;
    private boolean runningRight;
    private short armour;
    public boolean weapon;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private PlayScreen screen;
public short balls;
    private Array<FireBall> fireballs;

    public Mario(PlayScreen screen){
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
balls = 0;
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get run animation frames and add them to marioRun Animation
        for(int i = 7; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", i)));
        marioRun = new Animation(0.2f, frames);
        frames.clear();

        for(int i = 7; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", i)));
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();

        //get set animation frames from growing mario
        for(int i = 7; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet",  i)));
        growMario = new Animation(0.2f, frames);


        //get jump animation frames and add them to marioJump Animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", 10));

        //create texture region for mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", 7));

        //create dead mario texture region
        marioDead = new TextureRegion(screen.getAtlas().findRegion("Marx-Sprite-sheet", 1));

        //define mario in Box27
        defineMario();

        //set initial values for marios location, width and height. And initial frame as marioStand.
        setBounds(10, 10, 20 / MarioBros.PPM, 30 / MarioBros.PPM);
        setRegion(marioStand);
armour = 5;
        fireballs = new Array<FireBall>();

    }

    public void update(float dt){

        // time is up : too late mario dies T_T
        // the !isDead() method is used to prevent multiple invocation
        // of "die music" and jumping
        // there is probably better ways to do that but it works for now.

        //update our sprite to correspond with the position of our Box2D body
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        position = b2body.getPosition().x - getWidth();
        //update sprite with the correct frame depending on marios current action
        setRegion(getFrame(dt));
        if(timeToRedefineMario)
            redefineMario();
        if ( b2body.getPosition().y < 0 ) {
                die();
        }
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed()){
                balls = 0;
                fireballs.removeValue(ball, true);}
        }

    }

    public TextureRegion getFrame(float dt){
        //get marios current state. ie. jumping, running, standing...
        currentState = getState();

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region =  marioJump;
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            case FIRING:
            default:
                region =  marioStand;
                break;
        }

        //if mario is running left and the texture isnt facing left... flip it.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        //if mario is running right and the texture isnt facing right... flip it.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;

    }

    public State getState(){
        //Test to Box2D for velocity on the X and Y-Axis
        //if mario is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        //if negative in Y-Axis mario is falling
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        //if mario is positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        //if none of these return then he must be standing
        else if (balls > 1)
            return State.FIRING;
            else
            return State.STANDING;
    }



    public void die() {

        if (!isDead()) {

            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

       //     b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }
    public void weaponget() {
        weapon = true;
    }
    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    public void hit(){

            if (armour > 0) {
                armour--;
                Hud.getHealth(armour);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;

    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(100 / MarioBros.PPM, 60 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(11 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.PICKUP_BIT |
                MarioBros.FIREBALL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public boolean fire(){
        if ( currentState != State.FIRING && balls == 0) {

        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false, true));
            currentState = State.FIRING;
            return true;
    } return false;}

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs){

        balls++;
            ball.draw(batch);}
    }
}
