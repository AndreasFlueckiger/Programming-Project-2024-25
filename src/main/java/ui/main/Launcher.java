package main.ui.main;
import javax.swing.UIManager;

import main.ui.initialScreen.InitialFrame;

public class Launcher {
  	public static void main(String[] args) {
		
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	    } catch(Exception ignored){}
		
		InitialFrame mainFrame = InitialFrame.getInitialFrame();
		mainFrame.setVisible(true);

	}  
}
