package inkball;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerLineTest {

    PApplet p = new PApplet();

    /**
     * Test for adding points to the line.
     */
    @Test
    public void testAddPoint() {
        ArrayList<Ball> balls = new ArrayList<>();
        balls.add(new Ball(p,52, 116, null, 0));
        PlayerLine line = new PlayerLine();

        // Add invalid points
        line.addPoint(32, 10, balls);
        line.addPoint(64, 128, balls);
        assertTrue(line.points.isEmpty());
        assertTrue(line.getHitBoxes().isEmpty());

        // Add valid points
        line.addPoint(200, 96, balls);
        line.addPoint(220, 176, balls);
        line.addPoint(160, 156, balls);
        line.addPoint(185, 186, balls);
        line.addPoint(194, 190, balls);
        assertEquals(5, line.points.size());
        assertEquals(1, line.getHitBoxes().size());
    }

    /**
     * Test intersecting.
     */
    @Test
    public void testIntersect() {
        ArrayList<Ball> balls = new ArrayList<>();
        PlayerLine line = new PlayerLine();
        line.addPoint(200, 96, balls);
        line.addPoint(220, 176, balls);
        line.addPoint(160, 156, balls);
        line.addPoint(185, 186, balls);
        line.addPoint(194, 190, balls);

        assertFalse(line.isPointOnLine(32, 64));
        assertTrue(line.isPointOnLine(222, 175));

        assertTrue(line.isLineIntersectingCircle(32, 64, 32, 120, 34, 85, 12));
        assertFalse(line.isLineIntersectingCircle(32, 64, 32, 120, 52, 85, 12));
    }
}
