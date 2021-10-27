import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ScoreBoard {
    private final GraphicsContext GC;
    private final double HEIGHT;
    private final double LEFT_X;
    private final double RIGHT_X;
    private Color color;
    private int leftScore;
    private int rightScore;

    public ScoreBoard(GraphicsContext gc, double height, double leftX, double rightX) {
        this.GC = gc;
        this.HEIGHT = height;
        this.LEFT_X = leftX;
        this.RIGHT_X = rightX;
        this.color = Color.WHITE;
        this.leftScore = 0;
        this.rightScore = 0;
    }

    public void increaseLeft() {
        leftScore++;
    }

    public void increaseRight() {
        rightScore++;
    }

    public double getLeft() { return leftScore; }

    public double getRight() { return rightScore; }

    public void draw() {
        // save old color
        Paint old_color = GC.getFill();

        GC.setFill(color);
        GC.fillText(String.valueOf(leftScore), LEFT_X, HEIGHT);
        GC.fillText(String.valueOf(rightScore), RIGHT_X, HEIGHT);

        // set old color back
        GC.setFill(old_color);
    }

    public void reset() {
        leftScore = 0;
        rightScore = 0;
    }
}
