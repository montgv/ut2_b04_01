package com.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class Hero extends Actor {
    enum VerticalMovement {UP, NONE, DOWN}

    enum HorizontalMovement {LEFT, NONE, RIGHT}

    private static TextureRegion[] textureRegions;
    private static final TextureRegion[] arribaMov = new TextureRegion[2];
    private static final TextureRegion[] abajoMov = new TextureRegion[2];
    private static final TextureRegion[] derechaMov = new TextureRegion[2];
    private static final TextureRegion[] izquierdaMov = new TextureRegion[2];

    private static TextureRegion arriba;
    private static TextureRegion abajo;
    private static TextureRegion derecha;
    private static TextureRegion izquierda;

    float stateTime;

    private static TextureRegion actual;

    private Animation<TextureRegion> animation;
    private final Animation<TextureRegion> animationArriba;
    private final Animation<TextureRegion> animationAbajo;
    private final Animation<TextureRegion> animationDerecha;
    private final Animation<TextureRegion> animationIzquierda;

    TiledMap map;
    MapLayer positionLayer;
    MapObject playerStrat;
    int jugadorWidth, jugadorHeight;

    float ultimaX, ultimaY;
    TiledMapTileLayer obstacleLayer;
    HorizontalMovement horizontalMovement;
    VerticalMovement verticalMovement;
    private static final int FRAME_COLS = 3, FRAME_ROWS = 4;

    public Hero(TiledMap map) {
        if (textureRegions == null) {
            //Sacamos las texturas cuando el personaje ocupa el mismo espacio en las texture region
            Texture completo = new Texture(Gdx.files.internal("UT02_B02_Heroe.png"));
            jugadorWidth = completo.getWidth() / FRAME_COLS;
            jugadorHeight = completo.getHeight() / FRAME_ROWS;
            TextureRegion[][] tmp = TextureRegion.split(completo,
                    completo.getWidth() / FRAME_COLS,
                    completo.getHeight() / FRAME_ROWS);
            textureRegions = new TextureRegion[FRAME_COLS * FRAME_ROWS];
            int index = 0;
            for (int i = 0; i < FRAME_ROWS; i++) {
                for (int j = 0; j < FRAME_COLS; j++) {
                    textureRegions[index++] = tmp[i][j];
                }
            }
            arribaMov[0] = textureRegions[0];
            arriba = textureRegions[1];
            arribaMov[1] = textureRegions[2];
            derechaMov[0] = textureRegions[3];
            derecha = textureRegions[4];
            derechaMov[1] = textureRegions[5];
            izquierdaMov[0] = textureRegions[9];
            izquierda = textureRegions[10];
            izquierdaMov[1] = textureRegions[11];
            abajoMov[0] = textureRegions[6];
            abajo = textureRegions[7];
            abajoMov[1] = textureRegions[8];
        }

        actual = abajo;
        horizontalMovement = HorizontalMovement.NONE;
        verticalMovement = VerticalMovement.NONE;

        //Animamos al personaje
        animationArriba = new Animation<>(0.15f, arribaMov);
        animationAbajo = new Animation<>(0.15f, abajoMov);
        animationDerecha = new Animation<>(0.15f, derechaMov);
        animationIzquierda = new Animation<>(0.15f, izquierdaMov);

        stateTime = 0f;

        positionLayer = map.getLayers().get("objetos");
        playerStrat = positionLayer.getObjects().get("jugador");

        obstacleLayer = (TiledMapTileLayer) map.getLayers().get("obstaculos");

        setSize(actual.getRegionWidth(), actual.getRegionHeight());
        setPosition(playerStrat.getProperties().get("x", Float.class), playerStrat.getProperties().get("y", Float.class));
        addListener(new HeroListener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(actual, getX(), getY());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        TiledMapTileLayer.Cell cell;
        TiledMapTileLayer.Cell cell2;

        ultimaX = getX();
        ultimaY = getY();


        if (verticalMovement == VerticalMovement.UP) {
            cell = obstacleLayer.getCell(MathUtils.round(getX()) / 32 , MathUtils.round(getY() + 1) / 32);
            if (cell == null) {
                this.moveBy(0, 100 * delta);
            }
        }

        if (verticalMovement == VerticalMovement.DOWN) {
            cell = obstacleLayer.getCell(MathUtils.round(getX()) / 32 , MathUtils.round(getY() - 1) / 32);
            if (cell == null) {
                this.moveBy(0, -100 * delta);
            }

        }

        if (horizontalMovement == HorizontalMovement.LEFT) {
            cell = obstacleLayer.getCell(MathUtils.round(getX() - 1) / 32 , MathUtils.round(getY()) / 32);
            if (cell == null) {
                this.moveBy(-100 * delta, 0);
            }
        }


        if (horizontalMovement == HorizontalMovement.RIGHT) {
            cell = obstacleLayer.getCell(MathUtils.round(getX() + 1) / 32 , MathUtils.round(getY()) / 32);
            if(cell == null) {
                this.moveBy(100 * delta, 0);
            }

        }

        if (verticalMovement == VerticalMovement.UP && horizontalMovement == HorizontalMovement.LEFT) {
            animation = animationArriba;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.UP && horizontalMovement == HorizontalMovement.NONE) {
            animation = animationArriba;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.UP && horizontalMovement == HorizontalMovement.RIGHT) {
            animation = animationArriba;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.NONE && horizontalMovement == HorizontalMovement.LEFT) {
            animation = animationIzquierda;
            actual = animation.getKeyFrame(stateTime, true);
        }
        if (verticalMovement == VerticalMovement.NONE && horizontalMovement == HorizontalMovement.NONE) {
            if (animation == animationAbajo) actual = abajo;
            if (animation == animationArriba) actual = arriba;
            if (animation == animationDerecha) actual = derecha;
            if (animation == animationIzquierda) actual = izquierda;
        }
        if (verticalMovement == VerticalMovement.DOWN && horizontalMovement == HorizontalMovement.LEFT) {
            animation = animationAbajo;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.DOWN && horizontalMovement == HorizontalMovement.RIGHT) {
            animation = animationAbajo;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.NONE && horizontalMovement == HorizontalMovement.RIGHT) {
            animation = animationDerecha;
            actual = animation.getKeyFrame(stateTime, true);
        }

        if (verticalMovement == VerticalMovement.DOWN && horizontalMovement == HorizontalMovement.NONE) {
            animation = animationAbajo;
            actual = animation.getKeyFrame(stateTime, true);
        }
    }

    class HeroListener extends InputListener {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            switch (keycode) {
                case Input.Keys.DOWN:
                    verticalMovement = VerticalMovement.DOWN;
                    break;
                case Input.Keys.UP:
                    verticalMovement = VerticalMovement.UP;
                    break;
                case Input.Keys.LEFT:
                    horizontalMovement = HorizontalMovement.LEFT;
                    break;
                case Input.Keys.RIGHT:
                    horizontalMovement = HorizontalMovement.RIGHT;
                    break;
            }
            return true;
        }

        @Override
        public boolean keyUp(InputEvent event, int keycode) {
            switch (keycode) {
                case Input.Keys.DOWN:
                    if (verticalMovement == VerticalMovement.DOWN) {
                        verticalMovement = VerticalMovement.NONE;
                    }
                    break;
                case Input.Keys.UP:
                    if (verticalMovement == VerticalMovement.UP) {
                        verticalMovement = VerticalMovement.NONE;
                    }
                    break;
                case Input.Keys.LEFT:
                    if (horizontalMovement == HorizontalMovement.LEFT) {
                        horizontalMovement = HorizontalMovement.NONE;
                    }
                    break;
                case Input.Keys.RIGHT:
                    if (horizontalMovement == HorizontalMovement.RIGHT) {
                        horizontalMovement = HorizontalMovement.NONE;
                    }
                    break;
            }
            return true;
        }
    }
}

