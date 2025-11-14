package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game newGame;

    /**
     * Set up before testing.
     */
    @BeforeEach
    public void setup() {
        App app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.stop();
        newGame = app.game;
    }

    /**
     * Test for creating game board.
     */
    @Test
    public void testGetBoard() {
        String[] layout = new String[] {
                "    ",
                "    ",
                "    "
        };
        newGame.getBoard(layout, 100, 10, new ArrayList<>(), false, 0, 0);

        assertEquals(12, newGame.currentLevel.tiles.size());
        assertTrue(newGame.currentLevel.balls.isEmpty());
        assertTrue(newGame.currentLevel.spawner.isEmpty());
    }

}
