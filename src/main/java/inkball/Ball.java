package inkball;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

/**
 * The {@code Ball} class represents a ball object in the Inkball game.
 * It manages the ball's properties, movement, collisions, and interactions
 * with tiles, lines, and holes.
 */


public class Ball {
    public PApplet p; // PApplet instance
    public float x, y; // Position of the ball
    public float i, j; // speed vectors
    public float diameter = 24; // Diameter of the ball
    public PImage[] ballImages; // Array of ball images
    public int colorIndex; // color index of the ball
    public boolean isCaptured; // whether the ball has been captured
    public boolean isAttracted; // whether the ball has been attracted
    public int topbar = 64; // height of the top bar areas
    public boolean isColliding = false; // Collision status
    public boolean isLaunched = false; // whether the ball has been launched
    public Tile attractedHole = null; // the specified tile which attracts the ball
    public boolean prevCollideState; // Previous collision state

    /**
     * Constructs a new {@code Ball} object with the specified parameters.
     *
     * @param p          the PApplet instance used for rendering
     * @param x          the initial x-coordinate of the ball
     * @param y          the initial y-coordinate of the ball
     * @param ballImages the array of ball images representing different colors
     * @param colorIndex the initial color index of the ball
     */
    public Ball(PApplet p, float x, float y, PImage[] ballImages, int colorIndex) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.ballImages = ballImages;
        this.colorIndex = colorIndex;
        this.isCaptured = false;
        this.isAttracted = false;
        prevCollideState = false;
        setRandomDirection();
    }

    /**
     * Updates the ball's position and checks for collisions with tiles and player lines.
     *
     * @param tiles      the list of tiles on the board
     * @param playerLine the list of player-drawn lines
     * @param balls      the list of balls in the game
     * @param ballImages the array of ball images
     * @param tileImages the array of tile images
     */
    public void update(ArrayList<Tile> tiles, ArrayList<PlayerLine> playerLine, ArrayList<Ball> balls, PImage[] ballImages, PImage[] tileImages) {
        if (!isCaptured) {
            x += i;
            y += j;
            checkCollisions(tiles, playerLine, ballImages, tileImages);
            checkHoleAttraction(tiles, balls);
        }
    }

    /**
     * Sets a random initial direction for the ball.
     */
    public void setRandomDirection() {
        this.i = Math.random() < 0.5 ? 2 : -2;
        this.j = Math.random() < 0.5 ? 2 : -2;
    }

    /**
     * Checks for collisions between the ball and tiles or player lines.
     *
     * @param tiles      the list of tiles on the board
     * @param playerLines the list of player-drawn lines
     * @param ballImages the array of ball images
     * @param tileImages the array of tile images
     */
    public void checkCollisions(ArrayList<Tile> tiles, ArrayList<PlayerLine> playerLines, PImage[] ballImages, PImage[] tileImages) {
        if (x <= 0 || x >= p.width - diameter) {
            i *= -1;
        }

        if (y <= topbar || y >= p.height - diameter) {
            j *= -1;
        }

        for (Tile tile : tiles) {
            if (tile.type == Tile.TileType.WALL) {
                handleCollidingWithTile(tile, ballImages, tileImages);
            }
        }

        PlayerLine collidedLine = null;

        for (PlayerLine playerLine : playerLines) {
            int index = playerLines.indexOf(playerLine);
            if (handleCollidingWithLine(playerLine, index) == index) {
                collidedLine = playerLine;
                break;
            }
        }

        if (collidedLine != null) {
            playerLines.remove(collidedLine);
        }
    }

    /**
     * Handles collision between the ball and a tile.
     *
     * @param tile       the tile being collided with
     * @param ballImages the array of ball images
     * @param tileImages the array of tile images
     */
    public void handleCollidingWithTile(Tile tile, PImage[] ballImages, PImage[] tileImages) {
        for (HitBox hitBox : tile.getHitBoxes()) {
            if (hitBox.isColliding(x + 12, y + 12, diameter / 2, i, j)) {
                float[] newVelocity = hitBox.reflect(i, j, x, y);

                i = newVelocity[0];
                j = newVelocity[1];

                tile.hit(tileImages[0], this.colorIndex);
                changeColor(tile.colorIndex, tile, ballImages);
            }
        }
    }

    /**
     * Changes the color of the ball when it collides with a tile.
     *
     * @param newColorIndex the new color index of the ball
     * @param tile          the tile being collided with
     * @param ballImages    the array of ball images
     */
    public void changeColor(int newColorIndex, Tile tile, PImage[] ballImages) {
        if (newColorIndex > 0 && newColorIndex < ballImages.length && tile.type == Tile.TileType.WALL) {
            this.colorIndex = newColorIndex;
        }
    }

    /**
     * Handles collision between the ball and a player-drawn line.
     *
     * @param line  the player-drawn line
     * @param index the index of the line in the list
     * @return the index if a collision occurs, otherwise -1
     */
    public int handleCollidingWithLine(PlayerLine line, int index) {
        for (HitBox hitBox : line.getHitBoxes()) {
            if (hitBox.isColliding(x + 12, y + 12, diameter / 2, i, j)) {
                isColliding = true;
                float[] newVelocity = hitBox.reflect(i, j, x, y);
                i = newVelocity[0];
                j = newVelocity[1];
                return index;
            }
        }
        return -1;
    }

    /**
     * Checks if the ball is attracted to a hole and removes it if captured.
     *
     * @param holes the list of hole tiles
     * @param balls the list of balls in the game
     */
    public void checkHoleAttraction(ArrayList<Tile> holes, ArrayList<Ball> balls) {
        ArrayList<Ball> copyList = new ArrayList<>(balls);
        for (Tile hole : holes) {
            for (Ball ball : copyList) {
                if (hole.type == Tile.TileType.HOLE) {
                    float distanceToHoleCenter = PApplet.dist(ball.x + ball.diameter/2, ball.y + ball.diameter/2, hole.x + (float) hole.size /2, hole.y + (float) hole.size /2);
                    if (distanceToHoleCenter <= 32) {
                        ball.isAttracted = true;
                        Ball capturedBall = attractToHole(hole, ball);
                        ball.attractedHole = hole;
                        balls.remove(capturedBall);
                    }
                }
            }
        }
    }

    /**
     * Attracts the ball towards a hole.
     *
     * @param hole the hole attracting the ball
     * @param ball the ball being attracted
     * @return the captured ball if it reaches the hole, otherwise {@code null}
     */
    public Ball attractToHole(Tile hole, Ball ball) {
        float attractionForce = 0.1f;
        float dx = (hole.x + (float) hole.size /2) - (ball.x + ball.diameter/2);
        float dy = (hole.y + (float) hole.size /2) - (ball.y + ball.diameter/2);
        ball.i += dx * attractionForce;
        ball.j += dy * attractionForce;

        //ball.diameter -= 3f;

        float distanceFromHole = PApplet.dist((ball.x + ball.diameter / 2), (ball.y + ball.diameter / 2), hole.x + (float) hole.size /2, hole.y + (float) hole.size /2);

        ball.diameter -= 3f;

        if (ball.diameter >= 24) {
            return ball;
        }

        if (distanceFromHole <= 15) {
            ball.isCaptured = true;
            return ball;
        }

        return null;

    }

    /**
     * Returns the color name of the ball based on its color index.
     *
     * @return the name of the ball's color
     */
    public String getColorName() {
        switch (colorIndex) {
            case 0:
                return "grey";
            case 1:
                return "orange";
            case 2:
                return "blue";
            case 3:
                return "green";
            case 4:
                return "yellow";
            default:
                return "unknown";
        }
    }

    /**
     * Resets the state of the ball to its initial position and properties.
     *
     * <p>The ball's position is determined based on the number of balls currently
     * in the real top bar. The velocity, diameter, and attraction states are
     * also reset to their default values.</p>
     *
     * @param realTopBarBalls the list of balls currently in the top bar, used to
     *                        determine the new x-coordinate of this ball.
     */
    public void reset(ArrayList<Ball> realTopBarBalls) {
        this.x = 15 + realTopBarBalls.size() * 32;
        this.y = 20;
        this.diameter = 24;
        this.i = Math.random() < 0.5 ? 2 : -2;
        this.j = Math.random() < 0.5 ? 2 : -2;
        this.isCaptured = false;
        this.isAttracted = false;
        this.attractedHole = null;
    }

}


