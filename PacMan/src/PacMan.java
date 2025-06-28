// PacMan class will inherit the JPanel class 
// and add more properties for the actual game
// Displays Inheritance concept in OOP

import java.awt.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.util.HashSet;
import javax.swing.*;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    boolean levelCompleted = false;
    boolean paused = false;
    static int highScore = 0;
    HashSet<Block> powerFoods;
    Block cherry;
    Image powerFoodImage;
    Image scaredGhostImage;
    Image cherryImage;
    boolean powerMode = false;
    int powerTimer = 0; // in frames (50 ms * 100 = 5s)

    // Specify x/y postions and width/height of objects
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        // For restarting game, save original x/y starting positons
        // As the games goes on, the ghost and PacMan will be moving
        // around, hence the postions will be changing.
        int startX;
        int startY;
        char direction = 'U'; // U D L R
        // Not moving at all
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        // Update direction when you press an arrow key
        void updateDirection(char direction) {
            char prevDirection = this.direction; // Store previous direction
            this.direction = direction;
            updateVelocity(); // according to direction
            // Iterate through the walls and make sure that Pacman
            // is able to change directions w/o crashing into wall
            this.x += this.velocityX;
            this.y += this.velocityY;
            // Iterate through all of the walls
            for (Block wall : walls) {
                // If Pacman/Ghost collides with the wall
                if (collision(this, wall)) {
                    this.x -= this.velocityX; // take step back
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                // In every frame, we are going to inch every quarter of the tile size
                this.velocityY = -tileSize/4; // 32 pixels / 4 = 8 pixels
            }
            else if (this.direction == 'D') {
                this.velocityX = 0; // Going down
                this.velocityY = tileSize/4; // We are going to the 20th row
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4; // Moving towards the 0th column
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        // Reset Function
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
     }

    // Representation of objects in the Game
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    // Make the Ghost move around randomly
    // Up, Down, Left, Right
    char[] directions = {'U', 'D', 'L', 'R'}; 
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    // X = wall, O = skip, P = pac man, ' ' = food
    // New/Updated: C = power pellet, H = cherry
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        C        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P    H X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        // Make sure that JPanel is listening for the key presses
        setFocusable(true);

        // Load up all of the images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();

        loadMap(); // 1000 ms in 1s, so 1000/50 = 20fps

        // for each ghost, we are going to randomly select direction
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)]; // 0,1,2,3
            ghost.updateDirection(newDirection); // Update velocity for each ghost
        } 

        // every 50 ms, we will call actionPerformed, which will call repaint
        gameLoop = new Timer(50, this); // delay, PacMan Object
        gameLoop.start();

        // The following were used for testing
        /*System.out.println(walls.size());
        System.out.println(foods.size());
        System.out.println(ghosts.size());*/

        // Whitespace => food ; 0 => no food, it is empty
        // Iterate through the tiles and create objects 
        // Use a Hash Set, instead of arrays, because when you check for a collision,
        // it is easier to check for an hash set instead of an array list, and look up
        // for different and unique values, which means that you can't have duplicates
    }

    // Go through the tile maps, and create the objects for walls, foods, ghosts, and PacMan
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        powerFoods = new HashSet<Block>();
        cherry = null;
        
        // Iterate through the map
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r]; // Get the current row
                char tileMapChar = row.charAt(c); // Get current character

                // Figure out where the tile actually is
                int x = c*tileSize;
                int y = r*tileSize;

                // We have a wall block
                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                // If it's not an X - blue ghost
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } 
                else if (tileMapChar == 'o') { // Orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } 
                else if (tileMapChar == 'p') { // Pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } 
                else if (tileMapChar == 'r') { // Red ghost 
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } 
                else if (tileMapChar == 'P') { // PacMan
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') { // food
                    Block food  = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if (tileMapChar == 'C') {
                    Block pellet = new Block(powerFoodImage, x + 8, y + 8, 16, 16);
                    powerFoods.add(pellet);
                } 
                else if (tileMapChar == 'H') { // H = cherry
                    cherry = new Block(cherryImage, x + 4, y + 4, 24, 24);
                }
            }
        }
    }

    // Draw all of our objects into the game
    public void paintComponent(Graphics g) {
        // Invoke the function of the same 
        // name from the JPanel
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //g.fillRect(pacman.x, pacman.y, pacman.width, pacman.height);
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Update/add score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else { // Game's not over yet
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }

        // Draw cherry if available
        if (cherry != null) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
        // Draw power pellets
        for (Block pellet : powerFoods) {
            g.drawImage(pellet.image, pellet.x, pellet.y, pellet.width, pellet.height, null);
        }
        // Draw high score
        g.setColor(Color.YELLOW);
        g.drawString("High Score: " + highScore, tileSize * 12, tileSize / 2);

        // Print "Paused" message in the center if paused
        if (paused && !gameOver) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw semi-transparent dark rectangle over entire screen
            g2d.setColor(new Color(0, 0, 0, 150)); // black with alpha 150
            g2d.fillRect(0, 0, boardWidth, boardHeight);

            // Draw semi-transparent dark overlay for LEVEL COMPLETED! message
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, boardWidth, boardHeight);
            
            // Draw paused text
            String pausedMessage = "Paused, press 'P' again to continue";
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.setColor(Color.WHITE);
            FontMetrics f1 = g2d.getFontMetrics();
            int textWidth1 = f1.stringWidth(pausedMessage);
            int x1 = (boardWidth - textWidth1) / 2;
            int y1 = boardHeight / 2;
            g2d.drawString(pausedMessage, x1, y1);

            // Draw LEVEL COMPLETED! message
            String levelMessage2 = "LEVEL COMPLETED!";
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            g2d.setColor(Color.GREEN);
            FontMetrics f2 = g2d.getFontMetrics();
            int textWidth2 = f2.stringWidth(levelMessage2);
            int x2 = (boardWidth - textWidth2) / 2;
            int y2 = boardHeight / 2;
            g2d.drawString(levelMessage2, 2, y2);
        }
    }

    // Updating the actual PacMan
    public void move() {
        // One of the following will always be zero
        // Just add both to x and y so we don't have
        // to make any changes/checks
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                // Undo everything
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Check wall collisions
        // Handle portal teleportation for Pac-Man
        if (pacman.x < -tileSize) {
            pacman.x = boardWidth;
        }
        else if (pacman.x > boardWidth) {
            pacman.x = -tileSize;
        }

        // Move the ghosts
        // Check ghost collisions
        for (Block ghost : ghosts) {
            // Check to see if the current ghost has collided with PacMan
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            // before we move the ghosts, we will check to see if the ghost is on the 9th row
            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                // ghost is moving left/right, so it's stuck on that row, hence we will force it to go up
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                // If the ghost hits the left/right boundary, it will stop and change directions
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    // move a step forward
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    // move a step back and change directions
                    // If ghost collides into the wall, we
                    // want it to change directions immediately
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                    break;
                }
            }
        }

        // Handle portal teleportation (Ghosts)
        for (Block ghost : ghosts) {
            if (ghost.x < -tileSize) {
                ghost.x = boardWidth;
            }
            else if (ghost.x > boardWidth) {
                ghost.x = -tileSize;
            }
        }

        // Check food collision
        Block foodEaten  = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        // Remove food eaten from HashSet
        foods.remove(foodEaten);

        // PacMan has eaten all of the food
        if (foods.isEmpty()) {
            levelCompleted = true;
            paused = true; // Pasue game temporarily
        }

        // POWER PELLET COLLISION
        Block powerEaten = null;
        for (Block pellet : powerFoods) {
            if (collision(pacman, pellet)) {
                powerEaten = pellet;
                powerMode = true;
                powerTimer = 100; // 5 seconds
            }
        }
        powerFoods.remove(powerEaten);

        // CHERRY COLLISION
        if (cherry != null && collision(pacman, cherry)) {
            score += 100;
            cherry = null; // Removes cherry
        }

        // Update ghost appearance if scared
        if (powerMode) {
            for (Block ghost : ghosts) {
                ghost.image = scaredGhostImage;
            }
        }
        if (levelCompleted) {
            // Wait 2 seconds before resetting (40 frames)
            if (powerTimer == 0) {
                powerTimer = 40;
            } 
            else {
                powerTimer--;
                if (powerTimer <= 0) {
                    levelCompleted = false;
                    paused = false;
                    loadMap();
                    resetPositions();
                }
            }
        }
        else {
            for (Block ghost : ghosts) {
                // restore the original image
                if (ghost.startX == tileSize * 8) {
                    ghost.image =  blueGhostImage;
                }
                else if (ghost.startX == tileSize * 10) {
                    ghost.image = pinkGhostImage;
                }
                else if (ghost.startX == tileSize * 9) {
                    ghost.image =  orangeGhostImage;
                }
                else {
                    ghost.image = redGhostImage;
                }
            }
        }
        if (score > highScore) {
            highScore = score;
        }
    }

    // Detect collisions b/w PacMan/Ghost, and
    // PacMan/Wall, Ghost/Wall, PacMan/Food
    public boolean collision(Block a, Block b) {
        // Every image is a square/rectangle
        return a.x < b.x + b.width && a.x + a.width > b.x &&
        a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset(); // Sets x/y positions back to starting positions
        // Don't want PacMan to move until the players clicks on an arrow key
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    // Used for our game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        // Adding Pause Logic
        if (!paused && !gameOver) {
            if (!paused) {
                move();
            }
            // Velocity is tileSize/4, so in every frame, we expect
            // PacMan to move one quarter of the tile size
            // Always repaint, even if paused
            repaint(); // Call paint component
            if (powerMode) {
                powerTimer--;
                if (powerTimer <= 0) {
                    powerMode = false;
                }
            }
        }
        else if (gameOver) {
            gameLoop.stop(); // Game over stops the loop
        }
    }

    // Type on key with corresponding character, if I press arrow key, nothing happens
    // because arrow key doesn't give us characters when we are writing
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // When you press on or hold on to any key to trigger the function
    @Override
    public void keyPressed(KeyEvent e) {
    }

    // Only triggers functon if we press a key and let go/release the key
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (gameOver) {
            loadMap(); // Reload the map to add food back
            resetPositions(); // Give each ghost a new direction
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
            return;
        }
        
        // Pause/Unpause with P
        if (key == KeyEvent.VK_P) {
            paused = !paused;
            return;
        }
        
        // Reset anytime with R
        if (key == KeyEvent.VK_R) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            powerMode = false;
            powerTimer = 0;
            paused = false;
            repaint();
            return;
        }
        
        // Movement controls (only if not paused)
        if (!paused) {
            if (key == KeyEvent.VK_UP) {
                pacman.updateDirection('U');
                pacman.image = pacmanUpImage;
            } 
            else if (key == KeyEvent.VK_DOWN) {
                pacman.updateDirection('D');
                pacman.image = pacmanDownImage;
            } 
            else if (key == KeyEvent.VK_LEFT) {
                pacman.updateDirection('L');
                pacman.image = pacmanLeftImage;
            } 
            else if (key == KeyEvent.VK_RIGHT) {
                pacman.updateDirection('R');
                pacman.image = pacmanRightImage;
            }
        }
    }
}