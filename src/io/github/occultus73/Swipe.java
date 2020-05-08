package io.github.occultus73;

import java.io.IOException;

public class Swipe extends ADBCommand {
	public static void send(int pressedX, int pressedY, int releasedX, int releasedY, long swipeTime) {
		String[] shellCommand = { "input", "swipe", Integer.toString(pressedX), 
													Integer.toString(pressedY),
													Integer.toString(releasedX),
													Integer.toString(releasedY),
													Long.toString(swipeTime) };
		new Swipe(shellCommand);
	}

	private Swipe(String[] shellCommand) {
		super(shellCommand);
		try {
			ADBCOMMAND.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
