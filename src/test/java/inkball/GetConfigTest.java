package inkball;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetConfigTest {
    private static GetConfig newConfig;

    /**
     * Set up before testing.
     */
    @BeforeAll
    public static void setup() {
        App app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.stop();
        newConfig = app.config;
    }

    /**
     * Test for loading game information form config file.
     */
    @Test
    public void testLoadConfig() {
        newConfig.loadConfig(0);
        assertEquals(120, newConfig.time);
        assertEquals(10, newConfig.spawnInterval);
        assertEquals(1.0, newConfig.scoreIncreaseModifier, 1e-6);
        assertEquals(1.0, newConfig.scoreDecreaseModifier, 1e-6);
        assertEquals(6, newConfig.topBarBalls.size());
        assertEquals(6, newConfig.realTopBarBalls.size());

        HashMap<String, Integer> scoreIncreaseFromHoleCapture = newConfig.scoreIncreaseFromHoleCapture;
        assertEquals(5, scoreIncreaseFromHoleCapture.size());
        assertEquals(70, scoreIncreaseFromHoleCapture.get("grey"));
        assertEquals(50, scoreIncreaseFromHoleCapture.get("orange"));
        assertEquals(50, scoreIncreaseFromHoleCapture.get("blue"));
        assertEquals(50, scoreIncreaseFromHoleCapture.get("green"));
        assertEquals(100, scoreIncreaseFromHoleCapture.get("yellow"));

        HashMap<String, Integer> scoreDecreaseFromWrongHole = newConfig.scoreDecreaseFromWrongHole;
        assertEquals(5, scoreDecreaseFromWrongHole.size());
        assertEquals(0, scoreDecreaseFromWrongHole.get("grey"));
        assertEquals(25, scoreDecreaseFromWrongHole.get("orange"));
        assertEquals(25, scoreDecreaseFromWrongHole.get("blue"));
        assertEquals(25, scoreDecreaseFromWrongHole.get("green"));
        assertEquals(100, scoreDecreaseFromWrongHole.get("yellow"));
    }

    /**
     * Test for getting color index from color.
     */
    @Test
    public void testColorIndex() {
        assertEquals(0, newConfig.getColorIndexFromName("grey"));
        assertEquals(1, newConfig.getColorIndexFromName("orange"));
        assertEquals(2, newConfig.getColorIndexFromName("blue"));
        assertEquals(3, newConfig.getColorIndexFromName("green"));
        assertEquals(4, newConfig.getColorIndexFromName("yellow"));
        assertEquals(-1, newConfig.getColorIndexFromName("red"));
    }

}
