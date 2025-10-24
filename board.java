import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The main game panel where all the logic, drawing,
 * and input handling happens.
 */
public class Board extends JPanel implements ActionListener {

    // --- Constants ---
    // Board dimensions (in blocks)
    // Standard Tetris is 10 wide by 20 high (visible).
    // We add 2 hidden rows at the top for pieces to spawn in.
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22; 
    
    // Size of one block (in pixels)
    private static final int BLOCK_SIZE = 30;

    // The speed of the game (delay in milliseconds)
    private static final int TIMER_DELAY = 400;

    // --- Game State Variables ---
    private Timer timer; // Main game loop timer
    private boolean isFallingFinished = false; // True when a piece lands
    private boolean isPaused = false; // True if the game is paused
    private boolean isGameOver = false; // True when the game is over
    
    private int linesRemoved = 0; // Total lines cleared
    private int score = 0; // Player's score

    // Position of the current falling piece
    private int currentX = 0;
    private int currentY = 0;

    // The current falling piece
    private Piece currentPiece;

    // The "well" or grid. Stores all the landed pieces.
    // Using Color allows us to store the shape's color directly.
    private Color[][] board;

    /**
     * Constructor for the game board.
     */
    public Board() {
        // Set up the panel
        setFocusable(true); // Allow panel to receive keyboard input
        setBackground(Color.BLACK);
        
        // Calculate and set the preferred size of the panel
        // We only show 20 rows, not the 2 hidden ones.
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, 
                                     (BOARD_HEIGHT - 2) * BLOCK_SIZE));
        
        // Add the keyboard listener
        addKeyListener(new TAdapter());
    }

    /**
     * Initializes and starts the game.
     */
    public void start() {
        // Initialize the board grid
        board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();

        // Reset game state
        isFallingFinished = true;
        isPaused = false;
        isGameOver = false;
        linesRemoved = 0;
        score = 0;
        
        // Create the first piece
        newPiece();

        // Create and start the game timer
        timer = new Timer(TIMER_DELAY, this);
        timer.start();
    }
    
    /**
     * Fills the entire board with 'null' (empty).
     */
    private void clearBoard() {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                board[y][x] = null;
            }
        }
    }

    /**
     * Game loop's main action, called by the Timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver || isPaused) {
            return; // Do nothing if game is over or paused
        }

        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece(); // Create a new piece
        } else {
            oneLineDown(); // Move the current piece down
        }
    }

    /**
     * Creates a new random piece and places it at the top.
     * Checks for game over if the new piece immediately collides.
     */
    private void newPiece() {
        currentPiece = new Piece();
        currentPiece.setRandomShape();
        
        // Initial spawn position
        currentX = BOARD_WIDTH / 2;
        currentY = 1; // Spawn in the hidden rows

        // Check for game over
        if (!tryMove(currentPiece, currentX, currentY)) {
            isGameOver = true;
            timer.stop();
            currentPiece.setShape(Piece.Tetromino.NoShape); // Clear piece
        }
        
        repaint();
    }

    /**
     * Attempts to move the current piece down by one line.
     * If it can't, the piece has "landed".
     */
    private void oneLineDown() {
        if (!tryMove(currentPiece, currentX, currentY + 1)) {
            pieceDropped();
        }
    }

    /**
     * Called when a piece has landed.
     * It "stamps" the piece onto the board and checks for full lines.
     */
    private void pieceDropped() {
        // Stamp the piece onto the board
        for (int i = 0; i < 4; i++) {
            int x = currentX + currentPiece.x(i);
            int y = currentY + currentPiece.y(i);
            
            // Only stamp blocks that are on the board
            if (y >= 0) {
                 board[y][x] = currentPiece.getColor();
            }
        }

        // Check for and remove full lines
        removeFullLines();

        // If the game isn't over, signal for a new piece
        if (!isGameOver) {
            isFallingFinished = true;
        }
        
        repaint();
    }
    
    /**
     * Forces the piece to drop to the lowest possible position.
     */
    private void dropDown() {
        int newY = currentY;
        while (tryMove(currentPiece, currentX, newY + 1)) {
            newY++;
        }
        currentY = newY;
        pieceDropped();
    }

    /**
     * Pauses or unpauses the game.
     */
    private void pause() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
        repaint();
    }

    /**
     * The core collision detection logic.
     * Checks if a given piece at a new position is valid.
     * @param piece The piece to test
     * @param newX The new X-coordinate
     * @param newY The new Y-coordinate
     * @return true if the move is valid, false otherwise
     */
    private boolean tryMove(Piece piece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + piece.x(i);
            int y = newY + piece.y(i);

            // Check wall boundaries
            if (x < 0 || x >= BOARD_WIDTH) {
                return false;
            }
            // Check floor boundary
            if (y >= BOARD_HEIGHT) {
                return false;
            }
            
            // Check if the block is above the ceiling (which is fine)
            if (y < 0) {
                continue; 
            }

            // Check for collision with other landed pieces
            if (board[y][x] != null) {
                return false;
            }
        }

        // If no collisions, update the piece's position
        currentPiece = piece;
        currentX = newX;
        currentY = newY;
        
        repaint();
        return true;
    }

    /**
     * Checks the board for full lines, removes them, 
     * and shifts the lines above down.
     */
    private void removeFullLines() {
        int numFullLines = 0;

        // Iterate from the bottom row up to the top
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean lineIsFull = true;

            // Check if the current line is full
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == null) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                // Shift all lines above this one down
                for (int j = y; j > 0; j--) {
                    for (int x = 0; x < BOARD_WIDTH; x++) {
                        board[j][x] = board[j - 1][x];
                    }
                }
                // Clear the top row
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    board[0][x] = null;
                }
                // Since we shifted, we need to re-check the current row 'y'
                y++;
            }
        }

        // Update score
        if (numFullLines > 0) {
            // Simple scoring: 100, 300, 500, 800 for 1, 2, 3, 4 lines
            int[] points = {0, 100, 300, 500, 800};
            score += points[Math.min(numFullLines, 4)];
            linesRemoved += numFullLines;
        }
    }

    // --- Drawing ---

    /**
     * Main drawing method, called by repaint().
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the landed pieces (the "well")
        // We iterate from y=2 to skip the 2 hidden rows
        for (int y = 2; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] != null) {
                    drawBlock(g2d, x * BLOCK_SIZE, (y - 2) * BLOCK_SIZE, board[y][x]);
                }
            }
        }

        // Draw the current falling piece
        if (currentPiece.getShape() != Piece.Tetromino.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = currentX + currentPiece.x(i);
                int y = currentY + currentPiece.y(i);
                
                // Only draw the parts of the piece that are in the visible area
                if (y >= 2) {
                     drawBlock(g2d, x * BLOCK_SIZE, (y - 2) * BLOCK_SIZE, 
                               currentPiece.getColor());
                }
            }
        }
        
        // Draw the score and lines
        drawUI(g2d);

        // Draw Game Over or Paused message
        if (isGameOver) {
            drawMessage(g2d, "Game Over! (R to Restart)");
        } else if (isPaused) {
            drawMessage(g2d, "Paused (P to Resume)");
        }
    }

    /**
     * Helper method to draw a single Tetris block.
     * @param g The graphics context
     * @param x The pixel X-coordinate (top-left)
     * @param y The pixel Y-coordinate (top-left)
     * @param color The color of the block
     */
    private void drawBlock(Graphics2D g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        
        // Add a 3D-like bevel
        g.setColor(color.brighter());
        g.drawLine(x, y, x + BLOCK_SIZE - 1, y);
        g.drawLine(x, y, x, y + BLOCK_SIZE - 1);
        
        g.setColor(color.darker());
        g.drawLine(x + 1, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
        g.drawLine(x + BLOCK_SIZE - 1, y + 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
    }
    
    /**
     * Draws the Score and Lines on the side.
     * (A more advanced version would put this in a separate panel).
     */
    private void drawUI(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lines: " + linesRemoved, 10, 40);
    }
    
    /**
     * Draws a centered message over the game board.
     * @param g The graphics context
     * @param text The message to display
     */
    private void drawMessage(Graphics2D g, String text) {
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        
        // Center the text
        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2;
        
        g.drawString(text, x, y);
    }


    // --- Input Handling ---

    /**
     * Inner class to handle keyboard input.
     */
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyCode();

            // Handle Restart
            if (keycode == KeyEvent.VK_R && isGameOver) {
                start();
                return;
            }

            // Handle Pause
            if (keycode == KeyEvent.VK_P) {
                pause();
                return;
            }

            // Don't accept input if game is paused or over
            if (isPaused || isGameOver || currentPiece.getShape() == Piece.Tetromino.NoShape) {
                return;
            }

            // --- Game Controls ---
            switch (keycode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    tryMove(currentPiece, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    tryMove(currentPiece, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    // Rotate the piece
                    tryMove(currentPiece.rotateRight(), currentX, currentY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
            }
        }
    }
}
