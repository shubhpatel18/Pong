import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Pong extends Application {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    private static final double SCORE_HEIGHT = 50;
    private static final double SCORE_LEFT_X = 150;
    private static final double SCORE_RIGHT_X = 650;

    private static final double TEXT_X = 50;
    private static final double TEXT_Y = 500;
    private static final double TEXT_Y_OFFSET = 50;

    private static final double PADDLE_HEIGHT = 100;
    private static final double PADDLE_WIDTH = 16;

    private static final double BALL_RADIUS = 8;

    private static final double LEFT_GUTTER = PADDLE_WIDTH / 2;
    private static final double RIGHT_GUTTER = WINDOW_WIDTH - PADDLE_WIDTH / 2;

    // variable distance ai paddle will try to reach between its center and the ball
    private double aiTolerance;

    // display and animation elements
    private final Canvas CANVAS;
    private final GraphicsContext GC;
    private final Timeline TL;

    // game elements
    private final Paddle leftPaddle;
    private final Paddle rightPaddle;
    private final Ball ball;
    private final ScoreBoard score;

    // game information
    private boolean atMenu;
    private boolean gameStarted;
    private boolean singlePlayer;
    private boolean twoPlayer;

    // paddle controls
    private boolean moveRightUp;
    private boolean moveRightDown;
    private boolean moveLeftUp;
    private boolean moveLeftDown;

    public static void main(String[] args) {
        launch(args);
    }

    public Pong() {
        // set up display and animation elements
        CANVAS = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GC = CANVAS.getGraphicsContext2D();

        // refresh every 16ms (60Hz animation)
        TL = new Timeline(new KeyFrame(Duration.millis(16), e -> run()));

        // set up game elements
        leftPaddle = new Paddle(GC, 0, PADDLE_WIDTH, PADDLE_HEIGHT, WINDOW_HEIGHT, 0);
        rightPaddle = new Paddle(GC, WINDOW_WIDTH - PADDLE_WIDTH, WINDOW_WIDTH, PADDLE_HEIGHT, WINDOW_HEIGHT, 0);
        ball = new Ball(GC, WINDOW_HEIGHT, 0, WINDOW_WIDTH, 0, BALL_RADIUS);
        score = new ScoreBoard(GC, SCORE_HEIGHT, SCORE_LEFT_X, SCORE_RIGHT_X);
    }

    public void init() {
        GC.setFont(new Font("Consolas", 50));
        TL.setCycleCount(Timeline.INDEFINITE);
    }

    public void start(Stage stage) {
        // set up window
        stage.setTitle("Pong");
        stage.getIcons().add(new Image(Pong.class.getResourceAsStream("icon.png")));
        stage.setScene(new Scene(new StackPane(CANVAS)));
        stage.show();

        // set key listeners
        stage.getScene().setOnKeyPressed(e -> {
            // outside of the menu, any key can start the game
            if (!atMenu) gameStarted = true;
            switch (e.getCode()) {
                case UP -> moveRightUp = true;
                case DOWN -> moveRightDown = true;
                case W -> moveLeftUp = true;
                case S -> moveLeftDown = true;
                case ESCAPE -> showMenu();
                case DIGIT1 -> setSinglePlayer();
                case DIGIT2 -> setTwoPlayer();
            }
        });

        stage.getScene().setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case UP -> moveRightUp = false;
                case DOWN -> moveRightDown = false;
                case W -> moveLeftUp = false;
                case S -> moveLeftDown = false;
            }
        });

        // begin game
        showMenu();
        TL.play();
    }

    private void setSinglePlayer() {
        if (atMenu) {
            if (!singlePlayer) {
                singlePlayer = true;
                twoPlayer = false;
                score.reset();
            }
            recalculateAiTolerance();
            atMenu = false;
            showStart();
        }
    }

    private void setTwoPlayer() {
        if (atMenu) {
            if (!twoPlayer) {
                singlePlayer = false;
                twoPlayer = true;
                score.reset();
            }
            atMenu = false;
            showStart();
        }
    }

    private void showStart() {
        reset();
        GC.setFill(Color.YELLOW);
        GC.fillText("  Press Any Key To Play", TEXT_X, TEXT_Y);
        GC.fillText("   Press ESC for Menu  ", TEXT_X, TEXT_Y + TEXT_Y_OFFSET);
    }

    private void showMenu() {
        atMenu = true;
        reset();
        GC.setFill(Color.YELLOW);
        GC.fillText("Press 1 for Single Player", TEXT_X, TEXT_Y);
        GC.fillText("  Press 2 for Two Player ", TEXT_X, TEXT_Y + TEXT_Y_OFFSET);
    }

    private void reset() {
        gameStarted = false;
        leftPaddle.reset();
        rightPaddle.reset();
        ball.reset();
        recalculateAiTolerance();
        redraw();
    }

    private void run() {
        if (gameStarted) {
            updateLocations(); // update ball and paddle locations
            redraw(); // redraw screen
            checkPaddleCollisions(); // check if the ball is being returned by a paddle
            checkMiss(); // check if the ball has been missed by a paddle
        }
    }

    private void updateLocations() {
        ball.update();

        controlPaddle(rightPaddle, moveRightUp, moveRightDown);

        if (twoPlayer) {
            // user control left paddle
            controlPaddle(leftPaddle, moveLeftUp, moveLeftDown);

        } else {
            // ai control left paddle

            if (ball.getXVec() < 0 && ball.getX() < 2 * WINDOW_WIDTH / 3) {
                // ball is in left 2/3 of screen and moving left, so move paddle to respond

                double distanceToTarget = ball.getYProjectionAt(0) - leftPaddle.getY();
                double paddleIterationsNeeded = Math.abs(distanceToTarget) / leftPaddle.getSpeed();
                double ballIterationsNeeded = ball.getX() / ball.getXSpeed();

                // have paddle start moving only when it can beat the ball to its location within 10 iterations
                if (paddleIterationsNeeded + 10 >= ballIterationsNeeded) {
                    boolean tooLow = distanceToTarget < -1 * aiTolerance;
                    boolean tooHigh = distanceToTarget > aiTolerance;
                    controlPaddle(leftPaddle, tooLow, tooHigh);
                }

            } else if (ball.getX() > WINDOW_WIDTH / 3){
                // recenter paddle if ball is far away and moving away from left paddle

                boolean belowCenter = leftPaddle.getY() - WINDOW_HEIGHT / 2 > leftPaddle.getSpeed();
                boolean aboveCenter = leftPaddle.getY() - WINDOW_HEIGHT / 2 < -1 * leftPaddle.getSpeed();
                controlPaddle(leftPaddle, belowCenter, aboveCenter);
            }
        }
    }

    private void controlPaddle(Paddle paddle, boolean upCondition, boolean downCondition) {
        if (upCondition) {
            paddle.moveUp();
        } else if (downCondition) {
            paddle.moveDown();
        }
    }

    private void redraw() {
        // clear background
        GC.setFill(Color.BLACK);
        GC.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // redraw ball and paddles
        leftPaddle.draw();
        rightPaddle.draw();
        ball.draw();
        score.draw();
    }

    private void checkPaddleCollisions() {
        if (leftPaddle.contains(ball.getLeftEdge(), ball.getY())) {
            ball.bounceOffLeft();
        } else if (rightPaddle.contains(ball.getRightEdge(), ball.getY())) {
            ball.bounceOffRight();
            recalculateAiTolerance();
        }
    }

    private void recalculateAiTolerance() {
        if (singlePlayer) {
            // the distance the ai paddle will keep between it's center and the ball
            // > PADDLE_HEIGHT/2 will result in a miss
            aiTolerance = leftPaddle.getSpeed() + Math.pow(Math.random(), 4) * PADDLE_HEIGHT;
        }
    }

    private void checkMiss() {
        if (ball.getLeftEdge() < LEFT_GUTTER) {
            score.increaseRight();
            showStart();
        } else if (ball.getRightEdge() > RIGHT_GUTTER) {
            score.increaseLeft();
            showStart();
        }
    }
}
