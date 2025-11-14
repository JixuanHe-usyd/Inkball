package inkball;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;


/**
 * The {@code Tile} class represents a tile on the game board.
 * A tile can be of different types such as walls, holes, or entry points,
 * and it manages its hitboxes, visual representation, and behavior when hit by balls.
 */

public class Tile {

    public int x; // The x-coordinate of the tile
    public int y; // The y-coordinate of the tile
    public int size; // The size of the tile (width and height in pixels)
    public TileType type; // The type of the tile (WALL, TILE, HOLE, or ENTRYPOINT)
    public PImage image; // The image used to draw the tile
    public int colorIndex; // The color index of the tile
    public ArrayList<HitBox> hitBoxes; // List of hit boxes associated with the tile
    public int hitCount = 0; // Number of times the tile has been hit
    public int MAX_HIT_COUNT = 3; // Maximum number of hits before the tile is destroyed


    /**
     * Enum representing the different types of tiles.
     */
    public enum TileType {
        WALL,
        TILE,
        HOLE,
        ENTRYPOINT;
    }

    /**
     * Constructs a new {@code Tile} with the specified properties.
     *
     * @param x          the x-coordinate of the tile
     * @param y          the y-coordinate of the tile
     * @param size       the size of the tile (width and height in pixels)
     * @param type       the type of the tile (e.g., WALL, TILE)
     * @param image      the image used to render the tile
     * @param colorIndex the color index of the tile (used for walls)
     */
    public Tile(int x, int y, int size, TileType type, PImage image, int colorIndex) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
        this.image = image;
        this.colorIndex = colorIndex;
        this.hitBoxes = new ArrayList<>();
        if (this.type == TileType.WALL) {
            createHitBoxes();
        }
    }

    /**
     * Displays the tile on the screen using the specified PApplet instance.
     * If the tile is a wall, it changes its appearance based on the number of hits it has taken.
     *
     * @param p                the PApplet instance used for rendering
     * @param damagedWallImages an array of images representing damaged walls
     * @param blankTile         the image to use when the tile is destroyed
     */
    public void display(PApplet p, PImage[] damagedWallImages, PImage blankTile) {

        if (type == TileType.WALL) {
            if (hitCount == 0) {
                p.image(image, x, y, size, size);
            } else if (hitCount == 1) {
                p.image(damagedWallImages[colorIndex], x, y, size, size);
            } else if (hitCount == 2) {
                p.image(damagedWallImages[colorIndex], x, y, size, size);
            }
        } else {
            p.image(image, x, y, size, size);
        }
    }

    /**
     * Handles the behavior when the tile is hit by a ball.
     * If the ball's color matches the tile's color or the tile's color is neutral, the hit count is incremented.
     * When the hit count reaches the maximum, the tile is converted to a blank tile.
     *
     * @param blankTile     the image to use when the tile is destroyed
     * @param ballColorIndex the color index of the ball that hit the tile
     */
    public void hit (PImage blankTile, int ballColorIndex) {

        if (this.colorIndex == ballColorIndex || this.colorIndex == 0) {
            hitCount++;
        }
        if (hitCount == 3) {
            this.type = TileType.TILE;
            this.image = blankTile;
        }
    }


    /**
     * Creates the hitboxes for the tile. A hitbox is created for each edge of the tile.
     */
    public void createHitBoxes() {
        hitBoxes.add(new HitBox(x, y, x + size, y));
        hitBoxes.add(new HitBox(x, y + size, x + size, y + size));
        hitBoxes.add(new HitBox(x + size, y, x + size, y + size));
        hitBoxes.add(new HitBox(x, y, x, y + size));
    }

    /**
     * Returns the list of hitboxes associated with this tile.
     *
     * @return the list of hitboxes
     */
    public ArrayList<HitBox> getHitBoxes() {
        return hitBoxes;
    }
}
