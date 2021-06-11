package graficos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 160;
	private final int HEIGHT = 120;
	private final int SCALE = 3;
	
	private BufferedImage image;
	
	private Spritesheet sheet;
	private BufferedImage[] player;
	private int x=0;
	private int frames = 0;
	private int maxFrames =  17;
	private int curAnimation = 0;
	private int maxAnimation = 5;
	
	public Game() {
		sheet = new Spritesheet("/spritesheet.png");
		player = new BufferedImage[maxAnimation];
		player[0] = sheet.getSprite(0, 0, 16, 16);
		player[1] = sheet.getSprite(16, 0, 16, 16);
		player[2] = sheet.getSprite(32, 0, 16, 16);
		player[3] = sheet.getSprite(48, 0, 16, 16);
		player[4] = sheet.getSprite(64, 0, 16, 16);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
	}
	
	public void initFrame() {
		frame = new JFrame("Game");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tick() {
		frames++;
		if(frames > maxFrames) {
			frames = 0;
			curAnimation++;
			if(curAnimation >= maxAnimation) {
				curAnimation = 0;
			}
		}
		if(curAnimation != 0){
			x++;
		}
	};
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,255,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//Renderização do Jogo
		Graphics2D g2 = (Graphics2D) g;
		//g2.rotate(Math.toRadians(90),90+8,90+8);
		g2.setColor(new Color(0,0,0,180));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(player[curAnimation], x, 50, null);
		g.dispose();
		g.setColor(Color.BLUE);
		g.fillOval(20, 20, 10, 10);
		
		g.setColor(Color.RED);
		g.fillOval(40, 40, 50, 50);
		
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.setColor(Color.white);
		g.drawString("FPS: 60", 10, 10);
		
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show();
	};

	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns= 1000000000 / amountOfTicks;
		double delta  = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime=now;
			if(delta>=1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer+=1000;
			}
		}
		
		stop();
	}
}
