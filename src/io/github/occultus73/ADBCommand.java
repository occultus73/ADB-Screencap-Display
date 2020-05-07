package io.github.occultus73;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ADBCommand {
	//support for windows, which will not have ADB in its environment PATH by default because it's an OS for hacks who love problems.
	private static final boolean ISWINDOWS = System.getProperty("os.name").contains("Windows");
	private static final String WINDOWSPATH = System.getenv("USERPROFILE") + "\\AppData\\Local\\Android\\sdk\\platform-tools\\";
	private static final String[] COMMAND = { ISWINDOWS ? WINDOWSPATH + "adb" : "adb", "shell" };
	
	protected final ProcessBuilder ADBCOMMAND;
	private List<String> adbCommand; 
		
	ADBCommand(String[] shellCommand){
		adbCommand = new ArrayList<String>(Arrays.asList(COMMAND));
		adbCommand.addAll(Arrays.asList(shellCommand));
		
		ADBCOMMAND = new ProcessBuilder(adbCommand);
	}
}
