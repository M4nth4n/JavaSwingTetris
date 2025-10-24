# Java Swing Tetris

A simple, classic clone of the Tetris game built entirely in Java using the Swing library for the GUI. This project is a straightforward example of game loop mechanics, keyboard input, and 2D graphics rendering in Java without any external game engines.



---

## Features

* Classic Tetris gameplay
* All 7 standard Tetromino shapes
* Scoring system based on lines cleared
* Piece rotation and "hard drop"
* Pause and Restart functionality

---

## How to Run

This project consists of three `.java` files that must be in the same directory:

* `Tetris.java` (The main window)
* `Board.java` (The game logic and panel)
* `Piece.java` (The Tetromino class)

### From the Command Line:

1.  **Compile the code:**
    ```sh
    javac Tetris.java Board.java Piece.java
    ```

2.  **Run the game:**
    ```sh
    java Tetris
    ```

### From an IDE (like VS Code, IntelliJ, or Eclipse):

1.  Create a new Java project.
2.  Add all three `.java` files (`Tetris.java`, `Board.java`, `Piece.java`) to the project's `src` directory.
3.  Run the `Tetris.java` file (it contains the `main` method).

---

## Controls

* **Left Arrow / A**: Move piece left
* **Right Arrow / D**: Move piece right
* **Down Arrow / S**: Move piece down (soft drop)
* **Up Arrow / W**: Rotate piece right
* **Spacebar**: Drop piece instantly (hard drop)
* **P**: Pause / Resume game
* **R**: Restart game (only when game is over)
