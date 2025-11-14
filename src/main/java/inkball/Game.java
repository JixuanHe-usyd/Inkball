package inkball;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;


/**
 * The {@code Game} class manages the overall game logic, including the current
 * level, the game board, and resources such as images. It also interacts with
 * the {@code Level} class to handle level-specific operations.
 */
public class Game {
    public int boardWidth; // Width of the game board
    public int boardHeight; // Height of the game board
    public int cellsize; // Size of each cell in pixels
    public int topbar; // Height of the top bar areas
    public Tile[][] board; // 2D array representing the game board
    public PApplet p; // PApplet instance
    public Level currentLevel; // Current level
    public PImage[] ballImages; // Array of ball images
    public PImage[] entrypointImages; // Array of spawner images
    public PImage[] holeImages;  // Array of hole images
    public PImage[] inkBallSpriteSheetImages; // Array of ink ball images
    public PImage[] tileImages; // Array of tile images
    public PImage[] wallImages; // Array of wall images
    public PImage[] damagedWallImages; // Array of damaged wall images

    /**
     * Constructs a new {@code Game} object with the specified parameters.
     *
     * @param p                       the PApplet instance used for rendering
     * @param boardWidth              the width of the game board in tiles
     * @param boardHeight             the height of the game board in tiles
     * @param cellsize                the size of each tile in pixels
     * @param topbar                  the height of the top bar in pixels
     * @param ballImages              the array of ball images
     * @param entrypointImages        the array of entry point images
     * @param holeImages              the array of hole images
     * @param inkBallSpriteSheetImages the array of inkball sprite sheet images
     * @param tileImages              the array of tile images
     * @param wallImages              the array of wall images
     * @param damagedWallImages       the array of damaged wall images
     */
    public Game(PApplet p, int boardWidth, int boardHeight, int cellsize, int topbar, PImage[] ballImages, PImage[] entrypointImages,
                PImage[] holeImages,
                PImage[] inkBallSpriteSheetImages,
                PImage[] tileImages,
                PImage[] wallImages,
                PImage[] damagedWallImages) {
        this.p = p;
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.cellsize = cellsize;
        this.topbar = topbar;
        this.board = new Tile[boardWidth][boardHeight];
        this.currentLevel = new Level(p, boardWidth, boardHeight, cellsize, topbar, ballImages, entrypointImages,
                holeImages,
                inkBallSpriteSheetImages,
                tileImages,
                wallImages,
                damagedWallImages);
        this.ballImages = ballImages;
        this.entrypointImages = entrypointImages;
        this.holeImages = holeImages;
        this.inkBallSpriteSheetImages = inkBallSpriteSheetImages;
        this.tileImages = tileImages;
        this.wallImages = wallImages;
        this.damagedWallImages = damagedWallImages;
    }

    /**
     * Initializes and renders the current game board based on the provided parameters.
     *
     * <p>This method loads the level layout and draws the top bar containing
     * information such as the game timer and the balls in the top bar.</p>
     *
     * @param levelLayOut   the array representing the layout of the level
     * @param time          the total time allocated for the level
     * @param spawnInterval the interval at which new balls are spawned
     * @param topBarBalls   the list of balls currently in the top bar
     * @param isPaused      indicates whether the game is currently paused
     * @param pausedTime    the amount of time the game has been paused
     * @param elapsedTime   the total elapsed time since the level started
     */
    public void getBoard(String[] levelLayOut, int time, int spawnInterval, ArrayList<Ball> topBarBalls, boolean isPaused, float pausedTime, float elapsedTime) {
        currentLevel.getLevel(levelLayOut);
        currentLevel.drawTopBar(time, spawnInterval, topBarBalls, isPaused, pausedTime, elapsedTime);
    }
}
