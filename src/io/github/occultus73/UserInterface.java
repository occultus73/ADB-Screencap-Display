package io.github.occultus73;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class UserInterface implements MouseListener{
	private static final int NUMBER_OF_SCREENREADER_THREADS = 1; //Runtime.getRuntime().availableProcessors();
	private static ScreenUpdater[] screenUpdater = new ScreenUpdater[NUMBER_OF_SCREENREADER_THREADS];
	private static Thread[] screenUpdaterThread = new Thread[NUMBER_OF_SCREENREADER_THREADS];
	
	private static JFrame frame = new JFrame();
	private static JLabel label = new JLabel();
	
	//for MouseListener methods: released method needs to know these to calculate the "swipe" to send the phone.
	private static long timePressed = 0;
	private static int pressedX = 0;
	private static int pressedY = 0;
	
	public static void main(String[] args) throws InterruptedException {
		label.addMouseListener(new UserInterface());
		
		//Launch the screenupdater(s) - unfortunately, more threads means slower threads which means greater lag between computer and phone.
		for(int i = 0; i < NUMBER_OF_SCREENREADER_THREADS; i++)
		{
			screenUpdater[i] = ScreenUpdater.getInstance(label, frame);
			screenUpdaterThread[i] = new Thread(screenUpdater[i]);
			screenUpdaterThread[i].start();
			Thread.sleep(1000 / NUMBER_OF_SCREENREADER_THREADS);
		}
		
		frame.setTitle("Phone Screen");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.setLocationRelativeTo(null);
	    frame.pack();
	    frame.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//x,y coordinates of the window mouse-click
		int x = e.getX();
	    int y = e.getY();
	    
	    Click.send(translateXPixel(x), translateYPixel(y));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		timePressed = System.currentTimeMillis();
		pressedX = e.getX();
		pressedY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		long swipeTime = System.currentTimeMillis() - timePressed;
		int releasedX = e.getX();
		int releasedY = e.getY();
		
		Swipe.send(translateXPixel(pressedX), translateYPixel(pressedY), translateXPixel(releasedX), translateYPixel(releasedY), swipeTime);
	}
	
	private int translateXPixel(int x) {
		//calculate ratio to translate X pixel...
		double windowWidth = label.getWidth();
	    double phoneScreenWidth = screenUpdater[0].getPhoneWidth();
	    double screenWidthRatio = phoneScreenWidth / windowWidth;
	    
	    //and the product of the X coordinate with that ratio.
	    return (int)(x * screenWidthRatio);
	}
	
	private int translateYPixel(int y) {
		double windowHeight = label.getHeight();
		double phoneScreenHeight = screenUpdater[0].getPhoneHeight();
		double screenHeightRatio = phoneScreenHeight / windowHeight;
		
		//and the product of the Y coordinate with that ratio.
		return (int)(y * screenHeightRatio);
	}

}
