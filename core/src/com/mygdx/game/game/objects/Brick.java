package com.mygdx.game.game.objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.game.Assets;

public class Brick extends AbstractGameObject {

	private TextureRegion regEdge;
	//private TextureRegion regMiddle;

	private int length;

	public Brick () {
		init();
	}

	private void init () {
		dimension.set(1, 1.5f);

		regEdge = Assets.instance.brick.edge;
		//regMiddle = Assets.instance.brick.middle;

		// Start length of this rock
		setLength(1);
	}

	public void setLength (int length) {
		this.length = length;
	}

	public void increaseLength (int amount) {
		setLength(length + amount);
	}

	@Override
	public void render (SpriteBatch batch) {
		TextureRegion reg = null;

		float relX = 0;
		float relY = 0;


		// Draw left edge
		reg = regEdge;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x / 4, dimension.y,
			scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false,
			false);

		// Draw middle
		relX = 0;
        reg = regEdge;
		for (int i = 0; i < length; i++) {
			batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false,
				false);
			relX += dimension.x;
		}

		// Draw right edge
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4,
			dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
			reg.getRegionHeight(), true, false);
	}

}