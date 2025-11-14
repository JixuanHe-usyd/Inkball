package inkball;
import processing.core.PApplet;

/**
 * The {@code HitBox} class defines a line segment used for collision detection
 * between a ball and obstacles in the game. It provides utility methods for
 * determining collision status, calculating reflections, and computing distances.
 */

public class HitBox {

    public float x1, y1, x2, y2; // // Coordinates of the two endpoints of the hitbox segment

    /**
     * Constructs a new {@code HitBox} object with the specified coordinates.
     *
     * @param x1 the x-coordinate of the first endpoint
     * @param y1 the y-coordinate of the first endpoint
     * @param x2 the x-coordinate of the second endpoint
     * @param y2 the y-coordinate of the second endpoint
     */
    public HitBox(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the distance between the two points
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        return PApplet.sqrt(PApplet.pow(x2 - x1, 2) + PApplet.pow(y2 - y1, 2));
    }

    /**
     * Checks if a ball is colliding with this hitbox.
     *
     * @param bx         the x-coordinate of the ball
     * @param by         the y-coordinate of the ball
     * @param ballRadius the radius of the ball
     * @param vx         the velocity of the ball in the x-direction
     * @param vy         the velocity of the ball in the y-direction
     * @return {@code true} if the ball is colliding with the hitbox, otherwise {@code false}
     */
    public boolean isColliding(float bx, float by, float ballRadius, float vx, float vy) {
        float segmentLength = distance(x1, y1, x2, y2);
        float distP1ToBall = distance(x1, y1, bx + vx, by + vy);
        float distP2ToBall = distance(x2, y2, bx + vx, by + vy);
        return distP1ToBall + distP2ToBall < segmentLength + ballRadius;
    }

    /**
     * Calculates the normal vector of the hitbox at the specified point.
     *
     * @param bx the x-coordinate of the point
     * @param by the y-coordinate of the point
     * @return the normal vector as a float array of size 2
     */
    public float[] getNormal(float bx, float by) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = distance(x1, y1, x2, y2);

        float[] N1 = new float[]{-dy / length, dx / length};
        float[] N2 = new float[]{dy / length, -dx / length};

        float midX = (x1 + x2) / 2;
        float midY = (y1 + y2) / 2;

        float N1x = midX + N1[0];
        float N1y = midY + N1[1];
        float N2x = midX + N2[0];
        float N2y = midY + N2[1];

        float distToN1 = distance(bx, by, N1x, N1y);
        float distToN2 = distance(bx, by, N2x, N2y);

        return distToN1 < distToN2 ? N1 : N2;
    }

    /**
     * Reflects the velocity of the ball after colliding with the hitbox.
     *
     * @param vx the velocity of the ball in the x-direction
     * @param vy the velocity of the ball in the y-direction
     * @param bx the x-coordinate of the ball
     * @param by the y-coordinate of the ball
     * @return the new velocity vector as a float array of size 2
     */
    public float[] reflect(float vx, float vy, float bx, float by) {
        float[] normal = getNormal(bx, by);

        float[] ballVelocityVectors = {vx, vy};

        float dotProduct = ballVelocityVectors[0] * normal[0] + ballVelocityVectors[1] * normal[1];

        ballVelocityVectors[0] = ballVelocityVectors[0] - 2 * dotProduct * normal[0];
        ballVelocityVectors[1] = ballVelocityVectors[1] - 2 * dotProduct * normal[1];
        return ballVelocityVectors;
    }

    /**
     * Calculates the shortest distance from a point to the line segment representing the hitbox.
     *
     * @param px the x-coordinate of the point
     * @param py the y-coordinate of the point
     * @return the shortest distance from the point to the line segment
     */
    public float distanceFromPointToLine(float px, float py) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return PApplet.dist(px, py, x1, y1);
        }

        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = PApplet.constrain(t, 0, 1);

        float nearestX = x1 + t * dx;
        float nearestY = y1 + t * dy;

        return PApplet.dist(px, py, nearestX, nearestY);
    }

}

