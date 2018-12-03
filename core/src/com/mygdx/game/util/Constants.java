package com.mygdx.game.util;

public class Constants {

	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;

	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	// Location of description file for texture atlas
	//Change this to correct pack
	public static final String TEXTURE_ATLAS_OBJECTS = "..//CSC361_F18_Frankenberry-core//images//pack.atlas";
	public static final String TEXTURE_ATLAS_UI = "//CSC361_F18_Frankenberry-core//images//uipack.atlas";
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";

	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	public static final String SKIN_CANYONBUNNY_UI = "images/canyonbunny-ui.json";

	// Location of image file for level 01
	public static final String LEVEL_01 = "..//core//levels//level-01.png";
	
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;

	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	// Amount of extra lives at level start
	public static final int LIVES_START = 3;
	
	// Duration of feather power-up in seconds
	public static final float ITEM_BOTTLE_POWERUP_DURATION = 9;

	// Delay after game over
	public static final float TIME_DELAY_GAME_OVER = 3;
	
	// Game preferences file
	public static final String PREFERENCES = "JuiceRun.prefs";


}