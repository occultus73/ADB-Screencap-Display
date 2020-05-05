package com.hilles.rayner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class Main {
	
	private static JFrame frame;
	private static JLabel label;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Code to run adb shell screen cap and get its bytestream.
		String[] command = {"adb", "shell", "screencap"};
		Process screencap;
		ProcessBuilder adbBuilder = new ProcessBuilder(command);				
		try {
			screencap = adbBuilder.start();			
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		InputStream imageStream = screencap.getInputStream(); //new FileInputStream(initialFile); 
		
		//Read screencap's 128-bit header
		byte[] widthBytes 		  = new byte[4];
		byte[] heightBytes 		  = new byte[4];
		byte[] pixelType 		  = new byte[4];
		byte[] dontKnowWhatThisIs = new byte[4];
		imageStream.read(widthBytes);
		imageStream.read(heightBytes);
		imageStream.read(pixelType);
		imageStream.read(dontKnowWhatThisIs);
		int width = java.nio.ByteBuffer.wrap(widthBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		int height = java.nio.ByteBuffer.wrap(heightBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		
		//Read screencap's body: (nPixels = width * height) * Red Green Blue Alpha
		BufferedImage screenImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int red = imageStream.read();
				int green = imageStream.read();
				int blue = imageStream.read();
				@SuppressWarnings("unused")
				int alpha = imageStream.read();

				Color pixelColor = new Color(red,green,blue);
				screenImage.setRGB(x, y, pixelColor.getRGB());
			}
		}
		
		imageStream.close();
		screencap.destroy();
		
		//Paint buffer image to window (likely exceed screen size).
		label = new JLabel();
		label.setIcon(new ImageIcon(screenImage));
		
		frame = new JFrame();
		frame.setTitle("stained_image");
		frame.setSize(screenImage.getWidth(), screenImage.getHeight());
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.setLocationRelativeTo(null);
	    frame.pack();
	    frame.setVisible(true);

	}

}
