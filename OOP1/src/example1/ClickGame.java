package example1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClickGame extends JFrame {

    private int score;
    private JLabel scoreLabel;
    private List<Circle> circles;

    public ClickGame() {
        super("Click Game With Multiple Circles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        // JPanel을 상속한 새로운 클래스를 생성하여 그림을 그립니다.
        ClickPanel clickPanel = new ClickPanel();
        getContentPane().add(clickPanel);

        // 점수를 표시하는 JLabel 생성
        scoreLabel = new JLabel("Score: 0");
        getContentPane().add(scoreLabel, BorderLayout.NORTH);

        // 마우스 이벤트를 처리하는 리스너를 등록합니다.
        clickPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < circles.size(); i++) {
                    Circle circle = circles.get(i);
                    if (circle.contains(e.getPoint())) {
                        circles.remove(i);
                        score++;
                        updateScoreLabel();
                        clickPanel.repaint();
                        break;
                    }
                }
            }
        });

        // 타이머를 사용하여 일정 시간마다 원이 생성되도록 설정
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRandomCircle();
                clickPanel.repaint();
            }
        });
        timer.start();

        circles = new ArrayList<>();
    }

    private void createRandomCircle() {
        Random random = new Random();
        int radius = random.nextInt(30) + 20; // 반지름은 20에서 50까지 랜덤으로 설정
        int x = random.nextInt(getWidth() - 2 * radius) + radius; // x 좌표 랜덤 설정
        int y = random.nextInt(getHeight() - 2 * radius) + radius; // y 좌표 랜덤 설정

        circles.add(new Circle(x, y, radius));
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    // JPanel을 상속하여 원을 그리고 원의 반지름을 관리하는 클래스를 생성합니다.
    private class ClickPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 생성된 원을 그립니다.
            for (Circle circle : circles) {
                circle.draw(g);
            }
        }
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

        public boolean contains(Point point) {
            return Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2) <= Math.pow(radius, 2);
        }

        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClickGame clickGame = new ClickGame();
            clickGame.setVisible(true);
        });
    }
}





