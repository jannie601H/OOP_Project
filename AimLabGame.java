import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AimLabGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 30;
    private static final double CIRCLE_RADIUS = 10;
    private static final double MAX_RADIUS = 25;
    //게임 화면 크기 및 버튼과 게임에서 사용될 원의 크기를 정의
    
    Font font = new Font("맑은 고딕", Font.BOLD, 19);

    private int highScore = 0;
    private int lives = 3;
    private int score = 0;
    private double level = 1;
    private boolean increased = false;
    private Timer timer;
    private Random random = new Random();
    private Circle targetCircle;
    //목숨 수, 점수, 타이머, 난수 생성과 원 변수 선언
    private JLabel scoreLabel = new JLabel("score: "+Integer.toString(score));
    private JLabel livesLabel = new JLabel("life: "+Integer.toString(lives));
    private JLabel highScoreLabel = new JLabel("high score: "+Integer.toString(highScore));
    private Partition partition = new Partition(WIDTH - 100, 0, WIDTH - 100, HEIGHT); // 세로선 좌표
    // 점수, 목숨을 출력할 JLabel 정의 및 게임화면 분할 Partition 직선 정의
    private MyPanel panel = new MyPanel();
    
    // MouseListener
    MouseListener ml = new MouseAdapter() {
    	@Override
    	public void mousePressed(MouseEvent e) {
    		checkHit(e.getX(), e.getY());
    	}
    };
    
    public AimLabGame() {
        setTitle("Aim Lab Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //게임 창의 제목 및 크기 지정과 창 종료 설정
        setLayout(null);
        //게임 시작 버튼 생성
        JButton startButton = new JButton("Game Start");
        startButton.setBounds((WIDTH - BUTTON_WIDTH) / 2 - 44, (HEIGHT - BUTTON_HEIGHT) / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        //버튼의 위치를 중앙정렬
        setContentPane(panel);
        
        highScoreLabel.setSize(300, 200);
        highScoreLabel.setLocation(WIDTH/2 - 98, HEIGHT/5);
        highScoreLabel.setFont(font);
        add(highScoreLabel);
        // high score 출력
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
                //startGame 메서드를 호출하여 게임을 시작한다.
            }
        });
        
        // 레이아웃 매니저를 사용하지 않음
        setLayout(null);
        add(startButton);
        //버튼 표시
    }
   

    class MyPanel extends JPanel{
        private static final long serialVersionUID = 1L;
        
    	private ImageIcon icon; 
    	private ImageIcon screen;
    	public MyPanel() {
    		loadImageIcon();
    	}
    	public void loadImageIcon() {
    		if(score == 15) {
    			icon = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\gold.jpg");
    			//골드
    		}else if(score == 25 ) {
    			icon = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\diamond.jpg");
    			//다이아
    		}else if(score == 35) {
    			icon = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\master.jpg");
    			//마스터
    		}else if(score == 50) {
    			icon = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\faker.jpg");
    			//이상혁씨
    		}else {
    			icon = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\bronze.jpg");
    			//브론즈
    		}
    		//일정 점수에 도달하면 새로운 이미지를 불러옴.
    		screen = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\bg.jpg");
    		//배경화면 이미지 불러옴
    	}
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		g.drawImage(icon.getImage(),  700, 0, 80,50, this);
    		//티어이미지 위치 지정
    		g.drawImage(screen.getImage(), 0, 0, getWidth()-92, getHeight(), this);
    		//배경이미지 위치 지정
    	}
    	
        public void resetImage() {
        	icon = null;
        	screen = null;
        	loadImageIcon();
        	repaint();
        	//resetGame 시 이미지 초기화
        }
    }
    
    public void changeImage() {
    	panel.loadImageIcon();
    	panel.repaint();
    	//이미지를 바꿀 수 있는 메소드 정의
    }

    private void startGame() {
        getContentPane().removeAll(); // 버튼 제거
        
        // init score Label
        scoreLabel.setSize(100, 200);
        scoreLabel.setLocation(WIDTH - 100, HEIGHT - 150);
        scoreLabel.setFont(font);
        add(scoreLabel);

        // init lives Label
        livesLabel.setSize(100, 200);
        livesLabel.setLocation(WIDTH - 100, HEIGHT - 170);
        livesLabel.setFont(font);
        add(livesLabel);

        // 게임 컴포넌트 초기화
        initGame();
        
        // mouseListener 활성화
        addMouseListener(ml);

        // 게임이 시작되면 타이머 생성
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                //메서드 호출
                repaint();
                //화면을 다시 그리기
            }
        });

        timer.restart();
        generateRandomCircle();
    }

    private void initGame() {
        generateRandomCircle();
    }

    private void updateGame() {
        if (targetCircle != null) {
        	//targetCircle이 null이 아닌 경우에만 수행
            if (!targetCircle.getFlag()) {
                targetCircle.expand(level);
            } else {
                targetCircle.shrink(level);
            }

            if (targetCircle.getRadius() >= MAX_RADIUS) {
                targetCircle.setFlag();
            }

            if (targetCircle.getRadius() <= 0) {
                generateRandomCircle();
                // 새로운 원 생성
                lives--;
                livesLabel.setText("life: " + Integer.toString(lives));
                increased = false;
                // 클릭 실패시 목숨 1감소

                if (lives == 0) {
                    gameOver();
                    // 크기가 0이하가 되면 새로운 원을 생성하고 목숨을 감소
                    // 목숨이 0이 되면 gameOver 메소드를 호출
                }
            }

            if (score > 0 && score % 3 == 0 && !increased) {
                level += 0.1;
                increased = true;
            }

            if (score == 15 || score == 25 || score == 35 || score == 50) {
                changeImage();
            }
            // 일정 점수가 되면 이미지가 바뀌도록 정의
        }
    }

    private void checkHit(int x, int y) {
        if (targetCircle.contains(x, y)) {
            generateRandomCircle();
            //클릭이 원 안에서 이루어 졌는지 확인하고 새로운 원을 생성
            score += 1;
            scoreLabel.setText("score: "+Integer.toString(score));
            increased = false;
            // 클릭 성공시 점수 1증가
        }
    }

    private void gameOver() {
        timer.stop();
        //타이머 정지 게임 루프를 중단시키기 위해 사용
        removeMouseListener(ml);
        // mouseListener 비활성화
        JOptionPane.showMessageDialog(this, "Game Over");
        //다이얼로그 창을 통해 Game Over 메세지를 표시
        highScore = Math.max(highScore, score); //high score update
        resetGame();
    }
    //게임이 종료되었을 때 호출되는 메서드
    
    private void resetGame() {
    	panel.removeAll();
    	setLayout(null);
        //게임 시작 버튼 생성
        JButton startButton = new JButton("Game Start");
        startButton.setBounds((WIDTH - BUTTON_WIDTH) / 2 - 44, (HEIGHT - BUTTON_HEIGHT) / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        //버튼의 위치를 중앙정렬
        setContentPane(panel);
        
        highScoreLabel.setText("high score: "+Integer.toString(highScore));
        highScoreLabel.setSize(300, 200);
        highScoreLabel.setLocation(WIDTH/2 - 98, HEIGHT/5);
        highScoreLabel.setFont(font);
        add(highScoreLabel);
        // high score 출력
         
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
                //startGame 메서드를 호출하여 게임을 시작한다.
            }
        });
        panel.add(startButton);
        
        lives = 3;
        score = 0;
        level = 1.0;
        increased = false;
        //초기로 돌아가도록 설정 초기화
        
        scoreLabel.setText("score: " + Integer.toString(score));
        livesLabel.setText("life: " + Integer.toString(lives));
        panel.resetImage();
        //기록 초기화
        
        panel.repaint();
    	
    }
    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        partition.draw(g);
        if(targetCircle != null) {
        	targetCircle.draw(g);
        }
    }
    //페인트 메소드를 오버라이드하여 화면을 그림

    private void  generateRandomCircle() {
    	SwingWorker<Circle, Void> worker = new SwingWorker<Circle, Void>(){
    		//Override
    		protected Circle doInBackground() {
    			int x = random.nextInt(WIDTH - 100 - (int)MAX_RADIUS * 2) + (int)MAX_RADIUS;
    	        int y = random.nextInt(HEIGHT - 30 - (int)MAX_RADIUS * 2) + (int)MAX_RADIUS + 30;
    	        // circle 좌표 random 생성, 원이 화면 밖으로 나가지 않도록 범위 조정
    	        return new Circle(x, y, CIRCLE_RADIUS);
    		}
    		//Override
    		protected void done() {
                try {
                    targetCircle = get();
                    repaint();
                } 
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
    	};
    	worker.execute();
    }
    //화면 내에 랜덤한 위치에 원을 생성한다.
    //화면 넓이에서 원의 넓이 만큼 뺜 범위 내에서 랜덤한 값을 얻고 원의 반지름 만큼 이동해서 원이 생성되도록 설정한다.

    private class Circle {
        private int x;
        private int y;
        private double radius;
        public boolean flag;
        private ImageIcon circleImage;

        public Circle(int x, int y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.flag = false;
            loadImageCircle();
        }

        public double getRadius() {
            return radius;
        }
        
        public boolean getFlag() {
        	return flag;
        }
        
        public void setFlag() {
        	flag = true;
        }

        public void shrink(double x) {
            radius -= x;
        }
        //원의 크기를 감소시키는 메소드
        public void loadImageCircle() {
        	circleImage = new ImageIcon("C:\\Users\\qkqcu\\Pictures\\Saved Pictures\\target.png");
        }
        
        public void expand(double x) {
        	this.radius += x;
        }

        public boolean contains(int px, int py) {
            return Math.sqrt((px - x) * (px - x) + (py - y) * (py - y)) <= radius;
        }

        public void draw(Graphics g) {
            int xCoord = (int) (x - radius);
            int yCoord = (int) (y - radius);
            int diameter = (int) (radius * 2);
            
            g.drawImage(circleImage.getImage(), xCoord, yCoord, diameter, diameter, null);
        }
      //drawImage()는 int형을 사용하여야 하므로 double형을 int형으로 변환

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
    	SwingUtilities.invokeLater(() -> new AimLabGame().setVisible(true));
    }
}
