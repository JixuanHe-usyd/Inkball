package inkball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import java.util.*;

import processing.core.PImage;
import processing.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private App app;
    private final int SPACE_KEY_CODE = 32;
    private GetConfig config;
    private PApplet p;

    /**
     * Set up before testing.
     */
    @BeforeEach
    public void setup() {
        app = new App();
        p = new PApplet();
        config = app.config;
        app.levelFailed = false;
        app.isPaused = false;
        app.pausedTime = 0;
        app.frameCount = 0;
        app.levelIndex = 1;
        app.levelCompleted = false;
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.stop();
    }

    /**
     * Test for key pressed.
     */
    @Test
    public void testKeyPressedOne() {
        app.key = ' ';
        app.keyCode = SPACE_KEY_CODE;
        app.keyPressed();
        assertTrue(app.isPaused);

        app.frameCount += 120;
        app.keyPressed();
        assertFalse(app.isPaused);
    }

    /**
     * Test key pressed.
     */
    @Test
    public void testKeyPressedTwo() {
        app.levelFailed = true;
        app.key = ' ';
        app.keyCode = SPACE_KEY_CODE;
        app.keyPressed();
        assertFalse(app.isPaused);
    }

    /**
     * Test key pressed.
     */
    @Test
    public void testKeyPressedRestart() {
        app.key = 'r';
        app.keyCode = 'r';

        app.levelIndex = 1;
        app.keyPressed();
        assertFalse(app.levelCompleted);
        assertFalse(app.levelIndex >= 2);

        app.levelIndex = 2;
        app.levelCompleted = true;
        app.keyPressed();
        assertTrue(app.levelCompleted);
    }

    /**
     * Test for initializing level.
     */
    @Test
    public void testInitializeLevel() {
        app.initializeLevel();
        assertFalse(app.isPaused);
        assertEquals(0, app.pausedTime);
        assertEquals(0, app.elapsedTime);
        assertEquals(0, app.lastPauseFrame);
        assertFalse(app.levelFailed);
        assertFalse(app.levelCompleted);
    }

    /**
     * Test for restarting game when finished.
     */
    @Test
    public void testRestartGame() {
        app.restartGame();
        assertEquals(0, app.levelIndex);
        assertEquals(0, App.totalScore);
    }

    /**
     * Test for restart level.
     */
    @Test
    public void testRestartLevel() {
        app.levelIndex = 0;
        app.restartLevel();
        assertEquals(0, App.totalScore);
        assertFalse(app.isPaused);
        assertEquals(0, app.pausedTime);
        assertEquals(0, app.elapsedTime);
        assertEquals(0, app.lastPauseFrame);
        assertFalse(app.levelFailed);
        assertFalse(app.levelCompleted);

        app.levelIndex = 1;
        app.previousLevelScore = 200;
        app.restartLevel();
        assertEquals(app.previousLevelScore, App.totalScore);
        assertFalse(app.isPaused);
        assertEquals(0, app.pausedTime);
        assertEquals(0, app.elapsedTime);
        assertEquals(0, app.lastPauseFrame);
        assertFalse(app.levelFailed);
        assertFalse(app.levelCompleted);
    }

    /**
     * Test for going to next level when one level completes.
     */
    @Test
    public void testGoToNextLevel() {
        app.levelIndex = 0;
        App.totalScore = 200;
        app.previousLevelScore = 100;
        app.goToNextLevel();
        assertEquals(100, App.totalScore);
        assertEquals(1, app.levelIndex);

        app.levelIndex = 1;
        App.totalScore = 200;
        app.previousLevelScore = 100;
        app.goToNextLevel();
        assertEquals(100, App.totalScore);
        assertEquals(2, app.levelIndex);

        app.levelIndex = 2;
        assertFalse(app.levelCompleted);
        app.goToNextLevel();
        assertTrue(app.levelCompleted);
    }

    /**
     * Test for draw method.
     */
    @Test
    public void testDraw() {
        PApplet.runSketch(new String[]{"App"}, app);
        app.delay(1000);
        app.loop();

        app.isPaused = false;
        app.levelCompleted = false;
        app.levelFailed = false;
        app.elapsedTime = 0;

        app.draw();

        assertEquals(1.0f / App.FPS, app.elapsedTime, 0.01f);
        assertFalse(app.levelCompleted);

        app.levelCompleted = true;
        app.levelIndex = 3;

        app.draw();
        assertTrue(app.levelCompleted);
        assertEquals(3, app.levelIndex);

        app.levelFailed = true;
        app.levelCompleted = false;

        app.draw();
        assertTrue(app.levelFailed);

        app.game.currentLevel.isLevelComplete = true;
        app.game.currentLevel.countdownTime = 0.05f;
        app.levelCompleted = false;

        app.draw();
        assertTrue(app.levelCompleted);

        app.game.currentLevel.countdownTime = 0.0f;
        app.levelCompleted = false;

        app.draw();
        assertTrue(app.levelCompleted);

        app.isPaused = false;
        app.levelCompleted = false;
        app.levelFailed = false;
        app.levelIndex = 1;
        app.game.currentLevel.islevelFailed = false;

        app.draw();
        assertFalse(app.levelCompleted);
        assertFalse(app.levelFailed);
    }

    /**
     * Test for main method.
     */
    @Test
    public void testMain() {
        PApplet.main("inkball.App");
        app.delay(1000);

        assertNotNull(app);
        assertTrue(app.frameCount >= 0);
        assertFalse(app.isPaused);
        assertFalse(app.levelCompleted);
        assertFalse(app.levelFailed);

    }

    /**
     * Test for mouse dragged.
     */
    @Test
    public void testMouseDragged() {
        PApplet.runSketch(new String[]{"App"}, app);
        app.delay(1000);
        app.loop();

        app.game.currentLevel = new Level(p, 576, 640, 32, 64, new PImage[]{}, new PImage[]{}, new PImage[]{}, new PImage[]{}, new PImage[]{}, new PImage[]{}, new PImage[]{});
        app.game.currentLevel.playerLines = new ArrayList<>();
        app.game.currentLevel.balls = new ArrayList<>();

        PlayerLine line = new PlayerLine();
        app.game.currentLevel.playerLines.add(line);

        MouseEvent e = new MouseEvent(null, 0, 0, 0, 150, 200, 0, PApplet.LEFT);
        app.mouseX = 150;
        app.mouseY = 200;
        app.mouseButton = PApplet.LEFT;

        app.mouseDragged(e);
        assertEquals(1, line.points.size());
        assertEquals(150, line.points.get(0)[0]);
        assertEquals(200, line.points.get(0)[1]);
    }

    /**
     * Test for mouse pressed when game fails.
     */
    @Test
    public void testMousePressedWhenGameFail() {
        app.delay(1000);
        app.loop();

        app.levelFailed = true;
        MouseEvent e = new MouseEvent(null, 0, 0, 0, 150, 200, 0, PApplet.LEFT);
        app.mousePressed(e);
        assertTrue(app.game.currentLevel.playerLines.isEmpty());
    }

    /**
     * Test for mouse pressed when using left button
     */
    @Test
    public void testMousePressedWithLeftButton() {
        app.delay(1000);
        app.loop();
        MouseEvent e = new MouseEvent(null, 0, 0, 0, 150, 200, 0, PApplet.LEFT);
        app.mousePressed(e);
        assertEquals(1, app.game.currentLevel.playerLines.size());
        PlayerLine newLine = app.game.currentLevel.playerLines.get(0);
        assertEquals(1, newLine.points.size());
        assertEquals(150, newLine.points.get(0)[0]);
        assertEquals(200, newLine.points.get(0)[1]);
    }

    /**
     * Test mouse pressed When using ctrl + left.
     */
    @Test
    public void testMousePressedWithCtrlLeftButton() {
        app.delay(1000);
        app.loop();
        PlayerLine line = new PlayerLine();
        line.addPoint(150, 200, app.game.currentLevel.balls);
        app.game.currentLevel.playerLines.add(line);

        app.keyPressed = true;
        app.keyCode = PApplet.CONTROL;
        app.mouseButton = PApplet.LEFT;
        MouseEvent e = new MouseEvent(null, 0, 0, 0, 150, 200, 0, PApplet.LEFT);

        app.mousePressed(e);

        assertTrue(app.game.currentLevel.playerLines.isEmpty(), "The line should be removed on Ctrl + Left-click.");
    }

    /**
     * Test mouse pressed when removing lines.
     */
    @Test
    public void testMousePressedWithNoLinesToRemove() {
        app.delay(1000);
        app.loop();
        MouseEvent e = new MouseEvent(null, 0, 0, 0, 150, 200, 0, PApplet.RIGHT);

        app.mousePressed(e);

        assertTrue(app.game.currentLevel.playerLines.isEmpty(), "No lines should be removed if none exist.");
    }

    /**
     * Test for pausing and resuming game
     */
    @Test
    public void testKeyPressedSpacePauseResume() {
        app.delay(1000);
        app.loop();
        app.key = ' ';
        app.keyCode = 32;
        assertFalse(app.isPaused);

        app.keyPressed(null);
        assertTrue(app.isPaused);

        app.frameCount += 120;
        app.keyPressed(null);
        assertFalse(app.isPaused);
        assertEquals(2.0f, app.pausedTime, 0.01f);
    }

    /**
     * Test for space key pressed when level fail.
     */
    @Test
    public void testKeyPressedSpaceWhenLevelFailed() {
        app.delay(1000);
        app.loop();
        app.levelFailed = true;
        app.key = ' ';
        app.keyCode = 32;
        app.keyPressed(null);
        assertFalse(app.isPaused);
    }

    /**
     * Test for restarting level.
     */
    @Test
    public void testKeyPressedRestartLevel() {
        app.delay(1000);
        app.loop();
        app.key = 'r';
        app.keyCode = 'r';

        app.levelIndex = 1;
        app.levelCompleted = false;

        app.keyPressed(null);
        assertFalse(app.levelCompleted);
        assertEquals(1, app.levelIndex);
    }

    /**
     * Test for game restarting
     */
    @Test
    public void testKeyPressedRestartGame() {
        app.delay(1000);
        app.loop();
        app.levelIndex = 2;
        app.levelCompleted = true;
        app.key = 'r';
        app.keyCode = 'r';
        app.keyPressed(null);
        assertEquals(0, app.levelIndex);
        assertFalse(app.levelCompleted);
    }

}
