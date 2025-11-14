package inkball;
import processing.core.PApplet;
import java.util.*;

/**
 * The {@code PlayerLine} class represents a line drawn by the player.
 * It manages the points of the line, detects intersections with balls,
 * and creates hitboxes for collision detection.
 */

public class PlayerLine {
    public ArrayList<float[]> points; // List of points player adds
    public ArrayList<HitBox> hitBoxes; // List of hit boxes of a line

    /**
     * Constructs a new {@code PlayerLine} object, initializing the list of points and hitboxes.
     */
    public PlayerLine() {
        points = new ArrayList<>();
        hitBoxes = new ArrayList<>();
    }

    /**
     * Adds a new point to the line and updates hitboxes if necessary.
     *
     * <p>The point will not be added if it overlaps with a ball or is
     * too close to the top bar. Additionally, if the new segment intersects
     * a ball, the point is not added.</p>
     *
     * @param x     the x-coordinate of the new point
     * @param y     the y-coordinate of the new point
     * @param balls the list of balls to check for intersections
     */
    public void addPoint(float x, float y, ArrayList<Ball> balls) {
        if (y <= 64) {
            return;
        }

        for (Ball ball : balls) {
            if ((x >= ball.x && x <= ball.x + ball.diameter) && (y >= ball.y && y <= ball.y + ball.diameter)) {
                return;
            }
        }

        if (points.size() > 0) {
            float[] lastPoint = points.get(points.size() - 1);

            for (Ball ball : balls) {
                if (isLineIntersectingCircle(lastPoint[0], lastPoint[1], x, y, ball.x + ball.diameter / 2, ball.y + ball.diameter / 2, ball.diameter / 2)) {
                    return;
                }
            }
        }

        if (points.size() > 3) {
            float[] prePoint = points.get(points.size() - 3);
            hitBoxes.add(new HitBox(prePoint[0], prePoint[1], x, y));
        }
        points.add(new float[]{x, y});
    }

    /**
     * Displays the line by drawing each segment between consecutive points.
     *
     * @param p the PApplet instance used for rendering
     */
    public void display(PApplet p) {
        p.stroke(0);
        p.strokeWeight(10);
        for (int i = 0; i < points.size() - 1; i++) {
            p.line(points.get(i)[0], points.get(i)[1], points.get(i+1)[0], points.get(i+1)[1]);
        }

    }

    /**
     * Returns the list of hitboxes associated with this line.
     *
     * @return the list of hitboxes
     */
    public ArrayList<HitBox> getHitBoxes() {
        return hitBoxes;
    }

    /**
     * Checks if a given point (e.g., the mouse pointer) is close to any part of the line.
     *
     * @param mouseX the x-coordinate of the point
     * @param mouseY the y-coordinate of the point
     * @return {@code true} if the point is near the line, otherwise {@code false}
     */
    public boolean isPointOnLine(float mouseX, float mouseY) {
        for (HitBox hitBox : hitBoxes) {
            float distance = hitBox.distanceFromPointToLine(mouseX, mouseY);

            if (distance < 15.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a line segment intersects with a given circle (ball).
     *
     * @param x1     the x-coordinate of the first endpoint of the line segment
     * @param y1     the y-coordinate of the first endpoint of the line segment
     * @param x2     the x-coordinate of the second endpoint of the line segment
     * @param y2     the y-coordinate of the second endpoint of the line segment
     * @param cx     the x-coordinate of the circle's center
     * @param cy     the y-coordinate of the circle's center
     * @param radius the radius of the circle
     * @return {@code true} if the line segment intersects the circle, otherwise {@code false}
     */
    public boolean isLineIntersectingCircle(float x1, float y1, float x2, float y2, float cx, float cy, float radius) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float lengthSquared = dx * dx + dy * dy;
        float t = ((cx - x1) * dx + (cy - y1) * dy) / lengthSquared;

        t = Math.max(0, Math.min(1, t));

        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;

        float distanceSquared = (cx - closestX) * (cx - closestX) + (cy - closestY) * (cy - closestY);

        return distanceSquared <= radius * radius;
    }

}

