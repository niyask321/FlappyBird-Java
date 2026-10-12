import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class FlappyBird extends JPanel
        implements ActionListener, KeyListener, MouseListener {

    private final int WIDTH = 400;
    private final int HEIGHT = 600;

    private int birdY = 250;
    private double velocity = 0;
    private final int birdX = 80;
    private final int birdSize = 30;

    private final int pipeWidth = 65;
    private final int pipeGap = 165;
    private int pipeX = WIDTH;
    private int pipeTop;
    private boolean scored = false;

    private int score = 0;
    private boolean started = false;
    private boolean gameOver = false;

    private final Timer timer = new Timer(16, this);
    private final Random random = new Random();

    public FlappyBird() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        resetPipe();
        timer.start();
    }

    private void resetPipe() {
        pipeTop = 70 + random.nextInt(280);
        pipeX = WIDTH;
        scored = false;
    }

    private void flap() {
        if (gameOver) {
            restart();
            return;
        }

        started = true;
        velocity = -7.5;
    }

    private void restart() {
        birdY = 250;
        velocity = 0;
        score = 0;
        started = false;
        gameOver = false;
        resetPipe();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (started && !gameOver) {
            velocity += 0.42;
            birdY += (int) velocity;

            pipeX -= 3;

            if (pipeX + pipeWidth < 0) {
                resetPipe();
            }

            if (!scored && pipeX + pipeWidth < birdX) {
                score++;
                scored = true;
            }

            Rectangle bird = new Rectangle(
                    birdX, birdY, birdSize, birdSize);

            Rectangle topPipe = new Rectangle(
                    pipeX, 0, pipeWidth, pipeTop);

            Rectangle bottomPipe = new Rectangle(
                    pipeX, pipeTop + pipeGap,
                    pipeWidth, HEIGHT - pipeTop - pipeGap);

            if (bird.intersects(topPipe)
                    || bird.intersects(bottomPipe)
                    || birdY < 0
                    || birdY + birdSize >= HEIGHT - 45) {
                gameOver = true;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Sky
        g2.setColor(new Color(110, 200, 245));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Clouds
        g2.setColor(Color.WHITE);
        g2.fillOval(35, 75, 65, 28);
        g2.fillOval(65, 60, 55, 40);
        g2.fillOval(270, 125, 65, 28);
        g2.fillOval(300, 110, 50, 40);

        // Pipes
        g2.setColor(new Color(40, 180, 70));
        g2.fillRect(pipeX, 0, pipeWidth, pipeTop);
        g2.fillRect(pipeX, pipeTop + pipeGap,
                pipeWidth, HEIGHT - pipeTop - pipeGap - 45);

        // Pipe edges
        g2.setColor(new Color(20, 130, 45));
        g2.fillRect(pipeX - 5, pipeTop - 18,
                pipeWidth + 10, 18);
        g2.fillRect(pipeX - 5, pipeTop + pipeGap,
                pipeWidth + 10, 18);

        // Ground
        g2.setColor(new Color(220, 190, 90));
        g2.fillRect(0, HEIGHT - 45, WIDTH, 45);
        g2.setColor(new Color(70, 180, 65));
        g2.fillRect(0, HEIGHT - 45, WIDTH, 9);

        // Bird body
        g2.setColor(Color.YELLOW);
        g2.fillOval(birdX, birdY, birdSize, birdSize);

        // Wing
        g2.setColor(new Color(255, 180, 20));
        g2.fillOval(birdX + 3, birdY + 13, 17, 12);

        // Eye
        g2.setColor(Color.WHITE);
        g2.fillOval(birdX + 17, birdY + 5, 10, 10);
        g2.setColor(Color.BLACK);
        g2.fillOval(birdX + 22, birdY + 8, 4, 4);

        // Beak
        g2.setColor(Color.ORANGE);
        int[] bx = {birdX + 27, birdX + 39, birdX + 27};
        int[] by = {birdY + 14, birdY + 19, birdY + 23};
        g2.fillPolygon(bx, by, 3);

        // Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.drawString(String.valueOf(score), WIDTH / 2 - 10, 55);

        g2.setFont(new Font("Arial", Font.BOLD, 18));

        if (!started && !gameOver) {
            drawCentered(g2, "FLAPPY BIRD", 230);
            drawCentered(g2, "Press SPACE or click to start", 275);
        }

        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(40, 210, WIDTH - 80, 150, 20, 20);

            g2.setColor(Color.WHITE);
            drawCentered(g2, "GAME OVER", 255);
            drawCentered(g2, "Score: " + score, 295);
            g2.setFont(new Font("Arial", Font.PLAIN, 15));
            drawCentered(g2, "Press R or click to restart", 330);
        }
    }

    private void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (WIDTH - fm.stringWidth(text)) / 2, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            flap();
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restart();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        flap();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            FlappyBird game = new FlappyBird();

            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            game.requestFocusInWindow();
        });
    }
}