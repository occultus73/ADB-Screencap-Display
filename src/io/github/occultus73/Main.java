package io.github.occultus73;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class Main {	
	private static Thread screenUpdater;
	
	private static JFrame frame;
	private static JLabel label;
	
	public static void main(String[] args) {
		
		//Paint buffer image to window.
		label = new JLabel();
		frame = new JFrame();
		
		screenUpdater = new Thread(ScreenUpdater.getInstance(label, frame));
		screenUpdater.start();
		
		frame.setTitle("Phone Screen");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.setLocationRelativeTo(null);
	    frame.pack();
	    frame.setVisible(true);
	}

}
