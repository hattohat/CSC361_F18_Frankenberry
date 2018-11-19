package com.mygdx.game.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.game.objects.Jelly;
import com.mygdx.game.game.objects.Jelly.JUMP_STATE;
import com.mygdx.game.game.objects.Bottle;
import com.mygdx.game.game.objects.Box;
import com.mygdx.game.game.objects.Brick;
import com.mygdx.game.util.CameraHelper;
import com.mygdx.game.util.Constants;

public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();

	public Level level;
	public int lives;
	public int score;

	public CameraHelper cameraHelper;

	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	private float timeLeftGameOverDelay;

	public WorldController () {
		init();
	}

	private void init () {
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		initLevel();
	}

	private void initLevel () {
		score = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.jelly);
	}

	public void update (float deltaTime) {
		handleDebugInput(deltaTime);
		/*if (isGameOver()) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0) init();
		} else {
			handleInputGame(deltaTime);
		}*/
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		if (!isGameOver() && isPlayerInWater()) {
			lives--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
	}

	public boolean isGameOver () {
		return lives < 0;
	}

	public boolean isPlayerInWater () {
		return level.jelly.position.y < -5;
	}

	private void testCollisions () {
		r1.set(level.jelly.position.x, level.jelly.position.y, level.jelly.bounds.width, level.jelly.bounds.height);

		// Test collision: Bunny Head <-> Rocks
		for (Brick brick : level.bricks) {
			r2.set(brick.position.x, brick.position.y, brick.bounds.width, brick.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionJellyWithBrick(brick);
			// IMPORTANT: must do all collisions for valid edge testing on rocks.
		}

		// Test collision: Bunny Head <-> Gold Coins
		for (Box box : level.boxes) {
			if (box.collected) continue;
			r2.set(box.position.x, box.position.y, box.bounds.width, box.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionJellyWithBox(box);
			break;
		}

		// Test collision: Bunny Head <-> Feathers
		for (Bottle bottle : level.bottles) {
			if (bottle.collected) continue;
			r2.set(bottle.position.x, bottle.position.y, bottle.bounds.width, bottle.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionJellyWithBottle(bottle);
			break;
		}
	}

	private void onCollisionJellyWithBrick (Brick brick) {
		Jelly jelly = level.jelly;
		float heightDifference = Math.abs(jelly.position.y - (brick.position.y + brick.bounds.height));
		if (heightDifference > 0.25f) {
			boolean hitRightEdge  = jelly.position.x > (brick.position.x + brick.bounds.width / 2.0f);
			if (hitRightEdge ) {
				jelly.position.x = brick.position.x + brick.bounds.width;
			} else {
				jelly.position.x = brick.position.x - jelly.bounds.width;
			}
			return;
		}

		switch (jelly.jumpState) {
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			jelly.position.y = brick.position.y + jelly.bounds.height + jelly.origin.y;
			jelly.jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_RISING:
			jelly.position.y = brick.position.y + jelly.bounds.height + jelly.origin.y;
			break;
		}
	}

	private void onCollisionJellyWithBox (Box box) {
		box.collected = true;
		score += box.getScore();
		Gdx.app.log(TAG, "Box collected");
	}

	private void onCollisionJellyWithBottle (Bottle bottle) {
		bottle.collected = true;
		score += bottle.getScore();
		level.jelly.setBottlePowerup(true);
		Gdx.app.log(TAG, "Bottle collected");
	}

	private void handleDebugInput (float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		if (!cameraHelper.hasTarget(level.jelly)) {
			// Camera Controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}

	private void handleInputGame (float deltaTime) {
		if (cameraHelper.hasTarget(level.jelly)) {
			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.jelly.velocity.x = -level.jelly.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.jelly.velocity.x = level.jelly.terminalVelocity.x;
			} else {
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					level.jelly.velocity.x = level.jelly.terminalVelocity.x;
				}
			}

			// Bunny Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
				level.jelly.setJumping(true);
			else
				level.jelly.setJumping(false);
		}
	}

	private void moveCamera (float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.jelly);
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}
}