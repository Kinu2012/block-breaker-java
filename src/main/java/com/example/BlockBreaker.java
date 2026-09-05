package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// ゲーム状態を定義
enum GameState {
    START,
    PLAYING,
    GAME_OVER,
    GAME_CLEAR
}
class Block {
    int x, y, width, height;
    boolean destroyed = false;

    public Block(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void draw(Graphics g) {
        if (!destroyed) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, width, height); // 枠線
        }
    }
}

public class BlockBreaker extends JPanel implements ActionListener, KeyListener {

    GameState gameState = GameState.START;

    static final int PANEL_WIDTH = 400;
    static final int PANEL_HEIGHT = 300;
    static final int BALL_SIZE = 20;
    static final int PADDLE_Y_OFFSET = 40;

    int ballX = 100, ballY = 200;
    int ballDX = 2, ballDY = -3;
    int paddleX = 150;
    int paddleWidth = 80;
    int paddleHeight = 10;
    int paddleSpeed = 7;
    boolean leftPressed = false;
    boolean rightPressed = false;

    Timer timer;
    List<Block> blocks = new ArrayList<>();

    public BlockBreaker() {
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addKeyListener(this);

        initGame();

        timer = new Timer(10, this);
        timer.start();
    }

    void initGame() {
        // パドル・ボール初期位置
        ballX = 100;
        ballY = 200;
        ballDX = 2;
        ballDY = -3;
        paddleX = 150;
        leftPressed = false;
        rightPressed = false;

        blocks.clear();
        int rows = 5;
        int cols = 8;
        int blockWidth = 40;
        int blockHeight = 20;
        int padding = 5;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 30 + col * (blockWidth + padding);
                int y = 30 + row * (blockHeight + padding);
                blocks.add(new Block(x, y, blockWidth, blockHeight));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameState == GameState.START) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press SPACE to Start", 80, 150);
            return;
        }

        if (gameState == GameState.GAME_OVER) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over", 130, 150);
            g.drawString("Press SPACE to Retry", 100, 180);
            return;
        }

        if (gameState == GameState.GAME_CLEAR) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Clear!", 120, 150);
            g.drawString("Press SPACE to Retry", 80, 180);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ボール
        g2.setColor(Color.RED);
        g2.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // パドル
        g2.setColor(Color.GREEN);
        g2.fillRect(paddleX, getPaddleY(), paddleWidth, paddleHeight);

        // ブロック
        for (Block b : blocks) {
            b.draw(g2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState != GameState.PLAYING) return;

        movePaddle();
        ballX += ballDX;
        ballY += ballDY;

        // 画面端の跳ね返り
        if (ballX < 0) {
            ballX = 0;
            ballDX = -ballDX;
        }
        if (ballX > getWidth() - BALL_SIZE) {
            ballX = getWidth() - BALL_SIZE;
            ballDX = -ballDX;
        }
        if (ballY < 0) {
            ballY = 0;
            ballDY = -ballDY;
        }

        // パドルとの当たり判定
        Rectangle ballRect = new Rectangle(ballX, ballY, BALL_SIZE, BALL_SIZE);
        Rectangle paddleRect = new Rectangle(paddleX, getPaddleY(), paddleWidth, paddleHeight);
        if (ballDY > 0 && ballRect.intersects(paddleRect)) {
            ballY = getPaddleY() - BALL_SIZE;
            ballDY = -Math.abs(ballDY);
        }

        // ブロックとの当たり判定
        for (Block b : blocks) {
            if (!b.destroyed) {
                Rectangle blockRect = new Rectangle(b.x, b.y, b.width, b.height);
                if (ballRect.intersects(blockRect)) {
                    b.destroyed = true;
                    ballDY = -ballDY;
                    break;
                }
            }
        }
        boolean allDestroyed = true;

        for (Block b : blocks) {
            if (!b.destroyed) {
                allDestroyed = false;
                break;
            }
        }

        if (allDestroyed) {
            gameState = GameState.GAME_CLEAR;
        }

        // ゲームオーバー
        if (ballY > getHeight()) {
            gameState = GameState.GAME_OVER;
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameState == GameState.START && key == KeyEvent.VK_SPACE) {
            gameState = GameState.PLAYING;
        }

        if ((gameState == GameState.GAME_OVER || gameState == GameState.GAME_CLEAR)
                && key == KeyEvent.VK_SPACE) {
            initGame();
            gameState = GameState.PLAYING;
            repaint();
        }

        if (gameState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    int getPaddleY() {
        return getHeight() - PADDLE_Y_OFFSET;
    }

    void movePaddle() {
        if (leftPressed) {
            paddleX = Math.max(0, paddleX - paddleSpeed);
        }
        if (rightPressed) {
            paddleX = Math.min(getWidth() - paddleWidth, paddleX + paddleSpeed);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Block Breaker");
            BlockBreaker game = new BlockBreaker();
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
