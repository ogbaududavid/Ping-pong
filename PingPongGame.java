import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PingPongGame extends JPanel implements ActionListener {
    //Fields
    private int paddlePosition; 
    private int ballX, ballY; 
    private double ballSpeedX, ballSpeedY; 
    private double speedIncrement = 0.01; 
    private static final int PADDLE_WIDTH = 60; 
    private static final int PADDLE_HEIGHT = 10; 
    private static final int BALL_SIZE = 15; 
    private static final int SCREEN_WIDTH = 400; 
    private static final int SCREEN_HEIGHT = 300; 
    private Timer timer; 
    private int lives = 3; 
    private String playerName; 
    private int score = 0; 

    // Initializes a new instance of the PingPongGame class.
    // @param playerName The name of the player
    public PingPongGame(String playerName) {
        this.playerName = playerName;
        paddlePosition = SCREEN_WIDTH / 2 - PADDLE_WIDTH / 2; // Start paddle in the center
        ballX = SCREEN_WIDTH / 2; // Start ball in the center
        ballY = SCREEN_HEIGHT / 2; // Start ball in the center
        ballSpeedX = 2; // Initial ball speed to the right
        ballSpeedY = -2; // Initial ball speed upwards

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT && paddlePosition > 0) {
                    paddlePosition -= 20; // Move paddle left
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && paddlePosition < SCREEN_WIDTH - PADDLE_WIDTH) {
                    paddlePosition += 20; // Move paddle right
                }
            }
        });

        timer = new Timer(15, this); // Timer to control game updates
        timer.start(); // Start the game loop
        startSpeedIncreaseTimer(); // Start the speed increase timer
    }

    // Paints the current state of the game.
    // @param g Graphics object for drawing
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.fillRect(paddlePosition, SCREEN_HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT); // Draw paddle
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE); // Draw ball

        // Display lives and score
        g.drawString("Lives: " + lives, 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Player: " + playerName, 10, 60);
    }

    // Updates the game state.
    // @param e ActionEvent for timer update
    @Override
    public void actionPerformed(ActionEvent e) {
        // Move the ball
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Check for collision with the paddle
        if (ballY + BALL_SIZE >= SCREEN_HEIGHT - PADDLE_HEIGHT &&
                ballX + BALL_SIZE >= paddlePosition && ballX <= paddlePosition + PADDLE_WIDTH) {
            ballSpeedY = -ballSpeedY; // Change direction if hitting the paddle
            score++; // Increase score when ball hits paddle
        }

        // Check for wall collisions
        if (ballX <= 0 || ballX >= SCREEN_WIDTH - BALL_SIZE) {
            ballSpeedX = -ballSpeedX; // Change direction if hitting the walls
        }
        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY; // Change direction if hitting the top
        }
        if (ballY >= SCREEN_HEIGHT) {
            loseLife(); // Lose a life if the ball goes out of bounds
        }

        repaint(); // Repaint the panel to update graphics
    }

    // Decreases lives and prompts the user.
    private void loseLife() {
        lives--; // Lose a life
        if (lives == 0) {
            timer.stop(); // Stop the game if no lives are left
            JOptionPane.showMessageDialog(this, "Game Over, " + playerName + "!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else {
            int option = JOptionPane.showConfirmDialog(this, 
                "You lost a life! You have " + lives + " lives left. Do you want to continue?", 
                "Life Lost", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.NO_OPTION) {
                timer.stop(); // Stop the game if the player chooses not to continue
                JOptionPane.showMessageDialog(this, "Thanks for playing, " + playerName + "!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

            // Reset ball position
            ballX = SCREEN_WIDTH / 2;
            ballY = SCREEN_HEIGHT / 2;
            ballSpeedX = 2; // Reset direction
            ballSpeedY = -2;
        }
    }

    // Starts a timer to increase ball speed every 30 seconds.
    private void startSpeedIncreaseTimer() {
        new Timer(15000, e -> {
            ballSpeedX *= (1 + speedIncrement);
            ballSpeedY *= (1 + speedIncrement);
        }).start();
    }

    // The main entry point for the application.
    // @param args Command line arguments
    public static void main(String[] args) {
        String playerName = JOptionPane.showInputDialog("Enter your name:"); // Ask for player name
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player"; // Default name if none entered
        }

        JFrame frame = new JFrame("Ping Pong Game"); // Create the main window
        PingPongGame game = new PingPongGame(playerName); // Create a new game instance
        frame.add(game); // Add the game panel to the frame
        frame.pack(); // Pack the frame to fit components
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); // Make the window visible
    }
}
