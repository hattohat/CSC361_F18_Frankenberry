package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Constants;

public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();

	private AssetManager assetManager;

	public AssetFonts fonts;
	public AssetJelly jelly;
	public AssetBrick brick;
	public AssetBox box;
	//public AssetFeather bottle;
	public AssetLevelDecoration levelDecoration;

	// singleton: prevent instantiation from other classes
	private Assets () {
	}
	
	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts () {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public class AssetJelly {
		public final AtlasRegion head;

		public AssetJelly (TextureAtlas atlas) {
			head = atlas.findRegion("Character");
		}
	}

	public class AssetBrick {
		public final AtlasRegion edge;
		//public final AtlasRegion middle;

		public AssetBrick (TextureAtlas atlas) {
			edge = atlas.findRegion("Brick");
			//middle = atlas.findRegion("rock_middle");
		}
	}

	public class AssetBox {
		public final AtlasRegion box;

		public AssetBox (TextureAtlas atlas) {
			box = atlas.findRegion("Box");
		}
	}

	public class AssetFeather {
		public final AtlasRegion feather;

		public AssetFeather (TextureAtlas atlas) {
			feather = atlas.findRegion("item_feather");
		}
	}

	public class AssetLevelDecoration {
		//public final AtlasRegion cloud01;
		//public final AtlasRegion cloud02;
		//public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		//public final AtlasRegion waterOverlay;

		public AssetLevelDecoration (TextureAtlas atlas) {
			//cloud01 = atlas.findRegion("cloud01");
			//cloud02 = atlas.findRegion("cloud02");
			//cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("Mountain1");
			mountainRight = atlas.findRegion("Mountain2");
			//waterOverlay = atlas.findRegion("water_overlay");
		}
	}

	public void init (AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		// create game resource objects
		fonts = new AssetFonts();
		jelly = new AssetJelly(atlas);
		brick = new AssetBrick(atlas);
		box = new AssetBox(atlas);
		//feather = new AssetFeather(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
	}

	@Override
	public void dispose () {
		assetManager.dispose();
	}


	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception)throwable);
		
	}

}