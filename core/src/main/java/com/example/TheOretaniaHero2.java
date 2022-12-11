package com.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TheOretaniaHero2 extends ApplicationAdapter {
	Stage stage;
	OrthographicCamera camera;
	TiledMap map;
	private BitmapFont font;

	OrthogonalTiledMapRenderer mapRenderer;
	private float offsetX, offsetY;
	private int mapWidthInPixels;
	private int mapHeightInPixels;
	private SpriteBatch batch;
	Hero hero;
	Viewport viewport;

	@Override
	public void create() {
		stage = new Stage();
		camera = new OrthographicCamera();

		map = new TmxMapLoader().load("Mapa_ut02_b02_05.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		Gdx.input.setInputProcessor(stage);

		hero = new Hero(map);

		font = new BitmapFont();
		font.setColor(Color.WHITE);

		stage.addActor(hero);
		stage.setKeyboardFocus(hero);

		MapProperties properties = map.getProperties();
		int tileWidth = properties.get("tilewidth", Integer.class);
		int tileHeight = properties.get("tileheight", Integer.class);
		int mapWidthInTiles = properties.get("width", Integer.class);
		int mapHeightInTiles = properties.get("height", Integer.class);
		mapWidthInPixels = mapWidthInTiles * tileWidth;
		mapHeightInPixels = mapHeightInTiles * tileHeight;
		offsetX = 0;
		offsetY = 0;
		camera.setToOrtho(false, 800, 480);
		viewport = new ScreenViewport(camera);
		stage.setViewport(viewport);
		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float delta = Gdx.graphics.getDeltaTime();
		stage.act(Gdx.graphics.getDeltaTime());

		//x="343.985" y="234.962"

		if (hero.getX() > (offsetX + camera.viewportWidth) * 0.9) {
			offsetX += 100 * delta;
		}

		if (hero.getX() < (offsetX + 0.1 * camera.viewportWidth)) {
			offsetX -= 100 * delta;
		}

		if (hero.getY() - mapHeightInPixels + camera.viewportHeight > (offsetY + camera.viewportHeight - hero.getHeight()) * 0.9) {
			offsetY += 100 * delta;
		}

		if (hero.getY() - mapHeightInPixels + camera.viewportHeight < (offsetY + 0.1 * camera.viewportHeight)) {
			offsetY -= 100 * delta;
		}

		//Limites
		if (offsetX < 0) offsetX = 0;
		if (offsetY > 0) offsetY = 0;
		if (offsetX > mapWidthInPixels - camera.viewportWidth) offsetX = mapWidthInPixels - camera.viewportWidth;
		if (offsetY < -mapHeightInPixels + camera.viewportHeight) offsetY = -mapHeightInPixels + camera.viewportHeight;

		//Limites al personaje
		if (hero.getX() < 0) hero.setX(0);
		if (hero.getY() < 0) hero.setY(0);
		if (hero.getX() > mapWidthInPixels - hero.getWidth()) hero.setX(mapWidthInPixels - hero.getWidth());
		if (hero.getY() > mapHeightInPixels - hero.getHeight()) hero.setY(mapHeightInPixels - hero.getHeight());

		//Camara
		camera.position.x = camera.viewportWidth / 2 + offsetX;
		camera.position.y = mapHeightInPixels - camera.viewportHeight / 2 + offsetY;
		camera.update();

		//Mapa y stage
		mapRenderer.setView(camera);
		mapRenderer.render();
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(false, width, height);
		camera.position.x = camera.viewportWidth / 2 + offsetX;
		camera.position.y = mapHeightInPixels - camera.viewportHeight / 2 + offsetY;
		camera.update();
	}

	@Override
	public void dispose() {
		stage.dispose();
		map.dispose();
		mapRenderer.dispose();
	}
}