package com.badlogic.drop.screens;

import com.badlogic.drop.Drop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Created by mkemp on 8/11/17.
 */

public class GameScreen implements Screen {
    final Drop game;

    private Texture dropImage;
    private Texture bucketImage;
    private Sprite bucketSprite;

    private Sound dropSound;
    private Music rainMusic;

    private OrthographicCamera camera;

    private Rectangle bucket;
    //private Vector3 touchPos;

    // Better tha array list. Minimizes garbage.
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    int dropsGathered;

    int timeToDrop;
    int dropsFallen;

    public GameScreen(final Drop game) {
        this.game = game;

        // Load images
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // Load sounds
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // Create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Create bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        // Make sprite for bucket
        bucketSprite = new Sprite(bucketImage,
                ((int)bucket.width), ((int)bucket.height));

        // Create and spawn a raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();

        timeToDrop = 200;
        dropsFallen = 0;
    }

    /**
     * Spawns a raindrop at a random position at the top of the screen.
     */
    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
        dropsFallen++;
        if (dropsFallen % 5 == 0) timeToDrop += 50;
    }

    @Override
    public void show() {

        // Play music
        rainMusic.play();
    }

    @Override
    public void render(float delta) {

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Render bucket
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(bucketSprite, bucket.x, bucket.y,
                bucketSprite.getWidth()/2, bucketSprite.getHeight()/2,
                bucketSprite.getWidth(), bucketSprite.getHeight(),
                1f, 1.5f, 0f);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // Center bucket around touch
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        // Move bucket on key press
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // Stay within screen limits
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        // Spawn a raindrop if enough time has passed
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // Make raindrops move
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= timeToDrop * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                iter.remove();
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
