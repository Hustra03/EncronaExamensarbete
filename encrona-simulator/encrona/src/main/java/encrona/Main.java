package encrona;

import encrona.GUIMockup.MockGUIMain;
import encrona.expertSystem.ReasoningEngine;

/**
 * This is the main class, and is what is initially executed when starting the program. 
 */
public class Main {
    /**
     * This is the main method, and what is executed to start the program
     * @param args The provided command-line argumentss
     */
    public static void main(String[] args) {
        MockGUIMain.main(args);
        //new ReasoningEngine().recommendations();
    }


}