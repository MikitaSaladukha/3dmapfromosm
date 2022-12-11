package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.Buttons;
import de.topobyte.osm4j.core.access.OsmInputException;

import java.io.IOException;

public class Menu implements Screen {

    final MyGdxGame game;
    OrthographicCamera camera;
    SpriteBatch batch;
    BitmapFont font;
    Texture bgImage;
    Buttons buttons;

    public Menu(final MyGdxGame myGdxGame) throws IOException, OsmInputException {
        game = myGdxGame;

        batch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);


        createBackground();
        buttons = new Buttons(game);
        Gdx.input.setInputProcessor(buttons);
    }

    private void createBackground() {

        bgImage = new Texture("background.jpg");
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glFrontFace(GL20.GL_FRONT_AND_BACK);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgImage,0,0);
        font.draw(batch, "Welcome to the game!", 200, 650);
        font.draw(batch, "Press the ESC to return to the menu", 300,425);
        batch.end();

       buttons.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        bgImage.dispose();
    }
}
