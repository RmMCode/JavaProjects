import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating Instance of JPanel
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack(); // get full size of JPanel within window
        pacmanGame.requestFocus();
        frame.setVisible(true);

        // Ensure key focus
        frame.add(pacmanGame);
        frame.pack(); // Ensures correct size
        pacmanGame.requestFocusInWindow();
    }
}