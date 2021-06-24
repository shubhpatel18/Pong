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

    private double speed;
    private Color color;

    public Ball(GraphicsContext gc, double yMax, double yMin, double xMax, double xMin, double radius) {
        this.GC = gc;
        this.Y_MAX = yMax - radius;
        this.Y_MIN = yMin + radius;
        this.X_MAX = xMax - radius;
        this.X_MIN = xMin + radius;
        this.RADIUS = radius;

        this.xPos = (xMax + xMin)/2;
        this.yPos = (yMax + yMin)/2;

        //defaults
        speed = 8;

        // initial speed slower to prevent easy losses
        this.xVec = speed/2 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -x or +x
        this.yVec = speed/4 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -y or +y
        color = Color.WHITE;
    }

    public void update() {
        xPos += xVec;
        if (xPos > X_MAX) {
            xPos = X_MAX;
            xVec *= -1;
        } else if (xPos < X_MIN) {
            xPos = X_MIN;
            xVec *= -1;
        }

        yPos += yVec;
        if (yPos > Y_MAX) {
            yPos = Y_MAX;
            yVec *= -1;
        } else if (yPos < Y_MIN) {
            yPos = Y_MIN;
            yVec *= -1;
        }
    }

    public void bounceOffRight() {
        xVec = -1 * speed;
        yVec = Math.signum(yVec) * (.25*speed + .5*speed*Math.random());
    }

    public void bounceOffLeft() {
        xVec = speed;
        yVec = Math.signum(yVec) * (.25*speed + .5*speed*Math.random());
    }

    public void draw() {
        // save old color
        Paint old_color = GC.getFill();

        GC.setFill(color);
        GC.fillOval(xPos - RADIUS, yPos - RADIUS, 2*RADIUS, 2*RADIUS);

        // set old color back
        GC.setFill(old_color);
    }

    public void reset() {
        this.xPos = (X_MAX + X_MIN)/2;
        this.yPos = (Y_MAX + Y_MIN)/2;

        // initial speed slower to prevent easy losses
        this.xVec = speed/2 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -x or +x
        this.yVec = speed/4 * (Math.random() > .5 ? 1 : -1); // 50% chance to start toward -y or +y
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getLeftEdge() { return xPos - RADIUS; }

    public double getRightEdge() { return xPos + RADIUS; }

    public double getXVec() { return xVec; }

    public double getXSpeed() { return Math.abs(xVec); }

    public double getYProjectionAt(int x) {
        // y2-y1 = m(x2-x1)
        // y2 = m(x2-x1) + y1
        return yVec/xVec * (x - xPos) + yPos;
    }
}
