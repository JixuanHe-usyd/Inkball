package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.util.*;

/**
 * The {@code App} class is the main entry point for the Inkball game.
 * It extends the {@code PApplet} class to manage the game window,
 * handle input events, load resources, and control the game loop.
 */

public class App extends PApplet {

    // Game  constants
    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576;
    public static int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;
    public static final int FPS = 30;

    // Game state variables
    public String configPath;
    public static Random random = new Random();
    public Game game;

    // Images for game objects
    public PImage ball0;
    public PImage ball1;
    public PImage ball2;
    public PImage ball3;
    public PImage ball4;

    public PImage entrypoint;

    public PImage hole0;
    public PImage hole1;
    public PImage hole2;
    public PImage hole3;
    public PImage hole4;

    public PImage inkballspritesheet;

    public PImage tile;

    public PImage wall0;
    public PImage wall1;
    public PImage wall2;
    public PImage wall3;
    public PImage wall4;

    public PImage damagedWall0;
    public PImage damagedWall1;
    public PImage damagedWall2;
    public PImage damagedWall3;
    public PImage damagedWall4;

    // Image arrays
    public PImage[] ballImages;
    public PImage[] entrypointImages;
    public PImage[] holeImages;
    public PImage[] inkBallSpriteSheetImages;
    public PImage[] tileImages;
    public PImage[] wallImages;
    public PImage[] damagedWallImages;
    public GetConfig config;

    //  Game state flags
    public boolean isPaused = false;
    public int lastPauseFrame = 0;
    public float pausedTime = 0;
    public float elapsedTime = 0;
    public int levelIndex = 0;
    public boolean levelCompleted = false;
    public boolean levelFailed = false;

    // Score tracking
    public static float totalScore = 0;
    public float previousLevelScore = 0;
    public ArrayList<Ball> previousBalls;

    /**
     * Constructs the {@code App} class and sets the path to the configuration file.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        String path = "src/main/resources/inkball/";

        ball0 = loadImage(path + "ball0.png");
        ball1 = loadImage(path + "ball1.png");
        ball2 = loadImage(path + "ball2.png");
        ball3 = loadImage(path + "ball3.png");
        ball4 = loadImage(path + "ball4.png");

        entrypoint = loadImage(path + "entrypoint.png");

        hole0 = loadImage(path + "hole0.png");
        hole1 = loadImage(path + "hole1.png");
        hole2 = loadImage(path + "hole2.png");
        hole3 = loadImage(path + "hole3.png");
        hole4 = loadImage(path + "hole4.png");

        inkballspritesheet = loadImage(path + "inkball_spritesheet.png");

        tile = loadImage(path + "tile.png");

        wall0 = loadImage(path + "wall0.png");
        wall1 = loadImage(path + "wall1.png");
        wall2 = loadImage(path + "wall2.png");
        wall3 = loadImage(path + "wall3.png");
        wall4 = loadImage(path + "wall4.png");

        damagedWall0 = loadImage(path + "damagedwall0.png");
        damagedWall1 = loadImage(path + "damagedwall1.png");
        damagedWall2 = loadImage(path + "damagedwall2.png");
        damagedWall3 = loadImage(path + "damagedwall3.png");
        damagedWall4 = loadImage(path + "damagedwall4.png");

        ballImages = new PImage[]{ball0, ball1, ball2, ball3, ball4};
        entrypointImages = new PImage[]{entrypoint};
        holeImages = new PImage[]{hole0, hole1, hole2, hole3, hole4};
        inkBallSpriteSheetImages = new PImage[]{inkballspritesheet};
        tileImages = new PImage[]{tile};
        wallImages = new PImage[]{wall0, wall1, wall2, wall3, wall4};
        damagedWallImages = new PImage[]{damagedWall0, damagedWall1, damagedWall2, damagedWall3, damagedWall4};

        config = new GetConfig(configPath, this);


        game = new Game(this, BOARD_WIDTH, BOARD_HEIGHT,CELLSIZE, TOPBAR, ballImages, entrypointImages,
                holeImages,
                inkBallSpriteSheetImages,
                tileImages,
                wallImages,
                damagedWallImages);

        config.loadConfig(levelIndex);
        previousBalls = new ArrayList<>(config.realTopBarBalls);

    }

    /**
     * Receive key pressed signal from the keyboard to pause the game or restart the game or level.
     */
    @Override
    public void keyPressed(KeyEvent event){
        if ((key == ' ' || keyCode == 32) && !levelFailed) {
            isPaused = !isPaused;

            if (isPaused) {
                lastPauseFrame = frameCount;
            } else {
                pausedTime += (frameCount - lastPauseFrame) / (float) FPS;
            }
        }

        if (key == 'r' || key == 'R') {
            if ( levelIndex >= 2 && levelCompleted) {
                restartGame();
            } else {
                restartLevel();
            }
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased(){

    }

    /**
     * Receive mouse press signals from the keyboard to draw lines or remove lines.
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if (game.currentLevel.islevelFailed || levelFailed) {
            return;
        }

        if (mouseButton == LEFT) {
            PlayerLine newLine = new PlayerLine();
            newLine.addPoint(mouseX, mouseY, game.currentLevel.balls);
            game.currentLevel.playerLines.add(newLine);
        } else if ((mouseButton == RIGHT) || (mouseButton == LEFT && (keyPressed && (keyCode == CONTROL)))) {
            Iterator<PlayerLine> lineIterator = game.currentLevel.playerLines.iterator();
            while (lineIterator.hasNext()) {
                PlayerLine line = lineIterator.next();
                if (line.isPointOnLine(mouseX, mouseY)) {
                    lineIterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Receive mouse drag signals from the keyboard to draw lines.
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (game.currentLevel.islevelFailed || levelFailed) {
            return;
        }

        if (mouseButton == LEFT && !game.currentLevel.playerLines.isEmpty()) {
            PlayerLine currentLine = game.currentLevel.playerLines.get(game.currentLevel.playerLines.size() - 1);
            currentLine.addPoint(mouseX, mouseY, game.currentLevel.balls);
        }
    }

    /**
     * Receive mouse release signals.
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(200);
        game.getBoard(config.layoutLevelFile, config.time, config.spawnInterval, config.realTopBarBalls, isPaused, pausedTime, elapsedTime);

        if (levelCompleted && levelIndex >= 2) {
            fill(0);
            textSize(15);
            text("=== ENDED ===", 240, 50);
        }

        if (!isPaused && !game.currentLevel.islevelFailed) {
            if (game.currentLevel.isLevelComplete) {
                elapsedTime += (float) (15.0 / FPS);
            }
            elapsedTime += (float) (1.0 / FPS);
        }

        game.currentLevel.displayTiles();

        if (game.currentLevel.islevelFailed && !game.currentLevel.isLevelCompleted(config.realTopBarBalls)) {
            levelFailed = true;
        }

        if (game.currentLevel.isLevelCompleted(config.realTopBarBalls) && game.currentLevel.countdownTime <= 0.1) {
            levelCompleted = true;
            goToNextLevel();
        }

        if (!isPaused && !levelCompleted && !levelFailed && !game.currentLevel.islevelFailed) {
            game.currentLevel.updateBalls(config.scoreIncreaseFromHoleCapture, config.scoreDecreaseFromWrongHole, config.scoreIncreaseModifier, config.scoreDecreaseModifier, config.realTopBarBalls, config.spawnInterval, elapsedTime);
            game.currentLevel.spawnBall(config.realTopBarBalls, config.spawnInterval, elapsedTime);
        }
        game.currentLevel.displayBalls();

        if (game.currentLevel.isLevelComplete && game.currentLevel.countdownTime == 0) {
            levelCompleted = true;
            goToNextLevel();
        }

        if (game.currentLevel.islevelFailed) {
            levelFailed = true;
        }



        Iterator<PlayerLine> lineIterator = game.currentLevel.playerLines.iterator();
        while (lineIterator.hasNext()) {
            PlayerLine line = lineIterator.next();

            for (Ball ball : game.currentLevel.balls) {
                if (ball.isColliding) {
                    ball.isColliding = false;
                }
            }
            line.display(this);
        }

        if (levelFailed && levelIndex > 0) {
            fill(0);
            textSize(15);
            text("=== TIME'S UP ===", 240, 50);
        }
    }

    /**
     * Initializes the next level.
     */
    public void goToNextLevel() {
        if (levelIndex < 2) {
            previousLevelScore = totalScore;
            levelIndex++;
            initializeLevel();
        } else {
            levelCompleted = true;
        }
    }

    /**
     * Restarts the current level.
     */
    public void restartLevel() {
        if (levelIndex == 0) {
            totalScore = 0;
        } else {
            totalScore = previousLevelScore;
        }

        initializeLevel();
    }

    /**
     * Restarts the entire game from the first level.
     */
    public void restartGame() {
        levelIndex = 0;
        totalScore = 0;
        initializeLevel();
    }

    /**
     * Initializes the level with default values.
     */
    public void initializeLevel() {
        isPaused = false;
        pausedTime = 0;
        elapsedTime = 0;
        lastPauseFrame = 0;
        levelFailed = false;
        levelCompleted = false;

        config = new GetConfig(configPath, this);
        config.loadConfig(levelIndex);
        game = new Game(this, BOARD_WIDTH, BOARD_HEIGHT, CELLSIZE, TOPBAR, ballImages, entrypointImages,
                holeImages, inkBallSpriteSheetImages, tileImages, wallImages, damagedWallImages);

        game.getBoard(config.layoutLevelFile, config.time, config.spawnInterval, config.realTopBarBalls, isPaused, pausedTime, elapsedTime);

    }


    /**
     * Main entry point for the Inkball game.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
