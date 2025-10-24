import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;

/**
 * Main application class for the Tetris game.
 * This class creates the main window (JFrame) and adds the game
 * board (Board panel) to it.
 */
public class Tetris extends JFrame {

    public Tetris() {
        // Set up the main window
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create and add the game board
        Board board = new Board();
        add(board);
        
        // Arrange components to their preferred sizes
        pack(); 

        // Center the window on the screen
        setLocationRelativeTo(null);
        
        // Start the game logic *after* the UI is set up
        board.start();
    }

    public static void main(String[] args) {
        // Run the game on the Event Dispatch Thread (EDT)
        // This is the standard and safe way to start a Swing application
        SwingUtilities.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
