package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.MyGdxGame;
import de.topobyte.osm4j.core.access.OsmInputException;

import java.io.IOException;

public class Buttons extends Stage {
    final MyGdxGame game;
    Texture buttonUpImage;
    Texture buttonDownImage;
    TextButton button;
    TextButton button2;
    public Rotation rotation;
    public TriangleRotation triangleRotation;


    public Buttons(final MyGdxGame myGdxGame) throws IOException, OsmInputException {
        game = myGdxGame;
        rotation = new Rotation(game);
        triangleRotation = new TriangleRotation(game);

        buttonUpImage = new Texture("badlogic.jpg");
        buttonDownImage = new Texture("badlogic.jpg");

        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();

        skin.add("button_up", buttonUpImage);
        skin.add("button_down", buttonDownImage);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("button_up");
        textButtonStyle.down = skin.getDrawable("button_down");
        textButtonStyle.checked = skin.getDrawable("button_up");

        button = new TextButton("Test game", textButtonStyle);
        button.setPosition(50, 125);
        addActor(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                System.out.println("its all done!");
                //game.setScreen(game);
                game.setScreen(rotation);
                Gdx.input.setInputProcessor(rotation);
                dispose();
            }
        });

        button2 = new TextButton("Another button", textButtonStyle);
        button2.setPosition(350, 125);
        addActor(button2);

        button2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("the second button is done!");
                game.setScreen(triangleRotation);
                Gdx.input.setInputProcessor(triangleRotation);
                dispose();
            }
        });

    }
}







