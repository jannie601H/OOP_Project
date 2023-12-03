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
    private int score = 0;
    private Timer timer;
    private Random random = new Random();
    private Circle targetCircle;
    private JLabel scoreLabel = new JLabel(Integer.toString(score));
    private JLabel livesLabel = new JLabel(Integer.toString(lives));
    private Partition partition = new Partition(700, 0, 700, 600); // 세로선 좌표
   
    public AimLabGame() {
        setTitle("Aim Lab Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        scoreLabel.setSize(100, 200);
        scoreLabel.setLocation(750, 450);
        add(scoreLabel);
        
        livesLabel.setSize(100, 200);
        livesLabel.setLocation(730, 450);
        add(livesLabel);
        
      
        
        
        
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
            lives--; // 목숨 업데이트
            livesLabel.setText(Integer.toString(lives));
            if (lives == 0) {
                gameOver();
            }
        }
    }

    private void checkHit(int x, int y) {
        if (targetCircle.contains(x, y)) {
            targetCircle = generateRandomCircle(); // 새로운 원 생성
            score += 1; // 점수 업데이트
            scoreLabel.setText(Integer.toString(score));
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
        partition.draw(g);
    }

    private Circle generateRandomCircle() {
        int x = random.nextInt(WIDTH - 100 - CIRCLE_RADIUS * 2) + CIRCLE_RADIUS; // adjust 50
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
    
    private class Partition {
    	private int x1;
    	private int y1;
    	private int x2;
    	private int y2;
    	
    	public Partition(int x1, int y1, int x2, int y2) {
    		this.x1 = x1;
    		this.y1 = y1;
    		this.x2 = x2;
    		this.y2 = y2;
    	}
    	
    	public void draw(Graphics g) {
    		g.setColor(Color.BLACK);
    		g.drawLine(x1, y1, x2, y2);
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
