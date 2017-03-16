package com.brentaureli.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Scenes.Hud;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Mario;

/**
 * Created by brentaureli on 8/28/15.
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(1);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();

    }

}
