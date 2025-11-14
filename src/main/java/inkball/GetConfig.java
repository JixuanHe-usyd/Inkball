package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * The {@code GetConfig} class is responsible for loading and managing
 * the game configuration, including level layouts, ball settings, and score rules.
 * It reads configurations from a JSON file and initializes game elements accordingly.
 */
public class GetConfig {

    public PApplet p; // PApplet instance
    public String configPath; // The path of the config file
    public String[] layoutLevelFile; // The String Array of level layout
    public int time; // The countdown time for the level
    public int spawnInterval; // The interval before next ball spawns
    public float scoreIncreaseModifier; // Modifier for score increase from capturing a ball in the correct hole
    public float scoreDecreaseModifier; // Modifier for score decrease from capturing a ball in the wrong hole
    public ArrayList<String> topBarBalls; // List of ball's information in the top bar
    public ArrayList<Ball> realTopBarBalls; // List of actual Ball objects in the top bar
    public HashMap<String, Integer> scoreIncreaseFromHoleCapture; // Score increases for correct captures
    public HashMap<String, Integer> scoreDecreaseFromWrongHole; // Score decreases for wrong captures
    public PImage[] ballImages; // Array of ball images for rendering


    /**
     * Constructs a new {@code GetConfig} object with the specified configuration path and PApplet instance.
     *
     * @param configPath the path to the configuration JSON file
     * @param p          the PApplet instance used for rendering
     */
    public GetConfig(String configPath, PApplet p) {
        this.p = p;
        this.configPath = configPath;
        this.topBarBalls = new ArrayList<>();
        this.scoreIncreaseFromHoleCapture = new HashMap<>();
        this.scoreDecreaseFromWrongHole = new HashMap<>();
        this.realTopBarBalls = new ArrayList<>();
    }

    /**
     * Loads the configuration for a specific level from the JSON configuration file.
     *
     * <p>This method reads the level layout, time, spawn interval, ball colors,
     * and score rules from the JSON file. It initializes the game board layout
     * and creates ball objects based on the configuration.</p>
     *
     * @param levelIndex the index of the level to load from the configuration file
     */
    public void loadConfig(int levelIndex) {
        JSONObject config = p.loadJSONObject(configPath);
        JSONArray levels = config.getJSONArray("levels");

        JSONObject level = levels.getJSONObject(levelIndex);
        this.layoutLevelFile = readLayOut(level.getString("layout"));
        this.time = level.getInt("time");
        this.spawnInterval = level.getInt("spawn_interval");
        this.scoreIncreaseModifier = level.getFloat("score_increase_from_hole_capture_modifier");
        this.scoreDecreaseModifier = level.getFloat("score_decrease_from_wrong_hole_modifier");

        JSONArray topBarBallsArray = level.getJSONArray("balls");
        for (int i = 0; i < topBarBallsArray.size(); i++) {
            String ballColor = topBarBallsArray.getString(i);
            this.topBarBalls.add(ballColor);

            int ballColorIndex = getColorIndexFromName(ballColor);

            if (ballColorIndex != -1) {
                Ball newBall = new Ball(p, 20 + i * 32, 20, ballImages, ballColorIndex);
                realTopBarBalls.add(newBall);
            }
        }


        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        Set<String> keys1 = scoreIncrease.keys();
        for (String key : keys1) {
            int value = scoreIncrease.getInt(key);
            scoreIncreaseFromHoleCapture.put(key, value);
        }

        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");
        Set<String> keys2 = scoreDecrease.keys();
        for (String key : keys2) {
            int value = scoreDecrease.getInt(key);
            scoreDecreaseFromWrongHole.put(key, value);
        }
    }

    /**
     * Returns the color index for the given color name.
     * This index corresponds to the position of the color in the {@code ballImages} array.
     *
     * @param colorName the name of the color (e.g., "grey", "orange")
     * @return the index of the color, or -1 if the color is not recognized
     */
    public int getColorIndexFromName(String colorName) {
        switch (colorName.toLowerCase()) {
            case "grey":
                return 0;
            case "orange":
                return 1;
            case "blue":
                return 2;
            case "green":
                return 3;
            case "yellow":
                return 4;
            default:
                return -1;
        }
    }

    /**
     * Reads the layout of the level from a specified file and returns it as an array of strings.
     *
     * @param layoutFile the name of the layout file to read
     * @return an array of strings representing the level layout
     */
    public String[] readLayOut(String layoutFile) {
        return p.loadStrings(layoutFile);
    }

}
