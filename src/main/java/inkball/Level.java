package inkball;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

/**
 * The {@code Level} class manages the layout, logic, and behavior of each level in the Inkball game.
 * It handles ball spawning, tile movements, score updates, and rendering of game elements.
 */

public class Level {

    public int boardWidth; // Width of the board
    public int boardHeight; // Height of the board in tiles
    public int cellsize; // Size of each tile in pixels
    public int topbar; // Height of the top bar
    public Tile[][] board; // 2D array representing the board layout
    public PApplet p; // PApplet instance

    // Arrays of images for rendering different game elements
    public PImage[] ballImages;
    public PImage[] entrypointImages;
    public PImage[] holeImages;
    public PImage[] inkBallSpriteSheetImages;
    public PImage[] tileImages;
    public PImage[] wallImages;
    public PImage[] damagedWallImages;

    public boolean[][] occupiedByHole; // // Tracks whether a tile is occupied by a hole

    public ArrayList<Ball> balls; // List of active balls
    public ArrayList<Tile> tiles; // List of tiles on the board
    public ArrayList<Tile> spawner; // List of spawners
    public ArrayList<PlayerLine> playerLines; // List of player lines
    public boolean isLevelInitialized = false; // Whether the level is initialized
    public Random random = new Random(); // Random spawning direction
    public float spawnCounter = 0; // Counter for tracking spawn intervals
    public float score = 0; // Record the scores for current level

    public boolean isLevelComplete = false; // Whether the level completes
    public int yellowTileMoveCounter = 0; // Counter for yellow tile movement
    public int yellowTileTopLeftX, yellowTileTopLeftY, yellowTileTopLeftDirection; // // Movement state of top-left yellow tile
    public int yellowTileBottomRightX, yellowTileBottomRightY, yellowTileBottomRightDirection; // // Movement state of bottom-right yellow tile
    public int scoreAdditionCounter = 0; // Counter for score increment over time

    public float countdownTime; // Countdown timer for the level

    public boolean islevelFailed = false; // Whether the level fails

    public ArrayList<Ball> initialisedBalls; // List of initialized balls


    /**
     * Constructs a new {@code Level} object with the given parameters.
     *
     * @param p                       the PApplet instance used for rendering
     * @param boardWidth              the width of the board in tiles
     * @param boardHeight             the height of the board in tiles
     * @param cellsize                the size of each tile in pixels
     * @param topbar                  the height of the top bar
     * @param ballImages              the array of ball images
     * @param entrypointImages        the array of entry point images
     * @param holeImages              the array of hole images
     * @param inkBallSpriteSheetImages the array of sprite sheet images
     * @param tileImages              the array of tile images
     * @param wallImages              the array of wall images
     * @param damagedWallImages       the array of damaged wall images
     */
    public Level(PApplet p, int boardWidth, int boardHeight, int cellsize, int topbar, PImage[] ballImages, PImage[] entrypointImages,
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
        this.ballImages = ballImages;
        this.entrypointImages = entrypointImages;
        this.holeImages = holeImages;
        this.inkBallSpriteSheetImages = inkBallSpriteSheetImages;
        this.tileImages = tileImages;
        this.wallImages = wallImages;
        this.damagedWallImages = damagedWallImages;
        this.occupiedByHole = new boolean[boardWidth][boardHeight];
        this.balls = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.playerLines = new ArrayList<>();
        this.spawner = new ArrayList<>();
        this.initialisedBalls = new ArrayList<>();

        yellowTileTopLeftX = 0;
        yellowTileTopLeftY = 0;
        yellowTileTopLeftDirection = 0;

        yellowTileBottomRightX = this.boardWidth - 1;
        yellowTileBottomRightY = this.boardHeight - 1;
        yellowTileBottomRightDirection = 2;
    }

    /**
     * Loads the layout for the current level based on the provided layout data.
     *
     * @param levelLayOut the layout of the level as an array of strings
     */
    public void getLevel(String[] levelLayOut) {
        if (isLevelInitialized) {
            return;
        }
        for (int rowNum = 0; rowNum < levelLayOut.length; rowNum++) {
            for (int colNum = 0; colNum < levelLayOut[rowNum].length(); colNum++) {
                if (occupiedByHole[rowNum][colNum]) {
                    continue;
                }
                char cell = levelLayOut[rowNum].charAt(colNum);
                int x = colNum * cellsize;
                int y = rowNum * cellsize + topbar;

                switch (cell) {
                    case 'X':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.WALL, wallImages[0], 0));
                        break;

                    case '1':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.WALL, wallImages[1], 1));
                        break;

                    case '2':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.WALL, wallImages[2], 2));
                        break;

                    case '3':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.WALL, wallImages[3], 3));
                        break;

                    case '4':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.WALL, wallImages[4], 4));
                        break;

                    case 'B':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.TILE, tileImages[0], 0));
                        tiles.add(new Tile(x + cellsize, y, cellsize, Tile.TileType.TILE, tileImages[0], 0));
                        int ballIndex = Character.getNumericValue(levelLayOut[rowNum].charAt(colNum + 1));
                        Ball newBall = new Ball(p, x, y, ballImages, ballIndex);
                        balls.add(newBall);
                        newBall.checkCollisions(tiles, playerLines, ballImages, tileImages);
                        colNum++;
                        break;

                    case 'H':
                        if (colNum + 1 < levelLayOut[rowNum].length()) {
                            int holeIndex = Character.getNumericValue(levelLayOut[rowNum].charAt(colNum + 1));
                            tiles.add(new Tile(x, y, cellsize * 2, Tile.TileType.HOLE, holeImages[holeIndex], holeIndex));
                            occupiedByHole[rowNum][colNum + 1] = true;
                            occupiedByHole[rowNum + 1][colNum] = true;
                            occupiedByHole[rowNum + 1][colNum + 1] = true;
                            colNum++;
                        }
                        break;

                    case 'S':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.ENTRYPOINT, entrypointImages[0], 0));
                        spawner.add(new Tile(x, y, cellsize, Tile.TileType.ENTRYPOINT, entrypointImages[0], 0));
                        break;

                    case ' ':
                        tiles.add(new Tile(x, y, cellsize, Tile.TileType.TILE, tileImages[0], 0));
                        break;
                }
            }
        }
        isLevelInitialized = true;
    }

    /**
     * Draws the top bar with game information such as score, time, and balls.
     *
     * @param time          the total time allocated for the level
     * @param spawnInterval the interval between spawning balls
     * @param topBarBalls   the list of balls in the top bar
     * @param isPaused      whether the game is currently paused
     * @param pausedTime    the total paused time
     * @param elapsedTime   the total elapsed time since the level started
     */
    public void drawTopBar(int time, int spawnInterval, ArrayList<Ball> topBarBalls, boolean isPaused, float pausedTime, float elapsedTime) {
        p.fill(220);
        p.noStroke();
        p.rect(0, 0, App.WIDTH, App.TOPBAR);

        p.fill(0);
        p.rect(15, 15, 160, 32);

        if (islevelFailed) {
            p.fill(0);
            p.textSize(15);
            p.text("=== TIME'S UP ===", 240, 50);
            p.fill(0);
            p.textSize(24);
            p.text("Score: " + (int)App.totalScore, App.WIDTH - 150, 30);

            countdownTime = time - (int) elapsedTime;
            if (countdownTime < 0) {
                countdownTime = 0;
            }
            p.text("Time:  " + (int)countdownTime, App.WIDTH - 150, 50);

            if (isPaused) {
                p.fill(0);
                p.textSize(15);
                p.text("*** PAUSED ***", (float) App.WIDTH / 2 - 25, 30);
            }

            float spawnCountdown;
            if (topBarBalls.isEmpty()) {
                spawnCountdown = 0.0f;
            } else {
                spawnCountdown = spawnInterval - (elapsedTime % spawnInterval);
                if (spawnCountdown < 0) {
                    spawnCountdown = 0.0f;
                }
            }
            p.textSize(24);
            p.text(String.format("%.1f", spawnCountdown), 190, 50);
            return;
        }

        float launchPositionX = 20;

        boolean firstBallLaunched = !topBarBalls.isEmpty() && topBarBalls.get(0).isLaunched;

        if (!topBarBalls.isEmpty() && !topBarBalls.get(0).isLaunched) {
            topBarBalls.get(0).isLaunched = true;
        }

        int maxBalls = Math.min(topBarBalls.size(), 5);


        for (int i = 0; i < maxBalls; i++) {
            Ball ball = topBarBalls.get(i);

            if (firstBallLaunched) {
                if (i == 0) {
                    if ((ball.x >= launchPositionX)) {
                        ball.x -= 1;
                    }
                } else {
                    float previousBallPosition = topBarBalls.get(i - 1).x + topBarBalls.get(i - 1).diameter + 8;
                    if ((ball.x > previousBallPosition)) {
                        ball.x -= 1;
                    }
                }
            }

            p.image(ballImages[ball.colorIndex], ball.x, ball.y, ball.diameter, ball.diameter);
            p.fill(220);
            p.rect(175, 15, App.WIDTH-175, App.TOPBAR - 30);

        }

        p.fill(0);
        p.textSize(24);
        p.text("Score: " + (int)App.totalScore, App.WIDTH - 150, 30);

        countdownTime = time - (int) elapsedTime;
        if (countdownTime < 0) {
            countdownTime = 0;
        }
        p.text("Time:  " + (int)countdownTime, App.WIDTH - 150, 50);

        if (isPaused) {
            p.fill(0);
            p.textSize(15);
            p.text("*** PAUSED ***", (float) App.WIDTH / 2 - 25, 30);
        }

        float spawnCountdown;
        if (topBarBalls.isEmpty()) {
            spawnCountdown = 0.0f;
        } else {
            spawnCountdown = spawnInterval - (elapsedTime % spawnInterval);
            if (spawnCountdown < 0) {
                spawnCountdown = 0.0f;
            }
        }
        p.textSize(24);
        p.text(String.format("%.1f", spawnCountdown), 190, 50);

    }

    /**
     * Spawns a ball from the top bar at one of the entry points on the board.
     *
     * @param topBarBalls   the list of balls in the top bar
     * @param spawnInterval the interval between spawning balls
     * @param elapsedTime   the time passed
     */
    public void spawnBall(ArrayList<Ball> topBarBalls, int spawnInterval, float elapsedTime) {
        if (islevelFailed) {
            return;
        }

        if (elapsedTime - spawnCounter >= spawnInterval) {
            spawnCounter = elapsedTime;
            if (!spawner.isEmpty() && !topBarBalls.isEmpty()) {
                int randomIndex = random.nextInt(spawner.size());
                Tile selectedSpawner = spawner.get(randomIndex);

                Ball ballToSpawn = topBarBalls.remove(0);
                ballToSpawn.x = selectedSpawner.x + (float) cellsize / 2 - ballToSpawn.diameter / 2;
                ballToSpawn.y = selectedSpawner.y + (float) cellsize / 2 - ballToSpawn.diameter / 2;
                balls.add(ballToSpawn);
                ballToSpawn.isLaunched = true;
            }
        }
    }

    /**
     * Updates the state of all active balls and checks for collisions with tiles.
     *
     * @param scoreIncrease           the score increase map for correct captures
     * @param scoreDecrease           the score decrease map for incorrect captures
     * @param scoreIncreasedMultiplier the multiplier for score increases
     * @param scoreDecreasedMultiplier the multiplier for score decreases
     * @param realTopBarBalls         the list of balls in the top bar
     * @param spawnInterval           the interval between spawning balls
     * @param elapsedTime             the time passed
     */
    public void updateBalls(HashMap<String, Integer> scoreIncrease, HashMap<String, Integer> scoreDecrease, float scoreIncreasedMultiplier, float scoreDecreasedMultiplier, ArrayList<Ball> realTopBarBalls, int spawnInterval, float elapsedTime) {
        ArrayList<Ball> copyBalls = new ArrayList<>(balls);
        ArrayList<Tile> copyTiles = new ArrayList<>(tiles);

        if (countdownTime <= 0) {
            if (!balls.isEmpty() || !realTopBarBalls.isEmpty()) {
                islevelFailed = true;
                return;
            }
        }

        if (balls.isEmpty() && realTopBarBalls.isEmpty()) {
            isLevelComplete = true;
        }


        if (isLevelComplete) {
            incrementScoreWithTime();
            updateYellowTileMovement();
        }


        for (Ball ball : copyBalls) {
            ball.update(tiles, playerLines, balls, ballImages, tileImages);

            for (Tile tile : copyTiles) {
                if (ball.isCaptured && tile.type == Tile.TileType.HOLE) {
                    if (ball.colorIndex == ball.attractedHole.colorIndex || ball.colorIndex == 0 || ball.attractedHole.colorIndex == 0) {
                        score += scoreIncrease.get(ball.getColorName()) * scoreIncreasedMultiplier;
                        App.totalScore += scoreIncrease.get(ball.getColorName()) * scoreIncreasedMultiplier;
                    } else {
                        score -= scoreDecrease.get(ball.getColorName()) * scoreDecreasedMultiplier;
                        App.totalScore -= scoreDecrease.get(ball.getColorName()) * scoreDecreasedMultiplier;
                        ball.reset(realTopBarBalls);
                        realTopBarBalls.add(ball);
                    }
                    ball.isCaptured = false;
                    spawnBall(realTopBarBalls, spawnInterval, elapsedTime);
                }
            }
        }
    }

    /**
     * Moves the yellow tiles at the top-left and bottom-right corners of the board.
     */
    public void updateYellowTileMovement() {
        int yellowTileMoveInterval = (int) (0.067f * App.FPS);
        yellowTileMoveCounter++;
        if (yellowTileMoveCounter >= yellowTileMoveInterval) {
            yellowTileMoveCounter = 0;

            moveTopLeftYellowTile();
            moveBottomRightYellowTile();
        }
    }

    /**
     * Moves the yellow tile at the top-left corners of the board.
     */
    public void moveTopLeftYellowTile() {
        switch (yellowTileTopLeftDirection) {
            case 0:
                yellowTileTopLeftX++;
                if (yellowTileTopLeftX >= boardWidth - 1) yellowTileTopLeftDirection = 1;
                break;
            case 1:
                yellowTileTopLeftY++;
                if (yellowTileTopLeftY >= boardHeight - 3) yellowTileTopLeftDirection = 2;
                break;
            case 2:
                yellowTileTopLeftX--;
                if (yellowTileTopLeftX <= 0) yellowTileTopLeftDirection = 3;
                break;
            case 3:
                yellowTileTopLeftY--;
                if (yellowTileTopLeftY <= 0) yellowTileTopLeftDirection = 0;
                break;
        }
    }

    /**
     * Moves the yellow tile at the bottom-right corners of the board.
     */
    public void moveBottomRightYellowTile() {
        switch (yellowTileBottomRightDirection) {
            case 0:
                yellowTileBottomRightX++;
                if (yellowTileBottomRightX >= boardWidth - 1) yellowTileBottomRightDirection = 1;
                break;
            case 1:
                yellowTileBottomRightY++;
                if (yellowTileBottomRightY >= boardHeight - 1) yellowTileBottomRightDirection = 2;
                break;
            case 2:
                yellowTileBottomRightX--;
                if (yellowTileBottomRightX <= 0) yellowTileBottomRightDirection = 3;
                break;
            case 3:
                yellowTileBottomRightY--;
                if (yellowTileBottomRightY <= 2) yellowTileBottomRightDirection = 0;
                break;
        }
    }

    /**
     * Increase the score when level completed but time left
     */
    public void incrementScoreWithTime() {
        if (scoreAdditionCounter++ >= 2 && countdownTime > 0) {
            App.totalScore += 1;
            score += 1;
            scoreAdditionCounter = 0;
        }

    }

    /**
     * Displays all the active balls on the board.
     */
    public void displayBalls() {
        for (Ball ball : balls) {
            p.image(ballImages[ball.colorIndex], ball.x, ball.y, ball.diameter, ball.diameter);
        }
    }

    /**
     * Displays all the tiles on the board.
     */
    public void displayTiles() {
        for (Tile tile : tiles) {
            tile.display(p, damagedWallImages, tileImages[0]);
        }

        if (isLevelComplete) {
            p.image(wallImages[4], yellowTileTopLeftX * cellsize, yellowTileTopLeftY * cellsize + topbar, cellsize, cellsize);
            p.image(wallImages[4], yellowTileBottomRightX * cellsize, yellowTileBottomRightY * cellsize, cellsize, cellsize);
        }
    }

    /**
     * Checks if the current level is completed.
     *
     * @param topBarBalls the list of balls in the top bar
     * @return {@code true} if the level is complete, otherwise {@code false}
     */
    public boolean isLevelCompleted(ArrayList<Ball> topBarBalls) {
        return balls.isEmpty() && topBarBalls.isEmpty();
    }
}

