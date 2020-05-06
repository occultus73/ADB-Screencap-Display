package io.github.occultus73;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class Screencap {
	private static String[] command = {"adb", "shell", "screencap"};
	private static Process screencap;
	private static ProcessBuilder adbBuilder;
	
	private static final int computerScreenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100;
	
	public static BufferedImage getScreenImage() throws Exception {
		adbBuilder = new ProcessBuilder(command);
		screencap = adbBuilder.start();
		InputStream imageStream = screencap.getInputStream();
		
		//Read screencap's 128-bit header
		byte[] widthBytes 		  = new byte[4];
		byte[] heightBytes 		  = new byte[4];
		byte[] pixelType 		  = new byte[4];
		byte[] dontKnowWhatThisIs = new byte[4];
		imageStream.read(widthBytes);
		imageStream.read(heightBytes);
		imageStream.read(pixelType);
		imageStream.read(dontKnowWhatThisIs);
		int phoneWidth = java.nio.ByteBuffer.wrap(widthBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		int phoneHeight = java.nio.ByteBuffer.wrap(heightBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		
		//Read screencap's body: (nPixels = width * height) * Red Green Blue Alpha
		BufferedImage screenImage = new BufferedImage(phoneWidth, phoneHeight, BufferedImage.TYPE_3BYTE_BGR);
		for(int y = 0; y < phoneHeight; y++) {
			for(int x = 0; x < phoneWidth; x++) {
				int red = imageStream.read();
				int green = imageStream.read();
				int blue = imageStream.read();
				@SuppressWarnings("unused")
				int alpha = imageStream.read();

				Color pixelColor = new Color(red,green,blue);
				screenImage.setRGB(x, y, pixelColor.getRGB());
			}
		}
		
		//Code to scale down the image from a smartphone's immense pixel density output.
		final double deviceHeightRatio = (double) ((double) computerScreenHeight / (double) phoneHeight);
		final int scaledWidth = (int) ((double) phoneWidth * deviceHeightRatio);
		BufferedImage scaledScreenImage = new BufferedImage(scaledWidth, computerScreenHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = scaledScreenImage.createGraphics();
        g2d.drawImage(screenImage, 0, 0, scaledWidth, computerScreenHeight, null);
        g2d.dispose();
		
		imageStream.close();
		screencap.destroy();
		return scaledScreenImage;
	}
}
