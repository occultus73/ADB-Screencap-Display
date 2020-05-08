package io.github.occultus73;

import java.io.IOException;

public class Click extends ADBCommand {	
	public static void send(int x, int y) {
		String[] shellCommand = { "input", "tap", Integer.toString(x), Integer.toString(y) };
		new Click(shellCommand);
	}

	private Click(String[] shellCommand) {
		super(shellCommand);
		try {
			ADBCOMMAND.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}