package inkball;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

public class BallTest {
    PApplet p = new PApplet();

    /**
     * Basic test for ball class.
     */
    @Test
    public void basicTest() {
        Ball ball = new Ball(p, 64, 128, new PImage[]{null, null}, 0);
        assertEquals(2, Math.abs(ball.i), 1e-6);
        assertEquals(2, Math.abs(ball.j), 1e-6);
        assertEquals(64, ball.x, 1e-6);
        assertEquals(128, ball.y, 1e-6);
        assertEquals(0, ball.colorIndex);

        Tile tile = new Tile(88, 152, App.CELLSIZE, Tile.TileType.WALL, null, 1);
        ball.changeColor(1, tile, new PImage[]{null, null});
        assertEquals(1, ball.colorIndex);

        float mx = ball.x + ball.i;
        float my = ball.y + ball.j;
        ball.update(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new PImage[]{}, new PImage[]{null, null});
        assertEquals(mx, ball.x, 1e-6);
        assertEquals(my, ball.y, 1e-6);
    }

    /**
     * Test for checking collisions whether happens or not.
     */
    @Test
    public void testCheckCollisions() {
        App.random.setSeed(0);
        Ball ball = new Ball(p, 565, 629, new PImage[]{}, 0);
        float vx = ball.i;
        float vy = ball.j;
        ball.update(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new PImage[]{}, new PImage[]{});
        assertEquals(-vx, ball.i, 1e-6);
        assertEquals(-vy, ball.j, 1e-6);
        vx = ball.i;
        vy = ball.j;

        // Collides with walls
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(562, 625, App.CELLSIZE, Tile.TileType.WALL, null, 1));
        ball.update(tiles, new ArrayList<>(), new ArrayList<>(), new PImage[]{}, new PImage[]{});
        assertTrue(vx != ball.i || vy != ball.j);
        vx = ball.i;
        vy = ball.j;

        // Collides with player-drawn lines
        ArrayList<PlayerLine> playerLines = new ArrayList<>();
        PlayerLine line = new PlayerLine();
        line.addPoint(550, 600, new ArrayList<>());
        line.addPoint(552, 603, new ArrayList<>());
        line.addPoint(560, 610, new ArrayList<>());
        line.addPoint(562, 624, new ArrayList<>());
        line.addPoint(562, 630, new ArrayList<>());
        line.addPoint(550, 640, new ArrayList<>());
        playerLines.add(line);
        ball.update(new ArrayList<>(), playerLines, new ArrayList<>(), new PImage[]{}, new PImage[]{});
        assertTrue(vx != ball.i || vy != ball.j);
    }

    /**
     * Test for checking attraction.
     */
    @Test
    public void testCheckHoleAttraction() {
        App.random.setSeed(0);
        Ball ball = new Ball(p, 120, 120, new PImage[]{}, 0);
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(128, 128, 64, Tile.TileType.HOLE, null, 0));
        assertFalse(ball.isCaptured);
        for (int i = 0; i < 7000; i++) {
            ball.update(tiles, new ArrayList<>(), new ArrayList<>(), new PImage[]{}, new PImage[]{});
        }
        assertTrue(ball.isCaptured);
    }

    /**
     * Test for get color name from color index.
     */
    @Test
    public void testGetColorName() {
        Ball ball1 = new Ball(p, 0, 0, new PImage[]{}, 0);
        assertEquals("grey", ball1.getColorName());
        Ball ball2 = new Ball(p, 0, 0, new PImage[]{}, 1);
        assertEquals("orange", ball2.getColorName());
        Ball ball3 = new Ball(p, 0, 0, new PImage[]{}, 2);
        assertEquals("blue", ball3.getColorName());
        Ball ball4 = new Ball(p, 0, 0, new PImage[]{}, 3);
        assertEquals("green", ball4.getColorName());
        Ball ball5 = new Ball(p, 0, 0, new PImage[]{}, 4);
        assertEquals("yellow", ball5.getColorName());
        Ball ball6 = new Ball(p, 0, 0, new PImage[]{}, 5);
        assertEquals("unknown", ball6.getColorName());
    }

    /**
     * Test for reset the ball parameters.
     */
    @Test
    public void testReset() {
        Ball ball = new Ball(p, 0, 0, new PImage[]{}, 0);
        ball.reset(new ArrayList<>());
        assertEquals(15, ball.x);
        assertEquals(20, ball.y);
        assertEquals(24, ball.diameter);
        assertEquals(2, Math.abs(ball.i), 1e-6);
        assertEquals(2, Math.abs(ball.j), 1e-6);
        assertFalse(ball.isAttracted);
        assertFalse(ball.isCaptured);
        assertNull(ball.attractedHole);
    }

}

