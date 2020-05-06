package io.github.occultus73;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class Main {
	private static BufferedImage screenImage;
	
	private static JFrame frame;
	private static JLabel label;
	
	public static void main(String[] args) {
		
		try {
			screenImage = Screencap.getScreenImage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(screenImage == null) return;
		}
		
		//Paint buffer image to window.
		label = new JLabel();
		label.setIcon(new ImageIcon(screenImage));
		
		frame = new JFrame();
		frame.setTitle("Screen");
		frame.setSize(screenImage.getWidth(), screenImage.getHeight());
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.setLocationRelativeTo(null);
	    frame.pack();
	    frame.setVisible(true);
	}

}
