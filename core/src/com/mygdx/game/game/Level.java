package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.game.objects.AbstractGameObject;
import com.mygdx.game.game.objects.Jelly;
//import com.packtpub.libgdx.canyonbunny.game.objects.Clouds;
import com.mygdx.game.game.objects.Bottle;
import com.mygdx.game.game.objects.Box;
import com.mygdx.game.game.objects.Mountains;
import com.mygdx.game.game.objects.Brick;
//import com.packtpub.libgdx.canyonbunny.game.objects.WaterOverlay;

public class Level {

	public static final String TAG = Level.class.getName();

	public enum BLOCK_TYPE {
		EMPTY(0, 0, 0), // black
		ROCK(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		ITEM_FEATHER(255, 0, 255), // purple
		ITEM_GOLD_COIN(255, 255, 0); // yellow

		private int color;

		private BLOCK_TYPE (int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}

		public boolean sameColor (int color) {
			return this.color == color;
		}

		public int getColor () {
			return color;
		}
	}

	// player character
	public Jelly jelly;

	// objects
	public Array<Brick> bricks;
	public Array<Box> boxes;
	public Array<Bottle> bottles;

	// decoration
	//public Clouds clouds;
	public Mountains mountains;
	//public WaterOverlay waterOverlay;

	public Level (String filename) {
		init(filename);
	}

	private void init (String filename) {
		// player character
		jelly = null;

		// objects
		bricks = new Array<Brick>();
		boxes = new Array<Box>();
		bottles = new Array<Bottle>();

		// load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom-right
		int lastPixel = -1;
		for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				// height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				// get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x,y)
				// point and create the corresponding game object if there is
				// a match

				// empty space
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					// do nothing
				}
				// rock
				else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						obj = new Brick();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
						bricks.add((Brick)obj);
					} else {
						bricks.get(bricks.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new Jelly();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					jelly = (Jelly)obj;

				}
				// feather
				else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Bottle();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					bottles.add((Bottle)obj);

				}
				// gold coin
				else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new Box();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					boxes.add((Box)obj);

				}
				// unknown object/pixel color
				else {
					// red color channel
					int r = 0xff & (currentPixel >>> 24);
					// green color channel
					int g = 0xff & (currentPixel >>> 16);
					// blue color channel
					int b = 0xff & (currentPixel >>> 8);
					// alpha channel
					int a = 0xff & currentPixel;
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b
						+ "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}

		// decoration
		//clouds = new Clouds(pixmap.getWidth());
		//clouds.position.set(0, 2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);
		//waterOverlay = new WaterOverlay(pixmap.getWidth());
		//waterOverlay.position.set(0, -3.75f);

		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}

	public void update (float deltaTime) {
		// Bunny Head
		jelly.update(deltaTime);
		// Rocks
		for (Brick brick : bricks)
			brick.update(deltaTime);
		// Gold Coins
		for (Box box : boxes)
			box.update(deltaTime);
		// Feathers
		for (Bottle bottle : bottles)
			bottle.update(deltaTime);
		// Clouds
		//clouds.update(deltaTime);
	}

	public void render (SpriteBatch batch) {
		// Draw Mountains
		mountains.render(batch);
		// Draw Rocks
		for (Brick brick : bricks)
			brick.render(batch);
		// Draw Gold Coins
		for (Box box : boxes)
			box.render(batch);
		// Draw Feathers
		for (Bottle bottle : bottles)
			bottle.render(batch);
		// Draw Player Character
		jelly.render(batch);
		// Draw Water Overlay
		//waterOverlay.render(batch);
		// Draw Clouds
		//clouds.render(batch);
	}

}