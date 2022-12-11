package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.io.IOException;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws IOException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(120);
		config.setIdleFPS(120);
		config.setTitle("My GDX Game");
		config.setResizable(true);
		//config.setTransparentFramebuffer(true);
		config.setWindowPosition(720, 340);
		config.setMaximized(true);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
