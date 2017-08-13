package com.badlogic.drop;

import com.badlogic.drop.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {

	public SpriteBatch batch;
	public BitmapFont font;

	@Override
	public void create () {

		// Create sprite batch
		batch = new SpriteBatch();

		// Default is arial
		font = new BitmapFont();

		this.setScreen(new GameScreen(this));

	}

	@Override
	public void render () {

		// Important!
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}
}
