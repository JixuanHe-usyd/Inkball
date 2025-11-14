package inkball;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
    private Tile tile;

    /**
     * Test for wall.
     */
    @Test
    public void testWall() {
        Tile wall = new Tile(32, 64, App.CELLSIZE, Tile.TileType.WALL, null, 0);
        assertEquals(4, wall.getHitBoxes().size());
        assertEquals(Tile.TileType.WALL, wall.type);
        assertEquals(32, wall.size);
        assertEquals(0, wall.colorIndex);
        assertEquals(32, wall.x);
        assertEquals(64, wall.y);
        assertEquals(0, wall.hitCount);
        assertEquals(3, wall.MAX_HIT_COUNT);
    }

    /**
     * Test for hole.
     */
    @Test
    public void testHole() {
        Tile hole = new Tile(32, 32, App.CELLSIZE * 2, Tile.TileType.HOLE, null, 1);
        assertEquals(0, hole.getHitBoxes().size());
        assertEquals(Tile.TileType.HOLE, hole.type);
        assertEquals(64, hole.size);
        assertEquals(1, hole.colorIndex);
        assertEquals(32, hole.x);
        assertEquals(32, hole.y);
    }

    /**
     * Test for hitting mechanism.
     */
    @Test
    public void testHit() {
        tile = new Tile(0, 0, 32, Tile.TileType.WALL, null, 1);
        assertEquals(0, tile.hitCount);
        tile.hit(null, 1);
        assertEquals(1, tile.hitCount);
        tile.hit(null, 1);
        assertEquals(2, tile.hitCount);
        tile.hit(null, 2);
        assertEquals(2, tile.hitCount);
        tile.hit(null, 1);
        assertEquals(3, tile.hitCount);
        assertSame(tile.type, Tile.TileType.TILE);
    }

    /**
     * Test for ball hitting wall when the color index of wall is zero.
     */
    @Test
    public void testForZeroColorIndex() {
        tile = new Tile(0, 0, 32, Tile.TileType.WALL, null, 1);
        tile.colorIndex = 0;
        tile.hit(null, 1);
        assertEquals(1, tile.hitCount);
        tile.hit(null, 2);
        assertEquals(2, tile.hitCount);
        tile.hit(null, 3);
        assertEquals(3, tile.hitCount);
    }
}
