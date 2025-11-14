package inkball;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HitBoxTest {

    /**
     * Test for colliding or not.
     */
    @Test
    public void testCollision() {
        HitBox hitBox = new HitBox(64, 192, 128, 192);
        assertTrue(hitBox.isColliding(92, 190, 12, 2, 2));
        assertFalse(hitBox.isColliding(92, 165, 12, 2, -2));
    }

    /**
     * Test for reflecting when ball colliding with tile.
     */
    @Test
    public void testReflect() {
        HitBox hitBox = new HitBox(64, 192, 128, 192);
        float[] velocity = hitBox.reflect(2, 2, 88, 185);
        assertEquals(2.0, velocity[0], 1e-6);
        assertEquals(-2.0, velocity[1], 1e-6);
    }

    /**
     * Test for calculating distance between 2 points.
     */
    @Test
    public void testDistance() {
        HitBox hitBox1 = new HitBox(64, 192, 128, 192);
        assertEquals(7.0, hitBox1.distanceFromPointToLine(80, 185), 1e-6);
        assertEquals(1.0, hitBox1.distanceFromPointToLine(80, 191), 1e-6);

        HitBox hitBox2 = new HitBox(0, 0, 0, 0);
        assertEquals(5, hitBox2.distanceFromPointToLine(3, 4));

        HitBox hitBox3 = new HitBox(0, 0, 0, 3);
        assertEquals(5, hitBox3.distanceFromPointToLine(3, 4));

        HitBox hitBox4 = new HitBox(1, 2, 0, 0);
        assertEquals(2, hitBox4.distanceFromPointToLine(1, 4));
    }
}
