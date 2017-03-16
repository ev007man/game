package com.brentaureli.mariobros.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Sprites.Enemies.Enemy;
import com.brentaureli.mariobros.Sprites.Items.Item;
import com.brentaureli.mariobros.Sprites.Mario;
import com.brentaureli.mariobros.Sprites.Other.FireBall;
import com.brentaureli.mariobros.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by brentaureli on 9/4/15.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MarioBros.MARIO_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case MarioBros.MARIO_BIT | MarioBros.PICKUP_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.PICKUP_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT){
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                  //  ((Mario) fixA.getUserData()).weaponget();
                }
                else{
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                //    ((Mario) fixB.getUserData()).weaponget();
                }
                break;
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
             if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit();
                else
                    ((Mario) fixB.getUserData()).hit();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverse();
                else
                    ((Enemy)fixB.getUserData()).reverse();
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit();
                else
                    ((Mario) fixB.getUserData()).hit();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverse();
                ((Enemy)fixB.getUserData()).reverse();
                break;
            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).destroy();
                else
                    ((Item)fixB.getUserData()).destroy();
                break;
            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
            case MarioBros.FIREBALL_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case MarioBros.MARIO_BIT | MarioBros.FIREBALL_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit();
                else
                    ((Mario) fixB.getUserData()).hit();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.FIREBALL_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
