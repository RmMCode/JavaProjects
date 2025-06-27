import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

public class WhacAMole {
    int boardWidth = 600;
    int boardHeight = 650;

    JFrame frame = new JFrame("Mario: Whac A Mole");
    JLabel scoreLabel = new JLabel();
    JLabel highScoreLabel = new JLabel("High Score: 0");
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;
    JButton resetButton = new JButton("Reset");

    // Keep track of which mole has the Tile or the Plant
    JButton currMoleTile;
    JButton currPlantTile;

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;

    int score = 0;
    int highScore = 0;
    List<JButton> currPlantTiles = new ArrayList<>();

    WhacAMole() {
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // open up window at center of the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        highScoreLabel.setHorizontalAlignment(JLabel.CENTER);

        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(scoreLabel);
        textPanel.add(highScoreLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        frame.add(boardPanel, BorderLayout.CENTER);

        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(resetButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        //plantIcon = new ImageIcon(getClass().getResource("./piranha.png"));
        Image plantImage = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImage.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));

        Image moleImage = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(moleImage.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));

        score = 0;
        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.addActionListener(new TileClickListener(tile));
        }
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        setupTimers();
        resetGame();
        frame.setVisible(true);

        // 1000 ms = 1sec call actionPerformed
        setMoleTimer.start();
        setPlantTimer.start();
        frame.setVisible(true); // Ensure all our compoenents are visible before we load our window
    }

    private class TileClickListener implements ActionListener {
        JButton tile;
        TileClickListener(JButton tile) {
            this.tile = tile;
        }

        public void actionPerformed(ActionEvent e) {
            if (tile == currMoleTile) {
                score += 10;
                updateScoreLabel();
            }
            else if (currPlantTiles.contains(tile)) {
                gameOver();
            }
        }
    }

    private void setupTimers() {
        setMoleTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currMoleTile != null) {
                    currMoleTile.setIcon(null);
                }
                int num = random.nextInt(9);
                JButton tile = board[num];
                if (currPlantTiles.contains(tile)) {
                    return;
                }
                currMoleTile = tile;
                currMoleTile.setIcon(moleIcon);
            }
        });

        setPlantTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JButton tile : currPlantTiles) tile.setIcon(null);
                currPlantTiles.clear();

                // Set multiple plants
                int plantsToShow = 2;
                Set<Integer> chosenIndices = new HashSet<>();

                while (chosenIndices.size() < plantsToShow) {
                    int index = random.nextInt(9);
                    if (board[index] == currMoleTile) {
                        continue;
                    }
                    chosenIndices.add(index);
                }

                for (int index : chosenIndices) {
                    JButton tile = board[index];
                    currPlantTiles.add(tile);
                    tile.setIcon(plantIcon);
                }
            }
        });
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void gameOver() {
        setMoleTimer.stop();
        setPlantTimer.stop();
        if (currMoleTile != null) {
            currMoleTile.setIcon(null);
        }
        for (JButton tile : currPlantTiles) {
            tile.setIcon(null);
        }
        currPlantTiles.clear();
        for (JButton tile : board) {
            tile.setEnabled(false);
        }
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
        scoreLabel.setText("Game Over! Final Score: " + score);
    }

    private void resetGame() {
        // Update high score BEFORE resetting
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }

        // Reset all of the variables
        score = 0;
        updateScoreLabel();
        currMoleTile = null;
        for (JButton tile : board) {
            tile.setEnabled(true);
            tile.setIcon(null);
        }
        currPlantTiles.clear();
        setMoleTimer.start();
        setPlantTimer.start();
    }

    public static void main(String[] args) {
        new WhacAMole();
    }
}