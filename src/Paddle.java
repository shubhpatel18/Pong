import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Paddle {
    private final GraphicsContext GC;

    private final double X_POS_L;
    private final double X_POS_R;
    private final double HEIGHT;
    private final double Y_MAX;
    private final double Y_MIN;

    private double yPos;
    private double speed;
    private Color color;

    public Paddle(GraphicsContext gc, double xPosL, double xPosR, double height, double yMax, double yMin) {
        this.GC = gc;
        this.HEIGHT = height;
        this.X_POS_L = xPosL;
        this.X_POS_R = xPosR;
        this.Y_MAX = yMax - HEIGHT/2;
        this.Y_MIN = yMin + HEIGHT/2;
        this.yPos = (yMax + yMin)/2;

        // defaults
        this.speed = 10;
        this.color = Color.WHITE;
    }

    public void moveDown() {
        yPos += speed;
        if (yPos > Y_MAX) yPos = Y_MAX;
    }

    public void moveUp() {
        yPos -= speed;
        if (yPos < Y_MIN) yPos = Y_MIN;
    }

    public boolean contains(double x, double y) {
        boolean within_y = (y > yPos - HEIGHT/2) && (y < yPos + HEIGHT/2);
        boolean within_x = (x > X_POS_L) && (x < X_POS_R);
        return within_y && within_x;
    }

    public void draw() {
        // save old color
        Paint old_color = GC.getFill();

        GC.setFill(color);
        GC.fillRect(X_POS_L, yPos - HEIGHT/2, X_POS_R, HEIGHT);

        // set old color back
        GC.setFill(old_color);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void reset() {
        this.yPos = (Y_MAX + Y_MIN)/2;
    }

    public double getY() {
        return yPos;
    }

    public double getSpeed() {
        return speed;
    }
}
