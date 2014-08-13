package com.allsrc.monstertrucks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.allsrc.monstertrucks.MonsterTrucks;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 640;
        config.height = 480;
        config.fullscreen = true;

		new LwjglApplication(new MonsterTrucks(), config);
	}
}
