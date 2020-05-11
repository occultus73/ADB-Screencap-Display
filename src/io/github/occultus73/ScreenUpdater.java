package io.github.occultus73;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenUpdater extends ADBCommand implements Runnable {
	private static final String[] SHELLCOMMAND = {"screencap"};
	private static final int COMPUTERSCREENHEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100;
	
	//might in theory be used to terminate the infinite update loop
	static volatile boolean exit = false;
	
	private JLabel label;
	private JFrame frame;
	
	private Process screencap;
	private InputStream imageStream;
	
	private int phoneWidth = 0;
	private int phoneHeight = 0;

	public static ScreenUpdater getInstance(JLabel label, JFrame frame) {
		return new ScreenUpdater(label, frame);
	}
	
	private ScreenUpdater(JLabel label, JFrame frame) {		
		super(SHELLCOMMAND);
		
		this.label = label;
		this.frame = frame;
	}

	//Keep updating phone screen inside infinite loop
	@Override
	public void run() {
		while(!exit) {
			try {
				int initialWidth = phoneWidth;
				
				//update window with new image.
				BufferedImage phoneScreen = getPhoneScreen();
				if(phoneScreen != null) label.setIcon(new ImageIcon(phoneScreen));
				
				//re-pack the window if phone screen image has changed size.
				if(phoneWidth != initialWidth) {
					frame.pack();
					frame.setVisible(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//Execute the Screencap command to get our image...
	private BufferedImage getPhoneScreen() throws Exception {
		screencap = ADBCOMMAND.start();
		imageStream = screencap.getInputStream();
		
		//Read screencap's 128-bit header
		byte[] widthBytes 		  = new byte[4];
		byte[] heightBytes 		  = new byte[4];
		byte[] pixelType 		  = new byte[4];
		byte[] dontKnowWhatThisIs = new byte[4];
		imageStream.read(widthBytes);
		imageStream.read(heightBytes);
		imageStream.read(pixelType);
		imageStream.read(dontKnowWhatThisIs);
		phoneWidth = ByteBuffer.wrap(widthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		phoneHeight = ByteBuffer.wrap(heightBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		//ADB will sometime spit out an empty image, notably on Windows on the first run.
		if(phoneWidth == 0 || phoneHeight == 0) return null;
		
		//Read screencap's body: (nPixels = width * height) of Red Green Blue values, then seek the Alpha=255 (protect against Windows \r insertions)
		BufferedImage screenImage = new BufferedImage(phoneWidth, phoneHeight, BufferedImage.TYPE_3BYTE_BGR);
		for(int y = 0; y < phoneHeight; y++) {
			for(int x = 0; x < phoneWidth; x++) {
				int red = imageStream.read();
				int green = imageStream.read();
				int blue = imageStream.read();
				while(imageStream.read() != 255);

				Color pixelColor = new Color(red,green,blue);
				screenImage.setRGB(x, y, pixelColor.getRGB());
			}
		}
		
		//Code to scale down the image from a smartphone's immense pixel density output.
		final double deviceHeightRatio = (double) ((double) COMPUTERSCREENHEIGHT / (double) phoneHeight);
		final int scaledWidth = (int) ((double) phoneWidth * deviceHeightRatio);
		BufferedImage scaledScreenImage = new BufferedImage(scaledWidth, COMPUTERSCREENHEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = scaledScreenImage.createGraphics();
		g2d.drawImage(screenImage, 0, 0, scaledWidth, COMPUTERSCREENHEIGHT, null);
		g2d.dispose();
		
		imageStream.close();
		screencap.destroy();
		return scaledScreenImage;
	}
	
	//input tap ADB command needs to know phone width & height to translate computer screen clicks back into phone screen taps.
	public int getPhoneWidth() {
		return phoneWidth;
	}

	public int getPhoneHeight() {
		return phoneHeight;
	}
}
