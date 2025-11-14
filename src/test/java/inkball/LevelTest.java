package inkball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {
    private Level level;
    private App app;

    /**
     * Set up before testing.
     */
    @BeforeEach
    public void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.stop();
        level = app.game.currentLevel;
    }

    /**
     * Basic tests for parameters in level class.
     */
    @Test
    public void basicTest() {
        assertEquals(1, level.balls.size());
        assertEquals(0, level.score);
        assertTrue(level.playerLines.isEmpty());
        assertEquals(0, app.levelIndex);
        assertFalse(level.isLevelComplete);
        assertFalse(level.islevelFailed);
    }

    /**
     * Test score increasing when level completes.
     */
    @Test
    public void testGameLoop() {
        assertEquals(0, level.score);
        level.scoreAdditionCounter = 2;
        level.countdownTime = 3;
        level.incrementScoreWithTime();
        assertTrue(level.score > 0);
    }

    /**
     * Test for updating balls.
     */
    @Test
    public void testUpdate() {
        App.random.setSeed(0);
        assertEquals(0, level.yellowTileMoveCounter);
        for (int i = 0; i < 8000; i++) {
            level.updateYellowTileMovement();
        }
        assertTrue(level.yellowTileMoveCounter >= 0);
    }

    /**
     * Test for get correct level board.
     */
    @Test
    public void testGetLevel() {
        String[] layout = new String[] {
                "    ",
                "    ",
                "    "
        };

        level.getLevel(layout);

        assertEquals(12, level.tiles.size());
        assertTrue(level.balls.isEmpty());
        assertTrue(level.spawner.isEmpty());
    }

}
