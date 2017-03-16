package com.brentaureli.mariobros.Sprites.Other;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Screens.PlayScreen;

/**
 * Created by brentaureli on 10/12/15.
 */
public class FireBall extends Sprite {

    PlayScreen screen;
    World world;
    Array<TextureRegion> frames;
    Animation fireAnimation;
    float stateTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean fireRight;
float speed;
    float radius;
    Body b2body;
    public FireBall(PlayScreen screen, float x, float y, boolean fireRight, boolean FriendlyFire) {
        this.fireRight = fireRight;
        this.screen = screen;
        this.world = screen.getWorld();
        frames = new Array<TextureRegion>();
        if (FriendlyFire)
        {
            for (int i = 1; i < 5; i++) {
                frames.add(new TextureRegion(screen.getAtlas().findRegion("hammer", i), 0, 0, 24, 24));
            }
            speed = 1.5f;
            radius =  5 / MarioBros.PPM;
         }
        else
        {
        for(int i = 0; i < 2; i++)
            {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("shoot"), 0, 0, 24, 24));
            }
            speed = 0.8f;
            radius =  2 / MarioBros.PPM;
        }
        fireAnimation = new Animation(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));
setBounds(x, y, 6 / MarioBros.PPM, 6 / MarioBros.PPM);
        defineFireBall( FriendlyFire);
    }

    public void defineFireBall(boolean FriendlyFire){
        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 12 /MarioBros.PPM : getX() - 12 /MarioBros.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked())
        b2body = world.createBody(bdef);
        bdef.bullet = true;
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fdef.filter.categoryBits = MarioBros.FIREBALL_BIT;
        if (FriendlyFire) {
            fdef.filter.maskBits = MarioBros.GROUND_BIT |
                    MarioBros.COIN_BIT |
                    MarioBros.BRICK_BIT |
                    MarioBros.ENEMY_BIT |
                    MarioBros.OBJECT_BIT ;
        } else {
            fdef.filter.maskBits = MarioBros.GROUND_BIT |
                    MarioBros.COIN_BIT |
                    MarioBros.BRICK_BIT |
                    MarioBros.OBJECT_BIT |
                    MarioBros.MARIO_BIT;
        }
        fdef.shape = shape;
        fdef.restitution = 1;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);
        if (fireRight)
        b2body.applyLinearImpulse(speed, -0.25f, getWidth() / 2,  getHeight() / 2, false);
else
        b2body.applyLinearImpulse(-speed, -0.25f, getWidth() / 2,  getHeight() / 2, false);

        b2body.setGravityScale(0);
    }

    public void update(float dt){
        stateTime += dt;
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 1 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        if(b2body.getLinearVelocity().y > 2f)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }


}
