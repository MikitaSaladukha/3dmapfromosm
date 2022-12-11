package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.Screens.Menu;
import com.mygdx.game.Screens.Rotation;
import de.topobyte.osm4j.core.access.OsmInputException;

import java.io.IOException;

public class MyGdxGame extends Game {

	public Menu menu;
	public Rotation rotation;


	@Override
	public void create () {

		try {
			menu = new Menu(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (OsmInputException e) {
			throw new RuntimeException(e);
		}
		//rotation = new Rotation(this);
		setScreen(menu);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
