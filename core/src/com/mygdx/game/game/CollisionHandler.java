package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.game.objects.AbstractGameObject;
import com.mygdx.game.game.objects.Jelly;
import com.mygdx.game.game.objects.Jelly.JUMP_STATE;
import com.mygdx.game.game.objects.Bottle;
import com.mygdx.game.game.objects.Box;
import com.mygdx.game.game.objects.Brick;
import com.mygdx.game.game.WorldController;

public class CollisionHandler implements ContactListener
{
    private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;

    private WorldController controller;

    public CollisionHandler(WorldController w)
    {
	controller = w;
	listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
    }

    public void addListener(short categoryA, short categoryB, ContactListener listener)
    {
	addListenerInternal(categoryA, categoryB, listener);
	addListenerInternal(categoryB, categoryA, listener);
    }

    @Override
    public void beginContact(Contact contact)
    {
	Fixture fixtureA = contact.getFixtureA();
	Fixture fixtureB = contact.getFixtureB();

	//Gdx.app.log("CollisionHandler-begin A", "begin");

	ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
	if (listener != null)
	{
	    listener.beginContact(contact);
	}
    }

    @Override
    public void endContact(Contact contact)
    {
	Fixture fixtureA = contact.getFixtureA();
	Fixture fixtureB = contact.getFixtureB();

	//Gdx.app.log("CollisionHandler-end A", "end");

	// Gdx.app.log("CollisionHandler-end A", fixtureA.getBody().getLinearVelocity().x+" : "+fixtureA.getBody().getLinearVelocity().y);
	// Gdx.app.log("CollisionHandler-end B", fixtureB.getBody().getLinearVelocity().x+" : "+fixtureB.getBody().getLinearVelocity().y);
	ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
	if (listener != null)
	{
	    listener.endContact(contact);
	}
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
	//Gdx.app.log("CollisionHandler-preSolve A", "preSolve");
	Fixture fixtureA = contact.getFixtureA();
	Fixture fixtureB = contact.getFixtureB();
	ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
	if (listener != null)
	{
	    listener.preSolve(contact, oldManifold);
	}
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
	//Gdx.app.log("CollisionHandler-postSolve A", "postSolve");
	Fixture fixtureA = contact.getFixtureA();
	Fixture fixtureB = contact.getFixtureB();

	processContact(contact);

	ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
	if (listener != null)
	{
	    listener.postSolve(contact, impulse);
	}
    }

    private void addListenerInternal(short categoryA, short categoryB, ContactListener listener)
    {
	ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
	if (listenerCollection == null)
	{
	    listenerCollection = new ObjectMap<Short, ContactListener>();
	    listeners.put(categoryA, listenerCollection);
	}
	listenerCollection.put(categoryB, listener);
    }

    private ContactListener getListener(short categoryA, short categoryB)
    {
	ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
	if (listenerCollection == null)
	{
	    return null;
	}
	return listenerCollection.get(categoryB);
    }

    private void processContact(Contact contact)
    {
//	//Gdx.app.log("CollisionHandler-process A", "process");
    	Fixture fixtureA = contact.getFixtureA();
    	Fixture fixtureB = contact.getFixtureB();
    	AbstractGameObject objA = (AbstractGameObject) fixtureA.getBody().getUserData();
		AbstractGameObject objB = (AbstractGameObject) fixtureB.getBody().getUserData();

		if ((objA instanceof Jelly) && (objB instanceof Brick))
		{
			processBrickContact(fixtureA, fixtureB);
		}
		else if ((objB instanceof Jelly) && (objA instanceof Brick))
		{
			processBrickContact(fixtureB, fixtureA);
		}
		else if ((objA instanceof Jelly) && (objB instanceof Box))
		{
			processBoxContact(fixtureA, fixtureB);
		}
		else if ((objB instanceof Jelly) && (objA instanceof Box))
		{
			processBoxContact(fixtureB, fixtureA);
		}
		else if ((objA instanceof Jelly) && (objB instanceof Bottle))
		{
			processBottleContact(fixtureA, fixtureB);
		}
		else if ((objB instanceof Jelly) && (objA instanceof Bottle))
		{
			processBottleContact(fixtureB, fixtureA);
		}
    }

    private void processBrickContact(Fixture jellyFixture, Fixture brickFixture)
    {
    	Jelly jelly = (Jelly) brickFixture.getBody().getUserData();
    	Brick brick = (Brick) jellyFixture.getBody().getUserData();
    	jelly = Level.jelly;
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
    
    private void processBoxContact(Fixture jellyFixture, Fixture boxFixture)
    {
    	Jelly jelly = (Jelly) boxFixture.getBody().getUserData();
    	Box box = (Box) jellyFixture.getBody().getUserData();
    	System.out.println("Score + 1");
    	box.collected = true;
    	controller.score += box.getScore();
    }
    
    private void processBottleContact(Fixture jellyFixture, Fixture bottleFixture)
    {
    	Jelly jelly = (Jelly) bottleFixture.getBody().getUserData();
    	Bottle bottle = (Bottle) jellyFixture.getBody().getUserData();
    	//System.out.println("Score + 1");
    	bottle.collected = true;
    	//controller.score += box.getScore();
    }

}