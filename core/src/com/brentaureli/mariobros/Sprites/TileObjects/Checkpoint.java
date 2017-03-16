
package com.brentaureli.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Scenes.Hud;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Mario;

/**
 * Created by brentaureli on 8/28/15.
 */
public class Checkpoint extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 2;

    public Checkpoint(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.PICKUP_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
      /*  if(getCell().getTile().getId() == BLANK_COIN)
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                        Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else*/
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            getCell().setTile(tileSet.getTile(BLANK_COIN));
        setCategoryFilter(MarioBros.DESTROYED_BIT);
//       PlayScreen.CheckpointTime = true;
            Hud.addScore(100);
        //}
    }
}