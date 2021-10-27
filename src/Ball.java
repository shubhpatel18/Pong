import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Ball {
    private final GraphicsContext GC;

    private final double Y_MAX;
    private final double Y_MIN;
    private final double X_MAX;
    private final double X_MIN;

    private final double RADIUS;

    private double xPos;
    private double yPos;

    private double xVec;
    private double yVec;

    private final double DEFAULT_SPEED = 8;
    private final double SPEED_INCREASE = 1;
    private double speed;
    private Color color;

    public Ball(GraphicsContext gc, double yMax, double yMin, double xMax, double xMin, double radius) {
        this.GC = gc;
        this.Y_MAX = yMax - radius;
        this.Y_MIN = yMin + radius;
        this.X_MAX = xMax - radius;
        this.X_MIN = xMin + radius;
        this.RADIUS = radius;

        this.xPos = (xMax + xMin) / 2;
        this.yPos = (yMax + yMin) / 2;

        //defaults
        speed = DEFAULT_SPEED;

        // initial speed slower to prevent easy losses
        this.xVec = speed / 2 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -x or +x
        this.yVec = speed / 4 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -y or +y
        color = Color.WHITE;
    }

    public void update() {
        xPos += xVec;

        yPos += yVec;
        if (yPos > Y_MAX) {
            yPos = Y_MAX;
            yVec *= -1;
        } else if (yPos < Y_MIN) {
            yPos = Y_MIN;
            yVec *= -1;
        }
    }

    public void bounceOffRight(double paddle_edge) {
        speed += SPEED_INCREASE;
        xVec = -1 * speed;
        yVec = Math.signum(yVec) * (.25 * speed + .5 * speed * Math.random());
        xPos = paddle_edge;
        yPos += yPos - getYProjectionAt(paddle_edge);
    }

    public void bounceOffLeft(double paddle_edge) {
        speed += SPEED_INCREASE;
        xVec = speed;
        yVec = Math.signum(yVec) * (.25 * speed + .5 * speed * Math.random());
        xPos = paddle_edge;
        yPos += yPos - getYProjectionAt(paddle_edge);
    }

    public void draw() {
        // save old color
        Paint old_color = GC.getFill();

        GC.setFill(color);
        GC.fillOval(xPos - RADIUS, yPos - RADIUS, 2 * RADIUS, 2 * RADIUS);

        // set old color back
        GC.setFill(old_color);
    }

    public void reset() {
        color = Color.WHITE;
        speed = DEFAULT_SPEED;
        this.xPos = (X_MAX + X_MIN) / 2;
        this.yPos = (Y_MAX + Y_MIN) / 2;

        // initial speed slower to prevent easy losses
        this.xVec = speed / 2 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -x or +x
        this.yVec = speed / 4 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -y or +y
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getLeftEdge() {
        return xPos - RADIUS;
    }

    public double getRightEdge() {
        return xPos + RADIUS;
    }

    public double getBottomEdge() {
        return yPos + RADIUS;
    }

    public double getTopEdge() {
        return yPos - RADIUS;
    }

    public double getXVec() {
        return xVec;
    }

    public double getXSpeed() {
        return Math.abs(xVec);
    }

    public double getYProjectionAt(double x) {
        // y2-y1 = m(x2-x1)
        // y2 = m(x2-x1) + y1
        return yVec / xVec * (x - xPos) + yPos;
    }
}
