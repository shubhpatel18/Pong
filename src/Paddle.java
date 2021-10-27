import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Paddle {
    private final GraphicsContext GC;

    private final double X_POS_L;
    private final double X_POS_R;
    private double yMax;
    private double yMin;
    private double height;

    private final Side side;

    private double yPos;
    private double speed;
    private Color color;

    public Paddle(GraphicsContext gc, double xPosL, double xPosR, double height, double yMax, double yMin, Side side) {
        this.GC = gc;
        this.height = height;
        this.X_POS_L = xPosL;
        this.X_POS_R = xPosR;
        this.yMax = yMax - this.height/2;
        this.yMin = yMin + this.height/2;
        this.yPos = (yMax + yMin)/2;
        this.side = side;

        // defaults
        this.speed = 10;
        this.color = Color.WHITE;
    }

    public void moveDown() {
        yPos += speed;
        if (yPos > yMax) yPos = yMax;
    }

    public void moveUp() {
        yPos -= speed;
        if (yPos < yMin) yPos = yMin;
    }

    public boolean contains(double x, double y) {
        boolean within_y = (y > yPos - height /2) && (y < yPos + height /2);
        boolean within_x = (x > X_POS_L && side == Side.RIGHT) || (x < X_POS_R && side == Side.LEFT);
        return within_y && within_x;
    }

    public void draw() {
        // save old color
        Paint old_color = GC.getFill();

        GC.setFill(color);
        GC.fillRect(X_POS_L, yPos - height/2, X_POS_R, height);

        // set old color back
        GC.setFill(old_color);
    }

    public void setHeight(double height) {
        this.yMax = this.yMax + this.height/2 - height/2;
        this.yMin = this.yMin - this.height/2 + height/2;
        this.height = height;
    }

    public void reset() {
        this.yPos = (yMax + yMin)/2;
    }

    public double getY() {
        return yPos;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRightEdge() {
        return X_POS_R;
    }

    public double getLeftEdge() {
        return X_POS_L;
    }

    public enum Side {
        RIGHT,
        LEFT
    }
}
