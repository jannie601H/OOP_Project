import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class AimLabGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CIRCLE_RADIUS = 30;
    	
    private int lives = 3;
    private Timer timer;
    private Random random = new Random();
    private Circle targetCircle;

    public AimLabGame() {
        setTitle("Aim Lab Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initGame();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkHit(e.getX(), e.getY());
            }
        });

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaint();
            }
        });

        timer.start();
    }

    private void initGame() {
        targetCircle = generateRandomCircle();
    }

    private void updateGame() {
        targetCircle.shrink(); // 원의 크기를 감소시킴

        if (targetCircle.getRadius() <= 0) {
            targetCircle = generateRandomCircle(); // 새로운 원 생성
            lives--;

            if (lives == 0) {
                gameOver();
            }
        }
    }

    private void checkHit(int x, int y) {
        if (targetCircle.contains(x, y)) {
            targetCircle = generateRandomCircle(); // 새로운 원 생성
        }
    }

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over");
        System.exit(0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        targetCircle.draw(g);
    }

    private Circle generateRandomCircle() {
        int x = random.nextInt(WIDTH - CIRCLE_RADIUS * 2) + CIRCLE_RADIUS;
        int y = random.nextInt(HEIGHT - CIRCLE_RADIUS * 2) + CIRCLE_RADIUS;
        return new Circle(x, y, CIRCLE_RADIUS);
    }

    private class Circle {
        private int x;
        private int y;
        private int radius;

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }

        public void shrink() {
            radius -= 1; // 크기를 감소시킴
        }

        public boolean contains(int px, int py) {
            return Math.sqrt((px - x) * (px - x) + (py - y) * (py - y)) <= radius;
        }

        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AimLabGame().setVisible(true);
            }
        });
    }
}
