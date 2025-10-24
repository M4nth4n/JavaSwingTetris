import java.awt.Color;
import java.util.Random;

/**
 * Represents a single Tetris piece (Tetromino).
 * Holds the piece's shape, coordinates, and color.
 * Also handles rotation logic.
 */
public class Piece {

    // Enum for the 7 different Tetris shapes (and one for NoShape)
    public enum Tetromino {
        NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
    }

    private Tetromino pieceShape;
    private int[][] coords; // Stores the 4 [x, y] coordinates for the piece
    private Color color;

    // A table defining the initial [x,y] coordinates for each shape.
    // These are relative to the piece's center (0,0).
    private static final int[][][] COORDS_TABLE = new int[][][] {
        { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },   // NoShape
        { { -1, -1 }, { 0, -1 }, { 0, 0 },  { 1, 0 } },    // ZShape
        { { -1, 0 },  { 0, 0 },  { 0, -1 }, { 1, -1 } },   // SShape
        { { -1, 0 },  { 0, 0 },  { 1, 0 },  { 2, 0 } },    // LineShape
        { { -1, 0 },  { 0, 0 },  { 1, 0 },  { 0, -1 } },   // TShape
        { { 0, 0 },   { 1, 0 },  { 0, -1 }, { 1, -1 } },   // SquareShape
        { { -1, -1 }, { -1, 0 }, { 0, 0 },  { 1, 0 } },    // LShape
        { { -1, 0 },  { 0, 0 },  { 1, 0 },  { 1, -1 } }    // MirroredLShape
    };

    // A table for the color of each shape
    private static final Color[] COLOR_TABLE = new Color[] {
        new Color(0, 0, 0),        // NoShape (Black)
        new Color(204, 102, 102),  // ZShape (Red)
        new Color(102, 204, 102),  // SShape (Green)
        new Color(102, 102, 204),  // LineShape (Blue)
        new Color(204, 204, 102),  // TShape (Yellow)
        new Color(204, 102, 204),  // SquareShape (Magenta)
        new Color(102, 204, 204),  // LShape (Cyan)
        new Color(218, 170, 0)     // MirroredLShape (Orange)
    };

    /**
     * Creates an empty "NoShape" piece.
     */
    public Piece() {
        coords = new int[4][2];
        setShape(Tetromino.NoShape);
    }

    /**
     * Sets the piece to a specific shape.
     * @param shape The Tetromino shape to set
     */
    public void setShape(Tetromino shape) {
        this.pieceShape = shape;
        this.color = COLOR_TABLE[shape.ordinal()];
        
        // Copy the coordinates from the static table
        for (int i = 0; i < 4; i++) {
            System.arraycopy(COORDS_TABLE[shape.ordinal()][i], 0, coords[i], 0, 2);
        }
    }

    /**
     * Sets the piece to a random shape (excluding NoShape).
     */
    public void setRandomShape() {
        Random r = new Random();
        int x = r.nextInt(7) + 1; // Random number from 1 to 7
        setShape(Tetromino.values()[x]);
    }

    // --- Getters ---

    public Tetromino getShape() {
        return pieceShape;
    }

    public Color getColor() {
        return color;
    }

    // Get the x-coordinate of the i-th block
    public int x(int index) {
        return coords[index][0];
    }

    // Get the y-coordinate of the i-th block
    public int y(int index) {
        return coords[index][1];
    }

    // --- Rotation Logic ---

    /**
     * Rotates the piece 90 degrees to the right.
     * Uses a simple matrix rotation: (x, y) -> (-y, x)
     * @return A new Piece object representing the rotated state
     */
    public Piece rotateRight() {
        // The SquareShape does not rotate
        if (pieceShape == Tetromino.SquareShape) {
            return this;
        }

        // Create a new piece for the rotated state
        Piece result = new Piece();
        result.pieceShape = this.pieceShape;
        result.color = this.color;

        // Apply the rotation formula to each block
        for (int i = 0; i < 4; i++) {
            result.coords[i][0] = -y(i);
            result.coords[i][1] = x(i);
        }
        
        return result;
    }

    /**
     * Rotates the piece 90 degrees to the left.
     * Uses a simple matrix rotation: (x, y) -> (y, -x)
     * @return A new Piece object representing the rotated state
     */
    public Piece rotateLeft() {
        if (pieceShape == Tetromino.SquareShape) {
            return this;
        }

        Piece result = new Piece();
        result.pieceShape = this.pieceShape;
        result.color = this.color;
        
        for (int i = 0; i < 4; i++) {
            result.coords[i][0] = y(i);
            result.coords[i][1] = -x(i);
        }
        
        return result;
    }
}
